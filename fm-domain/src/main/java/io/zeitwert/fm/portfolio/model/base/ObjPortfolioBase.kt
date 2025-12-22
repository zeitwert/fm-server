package io.zeitwert.fm.portfolio.model.base

import io.dddrive.ddd.model.enums.CodeAggregateType
import io.dddrive.ddd.model.enums.CodeAggregateTypeEnum
import io.dddrive.obj.model.Obj
import io.dddrive.property.model.ReferenceSetProperty
import io.zeitwert.fm.building.model.ObjBuilding
import io.zeitwert.fm.collaboration.model.impl.AggregateWithNotesMixin
import io.zeitwert.fm.obj.model.base.FMObjBase
import io.zeitwert.fm.oe.model.ObjTenantFM
import io.zeitwert.fm.portfolio.model.ObjPortfolio
import io.zeitwert.fm.portfolio.model.ObjPortfolioRepository
import io.zeitwert.fm.task.model.impl.AggregateWithTasksMixin

abstract class ObjPortfolioBase(
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

	private lateinit var _includeSet: ReferenceSetProperty<Obj>
	private lateinit var _excludeSet: ReferenceSetProperty<Obj>
	private lateinit var _buildingSet: ReferenceSetProperty<ObjBuilding>

	override fun doInit() {
		super.doInit()
		addBaseProperty("name", String::class.java)
		addBaseProperty("description", String::class.java)
		addBaseProperty("portfolioNr", String::class.java)
		_includeSet = addReferenceSetProperty("includeSet", Obj::class.java)
		_excludeSet = addReferenceSetProperty("excludeSet", Obj::class.java)
		_buildingSet = addReferenceSetProperty("buildingSet", ObjBuilding::class.java)
	}

	override fun aggregate(): ObjPortfolio = this

	override fun taskRepository() = repository.taskRepository

	override val account get() = if (accountId != null) repository.accountRepository.get(accountId!!) else null

	// override fun addInclude(id: Int?) {
	// 	require(hasValidObjType(id)) { "supported objType $id" }
	// 	_includeSet.addItem(id)
	// }

	// override fun addExclude(id: Int?) {
	// 	requireThis(hasValidObjType(id), "supported objType $id")
	// 	_excludeSet.addItem(id)
	// }

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
		_buildingSet.clearItems()
		for (objId in _includeSet.items) {
			getBuildingIds(objId as Int).forEach { _buildingSet.addItem(it) }
		}
		for (objId in _excludeSet.items) {
			getBuildingIds(objId as Int).forEach { _buildingSet.removeItem(it) }
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
				pf.buildingSet
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

	// override fun doCalcSearch() {
	//     super.doCalcSearch()
	//     this.addSearchToken(this.portfolioNr)
	//     this.addSearchText(this.name)
	//     this.addSearchText(this.description)
	// }

}
