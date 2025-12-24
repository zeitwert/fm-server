package io.zeitwert.fm.portfolio.model

import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.building.model.ObjBuildingRepository
import io.zeitwert.fm.obj.model.FMObjRepository
import io.zeitwert.fm.task.model.DocTaskRepository

interface ObjPortfolioRepository : FMObjRepository<ObjPortfolio> {

	val accountRepository: ObjAccountRepository

	val buildingRepository: ObjBuildingRepository

	val taskRepository: DocTaskRepository

}
