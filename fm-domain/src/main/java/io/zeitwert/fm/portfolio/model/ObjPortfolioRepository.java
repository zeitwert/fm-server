package io.zeitwert.fm.portfolio.model;

import io.dddrive.core.obj.model.ObjRepository;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.task.model.DocTaskRepository;

public interface ObjPortfolioRepository extends ObjRepository<ObjPortfolio> {

	ObjAccountRepository getAccountRepository();

	ObjBuildingRepository getBuildingRepository();

	DocTaskRepository getTaskRepository();

}
