package io.zeitwert.fm.building.model.base

import io.dddrive.ddd.model.Part
import io.dddrive.ddd.model.PartMeta
import io.dddrive.ddd.model.PartRepository
import io.dddrive.obj.model.base.ObjPartBase
import io.dddrive.oe.model.ObjUser
import io.dddrive.property.model.Property
import io.zeitwert.fm.building.model.ObjBuilding
import io.zeitwert.fm.building.model.ObjBuildingPartElementRating
import io.zeitwert.fm.building.model.ObjBuildingPartRating
import io.zeitwert.fm.building.model.enums.CodeBuildingMaintenanceStrategy
import io.zeitwert.fm.building.model.enums.CodeBuildingPart
import io.zeitwert.fm.building.model.enums.CodeBuildingPartCatalog
import io.zeitwert.fm.building.model.enums.CodeBuildingRatingStatus
import java.time.LocalDate

abstract class ObjBuildingPartRatingBase protected constructor(
	obj: ObjBuilding,
	repository: PartRepository<ObjBuilding, out Part<ObjBuilding>>,
	property: Property<*>,
	id: Int,
) : ObjPartBase<ObjBuilding>(obj, repository, property, id),
	ObjBuildingPartRating,
	PartMeta<ObjBuilding> {

	protected val _partCatalog = addEnumProperty("partCatalog", CodeBuildingPartCatalog::class.java)
	protected val _maintenanceStrategy =
		addEnumProperty("maintenanceStrategy", CodeBuildingMaintenanceStrategy::class.java)
	protected val _ratingStatus = addEnumProperty("ratingStatus", CodeBuildingRatingStatus::class.java)
	protected val _ratingDate = addBaseProperty("ratingDate", LocalDate::class.java)
	protected val _ratingUser = addReferenceProperty("ratingUser", ObjUser::class.java)
	protected val _elementList = addPartListProperty("elementList", ObjBuildingPartElementRating::class.java)

	override var elementWeights: Int = 0

	override fun doAfterCreate() {
		super.doAfterCreate()
		this.ratingStatus = CodeBuildingRatingStatus.OPEN
	}

	override fun doAddPart(
		property: Property<*>,
		partId: Int?,
	): Part<*> {
		if (property === this._elementList) {
			val partRepo: PartRepository<ObjBuilding, *> =
				directory.getPartRepository<ObjBuilding, ObjBuildingPartElementRating>(
					ObjBuildingPartElementRating::class.java,
				)
			return partRepo.create(aggregate, property, partId)
		}
		return super.doAddPart(property, partId)
	}

	override fun doAfterAdd(
		property: Property<*>,
		part: Part<*>?,
	) {
		if (property === this._elementList) {
			val ratingDate = this.ratingDate
			if (ratingDate != null) {
				val year: Int? = ratingDate.year
				this.getElement(this.elementCount - 1).ratingYear = year
			}
		}
	}

	override fun doAfterSet(property: Property<*>) {
		if (property === this._partCatalog) {
			// Skip auto-populating elements when loading from persistence
			// (elements will be loaded from DB). Only populate on create/update.
			if (!this.meta.isInLoad) {
				this._elementList.clearParts()
				val partCatalog = this.partCatalog
				if (partCatalog != null) {
					for (part in partCatalog.getParts()) {
						this.addElement(part.first).weight = part.second
					}
				}
			}
		} else if (property === this._ratingDate) {
			val ratingDate = this.ratingDate
			if (ratingDate != null) {
				val year: Int? = ratingDate.year
				for (element in this.elementList) {
					element.ratingYear = year
				}
			}
		}
	}

	override val ratingYear: Int?
		get() = if (this.ratingDate != null) this.ratingDate!!.year else null

	override fun getElement(buildingPart: CodeBuildingPart): ObjBuildingPartElementRating =
		this._elementList.parts
			.stream()
			.filter { p: ObjBuildingPartElementRating? -> p!!.buildingPart === buildingPart }
			.findFirst()
			.orElse(null)

	override fun addElement(buildingPart: CodeBuildingPart): ObjBuildingPartElementRating {
// 		requireThis(this.getElement(buildingPart) == null, "unique element for buildingPart [" + buildingPart.getId() + "]");
		val e: ObjBuildingPartElementRating = this._elementList.addPart(null)
		e.buildingPart = buildingPart
		return e
	}

	override val condition: Int?
		get() {
			if (this.ratingYear == null || this.elementWeights == 0) {
				return null
			}
			var condition = 0
			for (element in this.elementList) {
				if (element.weight != null && element.weight!! > 0) {
					condition += element.weight!! * element.condition!!
				}
			}
			return condition / this.elementWeights!!
		}

	override fun getCondition(year: Int): Int? {
		if (year < this.ratingYear!! || this.elementWeights == 0) {
			return null
		}
		var condition = 0
		for (element in this.elementList) {
			if (element.weight != null && element.weight!! > 0) {
				condition += element.weight!! * element.getCondition(year)
			}
		}
		return condition / this.elementWeights!!
	}

	override fun doCalcAll() {
		super.doCalcAll()
		this.doCalcVolatile()
	}

	override fun doCalcVolatile() {
		super.doCalcVolatile()
		this.calcElementWeights()
	}

	private fun calcElementWeights() {
		var elementWeights = 0
		for (element in this.elementList) {
			if (element.weight != null && element.weight!! > 0) {
				elementWeights += element.weight!!
			}
		}
		this.elementWeights = elementWeights
	}

}
