package io.zeitwert.fm.portfolio.model;

import io.dddrive.ddd.model.enums.CodePartListType;
import io.dddrive.ddd.model.enums.CodePartListTypeEnum;
import io.dddrive.obj.model.ObjRepository;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.obj.model.ObjVRepository;
import io.zeitwert.fm.portfolio.model.db.tables.records.ObjPortfolioVRecord;

public interface ObjPortfolioRepository extends ObjRepository<ObjPortfolio, ObjPortfolioVRecord> {

	static CodePartListType includeSetType() {
		return CodePartListTypeEnum.getPartListType("portfolio.includeSet");
	}

	static CodePartListType excludeSetType() {
		return CodePartListTypeEnum.getPartListType("portfolio.excludeSet");
	}

	static CodePartListType buildingSetType() {
		return CodePartListTypeEnum.getPartListType("portfolio.buildingSet");
	}

	default ObjVRepository getObjRepository() {
		return this.getAppContext().getBean(ObjVRepository.class);
	}

	default ObjAccountRepository getAccountRepository() {
		return this.getAppContext().getBean(ObjAccountRepository.class);
	}

	default ObjBuildingRepository getBuildingRepository() {
		return this.getAppContext().getBean(ObjBuildingRepository.class);
	}

}
