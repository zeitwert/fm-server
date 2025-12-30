package io.zeitwert.fm.portfolio.model.impl

import dddrive.app.obj.model.Obj
import dddrive.ddd.core.model.enums.CodeAggregateType
import dddrive.ddd.core.model.enums.CodeAggregateTypeEnum
import dddrive.ddd.property.delegate.baseProperty
import dddrive.ddd.property.delegate.referenceSetProperty
import dddrive.ddd.property.model.ReferenceSetProperty
import io.zeitwert.fm.building.model.ObjBuilding
import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.fm.collaboration.model.ObjNoteRepository
import io.zeitwert.fm.collaboration.model.impl.AggregateWithNotesMixin
import io.zeitwert.fm.obj.model.base.FMObjBase
import io.zeitwert.fm.oe.model.ObjTenant
import io.zeitwert.fm.portfolio.model.ObjPortfolio
import io.zeitwert.fm.portfolio.model.ObjPortfolioRepository
import io.zeitwert.fm.task.model.impl.AggregateWithTasksMixin

class ObjPortfolioImpl(
	override val repository: ObjPortfolioRepository,
	isNew: Boolean,
) : FMObjBase(repository, isNew),
	ObjPortfolio,
	AggregateWithNotesMixin,
	AggregateWithTasksMixin {

	companion object {

		private val OBJ_TYPES: List<CodeAggregateType> by lazy {
			listOf(
				CodeAggregateTypeEnum.getAggregateType("obj_portfolio"),
				CodeAggregateTypeEnum.getAggregateType("obj_account"),
				CodeAggregateTypeEnum.getAggregateType("obj_building"),
			)
		}
	}

	override var name: String? by baseProperty(this, "name")
	override var description: String? by baseProperty(this, "description")
	override var portfolioNr: String? by baseProperty(this, "portfolioNr")
	override val includeSet: ReferenceSetProperty<Obj> = referenceSetProperty(this, "includeSet")
	override val excludeSet: ReferenceSetProperty<Obj> = referenceSetProperty(this, "excludeSet")
	override val buildingSet: ReferenceSetProperty<ObjBuilding> = referenceSetProperty(this, "buildingSet")

	override fun aggregate(): ObjPortfolio = this

	override fun noteRepository() = directory.getRepository(ObjNote::class.java) as ObjNoteRepository

	override fun taskRepository() = repository.taskRepository

	val tenantRepo = directory.getRepository(ObjTenant::class.java)

	override val account get() = if (accountId != null) repository.accountRepository.get(accountId!!) else null

	private fun hasValidObjType(id: Int?): Boolean {
		if (id == null) return false
		val obj = repository.get(id)
		val objType = obj?.meta?.repository?.aggregateType
		return OBJ_TYPES.contains(objType)
	}

	override val inflationRate: Double
		get() {
			val inflationRate = account?.inflationRate ?: tenantRepo.get(tenantId).inflationRate
			return inflationRate?.toDouble() ?: 0.0
		}

	override val discountRate: Double
		get() {
			val discountRate = account?.discountRate ?: tenantRepo.get(tenantId).discountRate
			return discountRate?.toDouble() ?: 0.0
		}

	override fun getPortfolioValue(year: Int): Double = 0.0

	override fun getCondition(year: Int): Int = 0

	override fun doCalcAll() {
		super.doCalcAll()
		calcCaption()
		calcBuildingSet()
	}

	override fun doCalcVolatile() {
		super.doCalcVolatile()
		calcBuildingSet()
	}

	private fun calcCaption() {
		setCaption(name)
	}

	private fun calcBuildingSet() {
		println("calcBuildingSet: include=${includeSet.toSet()} exclude=${excludeSet.toSet()}")
		buildingSet.clear()
		for (objId in includeSet) {
			getBuildingIds(objId).forEach { buildingSet.add(it) }
		}
		for (objId in excludeSet) {
			getBuildingIds(objId).forEach { buildingSet.remove(it) }
		}
	}

	private fun getBuildingIds(id: Any): Set<Any> {
		println("getBuildingIds.1($id)")
		val objTypeId = directory
			.getRepository(Obj::class.java)
			.get(id)
			.meta.objTypeId
		println("getBuildingIds.2($id): $objTypeId")
		val bldgIds = when (objTypeId) {
			"obj_building" -> {
				setOf(id)
			}

			"obj_portfolio" -> {
				val pf = repository.get(id)
				pf.buildingSet.toSet()
			}

			"obj_account" -> {
				val bldgIds = repository
					.buildingRepository
					.getByForeignKey("accountId", id)
					.toSet()
				println("account($id).buildings: $bldgIds")
				bldgIds
			}

			else -> {
				emptySet()
			}
		}
		println("getBuildingIds.2($id): $bldgIds")
		return bldgIds
	}

}
