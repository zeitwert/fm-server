package io.zeitwert.fm.portfolio.model.impl

import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.building.model.ObjBuildingRepository
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase
import io.zeitwert.fm.portfolio.model.ObjPortfolio
import io.zeitwert.fm.portfolio.model.ObjPortfolioRepository
import io.zeitwert.fm.task.model.DocTaskRepository
import org.springframework.stereotype.Component

@Component("objPortfolioRepository")
class ObjPortfolioRepositoryImpl(
	override val accountRepository: ObjAccountRepository,
	override val buildingRepository: ObjBuildingRepository,
	override val taskRepository: DocTaskRepository,
	override val sessionContext: SessionContext,
) : FMObjRepositoryBase<ObjPortfolio>(
		ObjPortfolio::class.java,
		AGGREGATE_TYPE_ID,
	),
	ObjPortfolioRepository {

	override fun createAggregate(isNew: Boolean) = ObjPortfolioImpl(this, isNew)

	companion object {

		private const val AGGREGATE_TYPE_ID = "obj_portfolio"
	}

}
