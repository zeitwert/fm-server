package io.zeitwert.fm.building.adapter.api.jsonapi.impl

import dddrive.ddd.core.model.RepositoryDirectory
import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.fm.building.adapter.api.jsonapi.dto.ObjBuildingDto
import io.zeitwert.fm.building.model.ObjBuilding
import io.zeitwert.fm.building.model.ObjBuildingPartElementRating
import io.zeitwert.fm.building.model.ObjBuildingPartRating
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoAdapterBase
import org.springframework.stereotype.Component

@Component("objBuildingDtoAdapter")
class ObjBuildingDtoAdapter(
	directory: RepositoryDirectory,
	private val sessionContext: SessionContext,
) : ObjDtoAdapterBase<ObjBuilding, ObjBuildingDto>(
		ObjBuilding::class.java,
		"building",
		ObjBuildingDto::class.java,
		directory,
		{ ObjBuildingDto() },
	) {

	companion object {

		const val ADD_RATING_OPERATION = "addRating"
	}

	init {
		config.exclude("ratingList") // Not exposed via API
		config.relationshipMany("contacts", "contact", "contactSet")
		config.relationship("coverFoto", "document", "coverFoto")

		// Inline the full part content instead of just the ID
		config.field("currentRating", doInline = true)

		config.partAdapter(ObjBuildingPartRating::class.java) {
			field("ratingUser")
			field("elements", "elementList")
		}

		config.partAdapter(ObjBuildingPartElementRating::class.java) {
			field("restorationYear", { (it as ObjBuildingPartElementRating).restorationYear() })
			field("restorationCosts", { (it as ObjBuildingPartElementRating).restorationCosts() })
			field("lifeTime20", { (it as ObjBuildingPartElementRating).buildingPart?.getLifetime(0.2) })
			field("lifeTime50", { (it as ObjBuildingPartElementRating).buildingPart?.getLifetime(0.5) })
			field("lifeTime70", { (it as ObjBuildingPartElementRating).buildingPart?.getLifetime(0.7) })
			field("lifeTime85", { (it as ObjBuildingPartElementRating).buildingPart?.getLifetime(0.85) })
			field("lifeTime95", { (it as ObjBuildingPartElementRating).buildingPart?.getLifetime(0.95) })
			field("lifeTime100", { (it as ObjBuildingPartElementRating).buildingPart?.getLifetime(1.0) })
		}
	}

	private fun ObjBuildingPartElementRating.restorationYear(): Int? {
		val building = meta.aggregate
		if (building.insuredValue == null) return null
		if (weight == null ||
			weight!! <= 0 ||
			condition == null ||
			ratingYear == null ||
			buildingPart == null
		) {
			return null
		}
		val renovationPeriod =
			buildingPart!!.getNextRestoration(1000000.0, ratingYear!!, condition!!.toDouble())
		return renovationPeriod?.year
	}

	private fun ObjBuildingPartElementRating.restorationCosts(): Double? {
		val building = meta.aggregate
		if (building.insuredValue == null) return null
		if (weight == null ||
			weight!! <= 0 ||
			condition == null ||
			ratingYear == null ||
			buildingPart == null
		) {
			return null
		}
		val renovationPeriod =
			buildingPart!!.getNextRestoration(1000000.0, ratingYear!!, condition!!.toDouble())
				?: return null
		val restorationYear = renovationPeriod.year
		val elementValue = weight!! / 100.0 * building.getBuildingValue(restorationYear) / 1000.0
		return Math.round(renovationPeriod.restorationCosts / 1000000.0 * elementValue).toDouble()
	}

	override fun toAggregate(
		dto: ObjBuildingDto,
		aggregate: ObjBuilding,
	) {
		try {
			aggregate.meta.disableCalc()
			// Handle AddRatingOperation before generic processing
			if (dto.hasOperation(ADD_RATING_OPERATION)) {
				aggregate.addRating(userRepository.get(sessionContext.userId), sessionContext.currentTime)
			} else {
				super.toAggregate(dto, aggregate)
			}
		} finally {
			aggregate.meta.enableCalc()
			aggregate.meta.calcAll()
		}
	}

}
