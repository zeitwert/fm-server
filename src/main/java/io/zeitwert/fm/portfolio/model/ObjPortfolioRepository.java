package io.zeitwert.fm.portfolio.model;

import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.part.model.enums.CodePartListTypeEnum;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.obj.model.FMObjRepository;
import io.zeitwert.fm.obj.model.ObjVRepository;
import io.zeitwert.fm.portfolio.model.db.tables.records.ObjPortfolioVRecord;

public interface ObjPortfolioRepository extends FMObjRepository<ObjPortfolio, ObjPortfolioVRecord> {

	static CodePartListType includeSetType() {
		return CodePartListTypeEnum.getPartListType("portfolio.includeSet");
	}

	static CodePartListType excludeSetType() {
		return CodePartListTypeEnum.getPartListType("portfolio.excludeSet");
	}

	static CodePartListType buildingSetType() {
		return CodePartListTypeEnum.getPartListType("portfolio.buildingSet");
	}

	ObjVRepository getObjVRepository();

	ObjAccountRepository getAccountRepository();

	ObjBuildingRepository getBuildingRepository();

}
