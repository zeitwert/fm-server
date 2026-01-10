package io.zeitwert.data.dsl

import dddrive.ddd.model.RepositoryDirectory
import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.account.model.enums.CodeCurrency
import io.zeitwert.fm.building.model.ObjBuilding
import io.zeitwert.fm.building.model.ObjBuildingRepository
import io.zeitwert.fm.building.model.enums.CodeBuildingMaintenanceStrategy
import io.zeitwert.fm.building.model.enums.CodeBuildingPart
import io.zeitwert.fm.building.model.enums.CodeBuildingPartCatalog
import io.zeitwert.fm.building.model.enums.CodeBuildingRatingStatus
import io.zeitwert.fm.building.model.enums.CodeBuildingSubType
import io.zeitwert.fm.building.model.enums.CodeBuildingType
import io.zeitwert.fm.building.service.api.BuildingService
import io.zeitwert.fm.oe.model.ObjUser
import io.zeitwert.fm.oe.model.ObjUserRepository
import io.zeitwert.fm.oe.model.enums.CodeCountry
import org.jooq.DSLContext
import java.math.BigDecimal
import java.time.LocalDate
import java.time.OffsetDateTime

/**
 * DSL for creating buildings with nested ratings using Spring repositories.
 *
 * This DSL uses the repository layer to create buildings and ratings, which ensures proper domain
 * logic is executed (e.g., creating cover photos, setting account associations, etc.).
 *
 * Usage:
 * ```
 * Building.init(dslContext, directory)
 * Building("Schulhaus", "Schulstrasse 1", "8000", "Zürich") {
 *     buildingNr = "01.01"
 *     buildingType = "T02"
 *     buildingSubType = "ST02-12"
 *     partCatalog = "C9"
 *     buildingYear = 1960
 *     volume = 5000
 *     insuredValue = 3000
 *     insuredValueYear = 2010
 *     maintenanceStrategy = "N"
 *     description = "Primary school building"
 *
 *     rating(2020) {
 *         element("P48", 40, 85, "Concrete structure")
 *         element("P2", 8, 90, "Tile roof")
 *     }
 * }
 * ```
 */
object Building {

	private const val DefaultGeoZoom = 17

	lateinit var dslContext: DSLContext
	lateinit var directory: RepositoryDirectory
	lateinit var buildingService: BuildingService

	val buildingRepository: ObjBuildingRepository
		get() = directory.getRepository(ObjBuilding::class.java) as ObjBuildingRepository

	val userRepository: ObjUserRepository
		get() = directory.getRepository(ObjUser::class.java) as ObjUserRepository

	fun init(
		dslContext: DSLContext,
		directory: RepositoryDirectory,
		buildingService: BuildingService,
	) {
		this.dslContext = dslContext
		this.directory = directory
		this.buildingService = buildingService
	}

	operator fun invoke(
		userId: Any,
		account: ObjAccount,
		name: String,
		street: String,
		zip: String,
		city: String,
		init: BuildingContext.() -> Unit = {},
	): Int {
		val context = BuildingContext(userId, name, street, zip, city).apply(init)
		return createBuilding(account, context)
	}

	private fun createBuilding(
		account: ObjAccount,
		ctx: BuildingContext,
	): Int {
		// Create new building via repository
		val building = buildingRepository.create()
		building.accountId = account.id
		building.name = ctx.name
		building.street = ctx.street
		building.zip = ctx.zip
		building.city = ctx.city
		building.country = CodeCountry.CH
		building.currency = CodeCurrency.CHF

		// Set optional properties
		ctx.buildingNr?.let { building.buildingNr = it }
		ctx.description?.let { building.description = it }
		ctx.buildingYear?.let { building.buildingYear = it }
		ctx.volume?.let { building.volume = BigDecimal(it) }
		ctx.areaGross?.let { building.areaGross = BigDecimal(it) }
		ctx.insuredValue?.let { building.insuredValue = BigDecimal(it) }
		ctx.insuredValueYear?.let { building.insuredValueYear = it }

		// Set enum properties
		ctx.buildingType?.let { building.buildingType = CodeBuildingType.getBuildingType(it) }
		ctx.buildingSubType?.let { building.buildingSubType = CodeBuildingSubType.getBuildingSubType(it) }

		// Calculate and set geo-coordinates
		val address = "${ctx.street}, ${ctx.zip} ${ctx.city}, ${building.country?.name ?: "CH"}"
		val coordinates = buildingService.getCoordinates(address)
		if (coordinates != null) {
			building.geoCoordinates = coordinates
			building.geoZoom = DefaultGeoZoom
		}

		// Create rating if specified
		ctx.ratingContext?.let { ratingCtx ->
			createRating(building, ratingCtx)
		}

		val buildingId = building.id as Int
		println("      Created building ${ctx.name} at ${ctx.street}, ${ctx.zip} ${ctx.city} (id=$buildingId, geo=$coordinates)")

		return buildingId
	}

	private fun createRating(
		building: ObjBuilding,
		ctx: RatingContext,
	) {
		// Add rating to building
		val rating = building.addRating(ctx.userId, OffsetDateTime.now())

		// Set rating properties
		rating.ratingDate = LocalDate.of(ctx.ratingYear, 1, 1)
		rating.ratingStatus = CodeBuildingRatingStatus.DONE

		// Set part catalog and maintenance strategy
		ctx.partCatalog?.let { rating.partCatalog = CodeBuildingPartCatalog.getPartCatalog(it) }
		ctx.maintenanceStrategy?.let {
			rating.maintenanceStrategy = CodeBuildingMaintenanceStrategy.getMaintenanceStrategy(it)
		}

		// Add elements - first clear any auto-generated elements if we have custom ones
		if (ctx.elements.isNotEmpty()) {
			rating.elementList.clear()

			// Add custom elements
			for (elementCtx in ctx.elements) {
				val element = rating.addElement(CodeBuildingPart.getBuildingPart(elementCtx.part)!!)
				element.weight = elementCtx.weight
				element.condition = elementCtx.condition
				element.ratingYear = elementCtx.ratingYear ?: ctx.ratingYear
				elementCtx.description?.let { element.description = it }
			}
		}

		dslContext.transaction { _ ->
			buildingRepository.store(building)
		}

		println("        Added rating for year ${ctx.ratingYear} with ${ctx.elements.size} elements")
	}
}

@TenantDslMarker
class BuildingContext(
	val userId: Any,
	val name: String,
	val street: String,
	val zip: String,
	val city: String,
) {

	var buildingNr: String? = null
	var description: String? = null
	var buildingType: String? = null
	var buildingSubType: String? = null
	var buildingYear: Int? = null
	var volume: Int? = null
	var areaGross: Int? = null
	var insuredValue: Int? = null
	var insuredValueYear: Int? = null

	internal var ratingContext: RatingContext? = null

	/**
	 * Add a rating to this building.
	 *
	 * @param ratingYear The year of the rating
	 * @param partCatalog The part catalog code (e.g., "C9")
	 * @param maintenanceStrategy The maintenance strategy code (e.g., "N", "NW")
	 * @param init Lambda to configure the rating elements
	 */
	fun rating(
		ratingYear: Int,
		partCatalog: String? = null,
		maintenanceStrategy: String? = null,
		init: RatingContext.() -> Unit = {},
	) {
		ratingContext = RatingContext(userId, ratingYear, partCatalog, maintenanceStrategy).apply(init)
	}

	/**
	 * Copy properties from another BuildingContext.
	 * Used internally when creating buildings from AccountContext.
	 */
	internal fun copyFrom(other: BuildingContext) {
		buildingNr = other.buildingNr
		description = other.description
		buildingType = other.buildingType
		buildingSubType = other.buildingSubType
		buildingYear = other.buildingYear
		volume = other.volume
		areaGross = other.areaGross
		insuredValue = other.insuredValue
		insuredValueYear = other.insuredValueYear
		ratingContext = other.ratingContext
	}
}

@TenantDslMarker
class RatingContext(
	val userId: Any,
	val ratingYear: Int,
	val partCatalog: String?,
	val maintenanceStrategy: String?,
) {

	internal val elements = mutableListOf<ElementContext>()

	/**
	 * Add an element rating.
	 *
	 * @param part The building part code (e.g., "P48", "P2")
	 * @param weight The weight percentage (0-100)
	 * @param condition The condition percentage (0-100)
	 * @param description Optional description
	 * @param ratingYear Optional rating year override (defaults to rating's year)
	 */
	fun element(
		part: String,
		weight: Int,
		condition: Int,
		description: String? = null,
		ratingYear: Int? = null,
	) {
		elements += ElementContext(part, weight, condition, description, ratingYear)
	}
}

@TenantDslMarker
class ElementContext(
	val part: String,
	val weight: Int,
	val condition: Int,
	val description: String?,
	val ratingYear: Int?,
)
