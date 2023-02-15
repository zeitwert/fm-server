package io.zeitwert.fm.portfolio.model;

import io.dddrive.ddd.model.enums.CodePartListType;
import io.dddrive.ddd.model.enums.CodePartListTypeEnum;
import io.dddrive.obj.model.ObjRepository;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.building.service.api.ObjBuildingCache;
import io.zeitwert.fm.obj.service.api.ObjVCache;
import io.zeitwert.fm.portfolio.model.db.tables.records.ObjPortfolioVRecord;
import io.zeitwert.fm.portfolio.service.api.ObjPortfolioCache;

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

	default ObjVCache getObjCache() {
		return this.getAppContext().getBean(ObjVCache.class);
	}

	default ObjAccountRepository getAccountCache() {
		return this.getAppContext().getBean(ObjAccountRepository.class);
	}

	default ObjBuildingCache getBuildingCache() {
		return this.getAppContext().getBean(ObjBuildingCache.class);
	}

	default ObjBuildingRepository getBuildingRepo() {
		return this.getAppContext().getBean(ObjBuildingRepository.class);
	}

	default ObjPortfolioCache getPortfolioCache() {
		return this.getAppContext().getBean(ObjPortfolioCache.class);
	}

}
