package io.zeitwert.fm.portfolio.model.base

import io.dddrive.core.ddd.model.enums.CodeAggregateType
import io.dddrive.core.ddd.model.enums.CodeAggregateTypeEnum
import io.dddrive.core.obj.model.Obj
import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.ReferenceSetProperty
import io.dddrive.util.Invariant.requireThis
import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.building.model.ObjBuilding
import io.zeitwert.fm.collaboration.model.impl.AggregateWithNotesMixin
import io.zeitwert.fm.obj.model.base.FMObjCoreBase
import io.zeitwert.fm.oe.model.ObjTenantFM
import io.zeitwert.fm.portfolio.model.ObjPortfolio
import io.zeitwert.fm.portfolio.model.ObjPortfolioRepository
import io.zeitwert.fm.task.model.DocTask
import io.zeitwert.fm.task.model.impl.AggregateWithTasksMixin
import java.time.OffsetDateTime

abstract class ObjPortfolioBase(
    repository: ObjPortfolioRepository
) : FMObjCoreBase(repository), ObjPortfolio, AggregateWithNotesMixin, AggregateWithTasksMixin {

    companion object {
        private val OBJ_TYPES: List<CodeAggregateType> by lazy {
            listOf(
                CodeAggregateTypeEnum.getAggregateType("obj_portfolio"),
                CodeAggregateTypeEnum.getAggregateType("obj_account"),
                CodeAggregateTypeEnum.getAggregateType("obj_building")
            )
        }
    }

    private val _name: BaseProperty<String> = this.addBaseProperty("name", String::class.java)
    private val _description: BaseProperty<String> = this.addBaseProperty("description", String::class.java)
    private val _portfolioNr: BaseProperty<String> = this.addBaseProperty("portfolioNr", String::class.java)
    private val _includeSet: ReferenceSetProperty<Obj> = this.addReferenceSetProperty("includeSet", Obj::class.java)
    private val _excludeSet: ReferenceSetProperty<Obj> = this.addReferenceSetProperty("excludeSet", Obj::class.java)
    private val _buildingSet: ReferenceSetProperty<ObjBuilding> = this.addReferenceSetProperty("buildingSet", ObjBuilding::class.java)

    override fun aggregate(): ObjPortfolio = this

    override fun taskRepository() = getRepository().taskRepository

    override fun getRepository(): ObjPortfolioRepository {
        return super.getRepository() as ObjPortfolioRepository
    }

    override fun getAccount(): ObjAccount? {
        return getRepository().accountRepository.get(accountId)
    }

    override fun getName(): String? = _name.value

    override fun setName(name: String?) {
        _name.value = name
    }

    override fun getDescription(): String? = _description.value

    override fun setDescription(description: String?) {
        _description.value = description
    }

    override fun getPortfolioNr(): String? = _portfolioNr.value

    override fun setPortfolioNr(portfolioNr: String?) {
        _portfolioNr.value = portfolioNr
    }

    @Suppress("UNCHECKED_CAST")
    override fun getIncludeSet(): Set<Int> {
        return _includeSet.items.map { it as Int }.toSet()
    }

    override fun clearIncludeSet() {
        _includeSet.clearItems()
    }

    override fun addInclude(id: Int?) {
        requireThis(hasValidObjType(id), "supported objType $id")
        _includeSet.addItem(id)
    }

    override fun removeInclude(id: Int?) {
        _includeSet.removeItem(id)
    }

    @Suppress("UNCHECKED_CAST")
    override fun getExcludeSet(): Set<Int> {
        return _excludeSet.items.map { it as Int }.toSet()
    }

    override fun clearExcludeSet() {
        _excludeSet.clearItems()
    }

    override fun addExclude(id: Int?) {
        requireThis(hasValidObjType(id), "supported objType $id")
        _excludeSet.addItem(id)
    }

    override fun removeExclude(id: Int?) {
        _excludeSet.removeItem(id)
    }

    @Suppress("UNCHECKED_CAST")
    override fun getBuildingSet(): Set<Int> {
        return _buildingSet.items.map { it as Int }.toSet()
    }

    private fun hasValidObjType(id: Int?): Boolean {
        if (id == null) return false
        val obj = getRepository().get(id)
        val objType = obj?.meta?.repository?.aggregateType
        return OBJ_TYPES.contains(objType)
    }

    override fun getInflationRate(): Double {
        val inflationRate = account?.inflationRate
            ?: (tenant as? ObjTenantFM)?.inflationRate
        return inflationRate?.toDouble() ?: 0.0
    }

    override fun getDiscountRate(): Double {
        val discountRate = account?.discountRate
            ?: (tenant as? ObjTenantFM)?.discountRate
        return discountRate?.toDouble() ?: 0.0
    }

    override fun getPortfolioValue(year: Int): Double {
        return 0.0
    }

    override fun getCondition(year: Int): Int? {
        return null
    }

    override fun doCalcAll() {
        super.doCalcAll()
        calcCaption()
        calcBuildingSet()
    }

    private fun calcCaption() {
        this.caption.value = this.name
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

    private fun getBuildingIds(id: Int): Set<Int> {
        val objType = getRepository().get(id)?.meta?.repository?.aggregateType
        return when (objType?.id) {
            "obj_building" -> setOf(id)
            "obj_portfolio" -> {
                val pf = getRepository().get(id)
                (pf as? ObjPortfolio)?.buildingSet ?: emptySet()
            }
            "obj_account" -> {
                getRepository().buildingRepository
                    .getByForeignKey("accountId", id)
                    .map { it.id as Int }
                    .toSet()
            }
            else -> emptySet()
        }
    }

    // override fun doCalcSearch() {
    //     super.doCalcSearch()
    //     this.addSearchToken(this.portfolioNr)
    //     this.addSearchText(this.name)
    //     this.addSearchText(this.description)
    // }

    override fun getTasks(): List<DocTask> = taskRepository().getByForeignKey("related_obj_id", id)

    override fun addTask(): DocTask {
        val task = taskRepository().create(tenantId, null, OffsetDateTime.now())
        task.setRelatedToId(id as Int)
        return task
    }
}

