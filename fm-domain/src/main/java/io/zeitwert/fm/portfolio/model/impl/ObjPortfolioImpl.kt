package io.zeitwert.fm.portfolio.model.impl

import io.dddrive.ddd.model.enums.CodeAggregateType
import io.dddrive.ddd.model.enums.CodeAggregateTypeEnum
import io.dddrive.obj.model.Obj
import io.dddrive.property.delegate.baseProperty
import io.dddrive.property.delegate.referenceSetProperty
import io.dddrive.property.model.ReferenceSetProperty
import io.zeitwert.fm.building.model.ObjBuilding
import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.fm.collaboration.model.ObjNoteRepository
import io.zeitwert.fm.collaboration.model.impl.AggregateWithNotesMixin
import io.zeitwert.fm.obj.model.base.FMObjBase
import io.zeitwert.fm.oe.model.ObjTenantFM
import io.zeitwert.fm.portfolio.model.ObjPortfolio
import io.zeitwert.fm.portfolio.model.ObjPortfolioRepository
import io.zeitwert.fm.task.model.impl.AggregateWithTasksMixin

open class ObjPortfolioImpl(
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

	// Base properties
	override var name: String? by baseProperty()
	override var description: String? by baseProperty()
	override var portfolioNr: String? by baseProperty()

	// Reference set properties
	override val includeSet: ReferenceSetProperty<Obj> by referenceSetProperty()
	override val excludeSet: ReferenceSetProperty<Obj> by referenceSetProperty()
	override val buildingSet: ReferenceSetProperty<ObjBuilding> by referenceSetProperty()

	override fun aggregate(): ObjPortfolio = this

	override fun noteRepository() = directory.getRepository(ObjNote::class.java) as ObjNoteRepository

	override fun taskRepository() = repository.taskRepository

	override val account get() = if (accountId != null) repository.accountRepository.get(accountId!!) else null

	private fun hasValidObjType(id: Int?): Boolean {
		if (id == null) return false
		val obj = repository.get(id)
		val objType = obj?.meta?.repository?.aggregateType
		return OBJ_TYPES.contains(objType)
	}

	override val inflationRate: Double
		get() {
			val inflationRate = account?.inflationRate
				?: (tenant as? ObjTenantFM)?.inflationRate
			return inflationRate?.toDouble() ?: 0.0
		}

	override val discountRate: Double
		get() {
			val discountRate = account?.discountRate
				?: (tenant as? ObjTenantFM)?.discountRate
			return discountRate?.toDouble() ?: 0.0
		}

	override fun getPortfolioValue(year: Int): Double = 0.0

	override fun getCondition(year: Int): Int = 0

	override fun doCalcAll() {
		super.doCalcAll()
		calcCaption()
		calcBuildingSet()
	}

	private fun calcCaption() {
		setCaption(name)
	}

	private fun calcBuildingSet() {
		buildingSet.clear()
		for (objId in includeSet) {
			getBuildingIds(objId as Int).forEach { buildingSet.add(it) }
		}
		for (objId in excludeSet) {
			getBuildingIds(objId as Int).forEach { buildingSet.remove(it) }
		}
	}

	private fun getBuildingIds(id: Any): Set<Any> {
		val objType = repository
			.get(id)
			.meta.repository.aggregateType
		return when (objType.id) {
			"obj_building" -> {
				setOf(id)
			}

			"obj_portfolio" -> {
				val pf = repository.get(id)
				pf.buildingSet.toSet()
			}

			"obj_account" -> {
				repository
					.buildingRepository
					.getByForeignKey("accountId", id)
					.map { it as Int }
					.toSet()
			}

			else -> {
				emptySet()
			}
		}
	}

}
