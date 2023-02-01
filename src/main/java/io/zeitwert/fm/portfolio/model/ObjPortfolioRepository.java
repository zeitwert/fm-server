package io.zeitwert.fm.portfolio.model;

import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.obj.model.FMObjRepository;
import io.zeitwert.fm.obj.model.ObjVRepository;
import io.zeitwert.fm.portfolio.model.db.tables.records.ObjPortfolioVRecord;

public interface ObjPortfolioRepository extends FMObjRepository<ObjPortfolio, ObjPortfolioVRecord> {

	ObjVRepository getObjVRepository();

	ObjAccountRepository getAccountRepository();

	ObjBuildingRepository getBuildingRepository();

}
