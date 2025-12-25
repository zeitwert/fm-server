package io.zeitwert.fm.building.model.impl

import io.dddrive.ddd.model.Part
import io.dddrive.ddd.model.PartMeta
import io.dddrive.ddd.model.PartRepository
import io.dddrive.obj.model.base.ObjPartBase
import io.dddrive.oe.model.ObjUser
import io.dddrive.property.delegate.baseProperty
import io.dddrive.property.delegate.enumProperty
import io.dddrive.property.delegate.partListProperty
import io.dddrive.property.delegate.referenceIdProperty
import io.dddrive.property.delegate.referenceProperty
import io.dddrive.property.model.PartListProperty
import io.dddrive.property.model.Property
import io.zeitwert.fm.building.model.ObjBuilding
import io.zeitwert.fm.building.model.ObjBuildingPartElementRating
import io.zeitwert.fm.building.model.ObjBuildingPartRating
import io.zeitwert.fm.building.model.enums.CodeBuildingMaintenanceStrategy
import io.zeitwert.fm.building.model.enums.CodeBuildingPart
import io.zeitwert.fm.building.model.enums.CodeBuildingPartCatalog
import io.zeitwert.fm.building.model.enums.CodeBuildingRatingStatus
import java.time.LocalDate

open class ObjBuildingPartRatingImpl(
	obj: ObjBuilding,
	override val repository: PartRepository<ObjBuilding, out Part<ObjBuilding>>,
	property: Property<*>,
	id: Int,
) : ObjPartBase<ObjBuilding>(obj, repository, property, id),
	ObjBuildingPartRating,
	PartMeta<ObjBuilding> {

	// Enum properties
	override var partCatalog: CodeBuildingPartCatalog? by enumProperty()
	override var maintenanceStrategy: CodeBuildingMaintenanceStrategy? by enumProperty()
	override var ratingStatus: CodeBuildingRatingStatus? by enumProperty()

	// Base properties
	override var ratingDate: LocalDate? by baseProperty()

	// Reference properties
	override var ratingUserId: Any? by referenceIdProperty<ObjUser>()
	override var ratingUser: ObjUser? by referenceProperty()

	// Part list property
	override val elementList: PartListProperty<ObjBuildingPartElementRating> by partListProperty()

	override var elementWeights: Int = 0

	override fun doAfterCreate() {
		super.doAfterCreate()
		this.ratingStatus = CodeBuildingRatingStatus.OPEN
	}

	override fun doAddPart(
		property: Property<*>,
		partId: Int?,
	): Part<*> {
		if (property === this.elementList) {
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
		if (property === this.elementList) {
			val ratingDate = this.ratingDate
			if (ratingDate != null) {
				val year: Int? = ratingDate.year
				this.elementList.get(this.elementList.size - 1).ratingYear = year
			}
		}
	}

	override fun doAfterSet(property: Property<*>) {
		if (property.name == "partCatalog") {
			// Skip auto-populating elements when loading from persistence
			// (elements will be loaded from DB). Only populate on create/update.
			if (!this.meta.isInLoad) {
				this.elementList.clear()
				val partCatalog = this.partCatalog
				if (partCatalog != null) {
					for (part in partCatalog.getParts()) {
						this.addElement(part.first).weight = part.second
					}
				}
			}
		} else if (property.name == "ratingDate") {
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

	override fun getElement(buildingPart: CodeBuildingPart) = this.elementList.first { p: ObjBuildingPartElementRating? -> p!!.buildingPart === buildingPart }

	override fun addElement(buildingPart: CodeBuildingPart): ObjBuildingPartElementRating {
		val e: ObjBuildingPartElementRating = this.elementList.add(null)
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
			return condition / this.elementWeights
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
		return condition / this.elementWeights
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
