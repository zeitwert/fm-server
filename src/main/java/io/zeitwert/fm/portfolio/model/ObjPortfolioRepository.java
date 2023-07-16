package io.zeitwert.fm.portfolio.model;

import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.building.service.api.ObjBuildingCache;
import io.zeitwert.fm.obj.model.FMObjRepository;
import io.zeitwert.fm.obj.service.api.ObjVCache;
import io.zeitwert.fm.portfolio.model.db.tables.records.ObjPortfolioVRecord;
import io.zeitwert.fm.portfolio.service.api.ObjPortfolioCache;

public interface ObjPortfolioRepository extends FMObjRepository<ObjPortfolio, ObjPortfolioVRecord> {

	ObjVCache getObjCache();

	ObjAccountRepository getAccountCache();

	ObjBuildingCache getBuildingCache();

	ObjBuildingRepository getBuildingRepo();

	ObjPortfolioCache getPortfolioCache();

}
