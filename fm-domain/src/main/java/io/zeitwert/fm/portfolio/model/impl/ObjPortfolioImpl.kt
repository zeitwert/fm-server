package io.zeitwert.fm.portfolio.model.impl

import dddrive.app.obj.model.Obj
import dddrive.ddd.core.model.enums.CodeAggregateType
import dddrive.ddd.core.model.enums.CodeAggregateTypeEnum
import dddrive.ddd.property.delegate.baseProperty
import dddrive.ddd.property.delegate.referenceSetProperty
import io.crnk.core.queryspec.FilterOperator
import io.crnk.core.queryspec.PathSpec
import io.crnk.core.queryspec.QuerySpec
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

	override var name by baseProperty<String>("name")
	override var description by baseProperty<String>("description")
	override var portfolioNr by baseProperty<String>("portfolioNr")
	override val includeSet = referenceSetProperty<Obj>("includeSet")
	override val excludeSet = referenceSetProperty<Obj>("excludeSet")
	override val buildingSet = referenceSetProperty<ObjBuilding>("buildingSet")

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
		buildingSet.clear()
		for (objId in includeSet) {
			getBuildingIds(objId).forEach { buildingSet.add(it) }
		}
		for (objId in excludeSet) {
			getBuildingIds(objId).forEach { buildingSet.remove(it) }
		}
	}

	private fun getBuildingIds(id: Any): Set<Any> {
		val objTypeId = directory
			.getRepository(Obj::class.java)
			.get(id)
			.meta.objTypeId
		return when (objTypeId) {
			"obj_building" -> {
				setOf(id)
			}

			"obj_portfolio" -> {
				repository.get(id).buildingSet.toSet()
			}

			"obj_account" -> {
				val query = QuerySpec(ObjBuilding::class.java).apply {
					addFilter(PathSpec.of("accountId").filter(FilterOperator.EQ, id))
				}
				repository
					.buildingRepository
					.find(query)
					.toSet()
			}

			else -> {
				emptySet()
			}
		}
	}

}
