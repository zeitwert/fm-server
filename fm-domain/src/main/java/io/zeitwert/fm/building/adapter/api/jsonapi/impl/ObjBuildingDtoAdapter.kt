package io.zeitwert.fm.building.adapter.api.jsonapi.impl

import dddrive.ddd.core.model.RepositoryDirectory
import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.ReadableMap
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.DtoUtils
import io.zeitwert.fm.building.adapter.api.jsonapi.dto.ObjBuildingDto
import io.zeitwert.fm.building.model.ObjBuilding
import io.zeitwert.fm.building.model.ObjBuildingPartElementRating
import io.zeitwert.fm.building.model.ObjBuildingPartRating
import io.zeitwert.fm.building.model.enums.CodeBuildingPart
import io.zeitwert.fm.building.model.enums.CodeBuildingRatingStatus
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoAdapterBase
import io.zeitwert.fm.oe.adapter.api.jsonapi.impl.ObjUserDtoAdapter
import org.springframework.stereotype.Component

@Component("objBuildingDtoAdapter")
class ObjBuildingDtoAdapter(
	directory: RepositoryDirectory,
	private val sessionContext: SessionContext,
	private val userDtoAdapter: ObjUserDtoAdapter,
) : ObjDtoAdapterBase<ObjBuilding, ObjBuildingDto>(directory, { ObjBuildingDto() }) {

	companion object {

		const val ADD_RATING_OPERATION = "addRating"
	}

	init {
		config.exclude("currentRating") // Handled via custom field (computed property)
		config.exclude("ratingList") // Not exposed via API
		config.relationshipSet("contactIds", "contact", "contactSet")
		config.relationship("coverFotoId", "document", "coverFoto")

		// Custom field for currentRating (computed property on aggregate)
		config.field(
			"currentRating",
			outgoing = { entity -> buildCurrentRatingMap(entity as ObjBuilding) },
			incoming = { value, entity -> applyCurrentRating(value, entity as ObjBuilding) },
		)

		// Configure part adapter for ObjBuildingPartRating
		config.partAdapter(ObjBuildingPartRating::class.java) {
			exclude("ratingUser") // Handled via custom field (needs EnumeratedDto conversion)
			field("ratingUser", { part ->
				userDtoAdapter.asEnumerated((part as ObjBuildingPartRating).ratingUser)
			})
			field("elements", "elementList")
		}

		// Configure part adapter for ObjBuildingPartElementRating
		config.partAdapter(ObjBuildingPartElementRating::class.java) {
			// Add computed fields for restoration and lifetime calculations
			field("restorationYear", { part -> calculateRestorationYear(part as ObjBuildingPartElementRating) })
			field("restorationCosts", { part -> calculateRestorationCosts(part as ObjBuildingPartElementRating) })
			field("lifeTime20", { part -> (part as ObjBuildingPartElementRating).buildingPart?.getLifetime(0.2) })
			field("lifeTime50", { part -> (part as ObjBuildingPartElementRating).buildingPart?.getLifetime(0.5) })
			field("lifeTime70", { part -> (part as ObjBuildingPartElementRating).buildingPart?.getLifetime(0.7) })
			field("lifeTime85", { part -> (part as ObjBuildingPartElementRating).buildingPart?.getLifetime(0.85) })
			field("lifeTime95", { part -> (part as ObjBuildingPartElementRating).buildingPart?.getLifetime(0.95) })
			field("lifeTime100", { part -> (part as ObjBuildingPartElementRating).buildingPart?.getLifetime(1.0) })
		}
	}

	private fun isActiveRating(rating: ObjBuildingPartRating): Boolean = rating.ratingStatus == null || rating.ratingStatus != CodeBuildingRatingStatus.DISCARD

	/**
	 * Calculate restoration year for an element rating.
	 */
	private fun calculateRestorationYear(element: ObjBuildingPartElementRating): Int? {
		val building = element.meta.aggregate
		if (building.insuredValue == null) return null

		val weight = element.weight
		val condition = element.condition
		val ratingYear = element.ratingYear
		val buildingPart = element.buildingPart

		if (weight == null || weight <= 0 || condition == null || ratingYear == null || buildingPart == null) {
			return null
		}

		val renovationPeriod = buildingPart.getNextRestoration(1000000.0, ratingYear, condition.toDouble())
		return renovationPeriod?.year
	}

	/**
	 * Calculate restoration costs for an element rating.
	 */
	private fun calculateRestorationCosts(element: ObjBuildingPartElementRating): Double? {
		val building = element.meta.aggregate
		if (building.insuredValue == null) return null

		val weight = element.weight
		val condition = element.condition
		val ratingYear = element.ratingYear
		val buildingPart = element.buildingPart

		if (weight == null || weight <= 0 || condition == null || ratingYear == null || buildingPart == null) {
			return null
		}

		val renovationPeriod = buildingPart.getNextRestoration(1000000.0, ratingYear, condition.toDouble())
			?: return null

		val restorationYear = renovationPeriod.year
		val elementValue = weight / 100.0 * building.getBuildingValue(restorationYear) / 1000.0
		return Math.round(renovationPeriod.restorationCosts / 1000000.0 * elementValue).toDouble()
	}

	/**
	 * Build the currentRating map for JSON serialization.
	 * Uses the generic fromPart() and adds the seqNr computed field.
	 */
	private fun buildCurrentRatingMap(building: ObjBuilding): Map<String, Any?>? {
		val rating = building.currentRating ?: return null

		// Use generic part serialization as base
		val map = fromPart(rating).toMutableMap()

		// Add seqNr (read-only calculated field: count of active ratings - 1)
		map["seqNr"] = building.ratingList.count { isActiveRating(it) } - 1

		return map
	}

	/**
	 * Apply currentRating from DTO to aggregate.
	 * New ratings are only created via explicit AddRatingOperation.
	 * Otherwise, updates are applied to the existing currentRating.
	 */
	@Suppress("UNCHECKED_CAST")
	private fun applyCurrentRating(
		value: Any?,
		building: ObjBuilding,
	) {
		val ratingMap = value as? Map<String, Any?> ?: return
		val rating = building.currentRating ?: return

		// Use generic part deserialization for the rating
		toPart(ReadableMap(ratingMap), rating)

		// Handle ratingUser specially (needs to load from repository)
		val ratingUserMap = ratingMap["ratingUser"] as? Map<String, Any?>
		if (ratingUserMap != null) {
			val userId = DtoUtils.idFromString(ratingUserMap["id"] as? String)
			rating.ratingUser = if (userId != null) userRepository.get(userId) else null
		}

		// Apply elements
		val elements = ratingMap["elements"] as? List<Map<String, Any?>>
		elements?.forEach { elementMap ->
			val element = findElement(rating, elementMap)
			if (element != null) {
				toPart(ReadableMap(elementMap), element)

				// Set ratingYear from rating date if not provided
				if (element.ratingYear == null && rating.ratingDate != null) {
					element.ratingYear = rating.ratingDate!!.year
				}
			}
		}
	}

	@Suppress("UNCHECKED_CAST")
	private fun findElement(
		rating: ObjBuildingPartRating,
		elementMap: Map<String, Any?>,
	): ObjBuildingPartElementRating? {
		val partId = when (val id = elementMap["id"]) {
			is Number -> id.toInt()
			is String -> id.toIntOrNull()
			else -> null
		}

		return if (partId != null) {
			rating.elementList.getById(partId)
		} else {
			val buildingPartMap = elementMap["buildingPart"] as? Map<String, Any?>
			if (buildingPartMap != null) {
				val buildingPart = CodeBuildingPart.Enumeration.getBuildingPart(buildingPartMap["id"] as? String)
				if (buildingPart != null) rating.getElement(buildingPart) else null
			} else {
				null
			}
		}
	}

	override fun toAggregate(
		dto: ObjBuildingDto,
		aggregate: ObjBuilding,
	) {
		try {
			aggregate.meta.disableCalc()
			// Handle AddRatingOperation before generic processing
			if (dto.hasOperation(ADD_RATING_OPERATION)) {
				aggregate.addRating(sessionContext.user, sessionContext.currentTime)
			} else {
				super.toAggregate(dto, aggregate)
			}
		} finally {
			aggregate.meta.enableCalc()
			aggregate.meta.calcAll()
		}
	}

}
