package io.zeitwert.fm.portfolio.model.impl

import io.dddrive.core.ddd.model.AggregatePersistenceProvider
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.building.model.ObjBuildingRepository
import io.zeitwert.fm.obj.model.base.FMObjCoreRepositoryBase
import io.zeitwert.fm.portfolio.model.ObjPortfolio
import io.zeitwert.fm.portfolio.model.ObjPortfolioRepository
import io.zeitwert.fm.portfolio.model.base.ObjPortfolioBase
import io.zeitwert.fm.portfolio.persist.jooq.ObjPortfolioPersistenceProvider
import io.zeitwert.fm.task.model.DocTaskRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Component("objPortfolioRepository")
class ObjPortfolioRepositoryImpl : FMObjCoreRepositoryBase<ObjPortfolio>(
    ObjPortfolioRepository::class.java,
    ObjPortfolio::class.java,
    ObjPortfolioBase::class.java,
    AGGREGATE_TYPE_ID
), ObjPortfolioRepository {

    private lateinit var persistenceProvider: ObjPortfolioPersistenceProvider
    private lateinit var _accountRepository: ObjAccountRepository
    private lateinit var _buildingRepository: ObjBuildingRepository
    private lateinit var _taskRepository: DocTaskRepository

    @Autowired
    @Lazy
    fun setPersistenceProvider(persistenceProvider: ObjPortfolioPersistenceProvider) {
        this.persistenceProvider = persistenceProvider
    }

    @Autowired
    @Lazy
    fun setAccountRepository(accountRepository: ObjAccountRepository) {
        this._accountRepository = accountRepository
    }

    @Autowired
    @Lazy
    fun setBuildingRepository(buildingRepository: ObjBuildingRepository) {
        this._buildingRepository = buildingRepository
    }

    @Autowired
    @Lazy
    fun setTaskRepository(taskRepository: DocTaskRepository) {
        this._taskRepository = taskRepository
    }

    override fun getPersistenceProvider(): AggregatePersistenceProvider<ObjPortfolio> = persistenceProvider

    override fun getAccountRepository(): ObjAccountRepository = _accountRepository

    override fun getBuildingRepository(): ObjBuildingRepository = _buildingRepository

    override fun getTaskRepository(): DocTaskRepository = _taskRepository

    companion object {
        private const val AGGREGATE_TYPE_ID = "obj_portfolio"
    }
}

