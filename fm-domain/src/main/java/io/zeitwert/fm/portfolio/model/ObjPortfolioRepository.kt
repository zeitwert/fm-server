package io.zeitwert.fm.portfolio.model

import io.dddrive.obj.model.ObjRepository
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.building.model.ObjBuildingRepository
import io.zeitwert.fm.task.model.DocTaskRepository

interface ObjPortfolioRepository : ObjRepository<ObjPortfolio> {

	val accountRepository: ObjAccountRepository

	val buildingRepository: ObjBuildingRepository

	val taskRepository: DocTaskRepository

}
