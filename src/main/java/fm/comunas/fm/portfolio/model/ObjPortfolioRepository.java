package fm.comunas.fm.portfolio.model;

import fm.comunas.ddd.obj.model.ObjRepository;
import fm.comunas.ddd.property.model.enums.CodePartListType;
import fm.comunas.fm.account.model.ObjAccountRepository;
import fm.comunas.fm.building.model.ObjBuildingRepository;
import fm.comunas.fm.obj.model.ObjVRepository;
import fm.comunas.fm.portfolio.model.db.tables.records.ObjPortfolioVRecord;

public interface ObjPortfolioRepository extends ObjRepository<ObjPortfolio, ObjPortfolioVRecord> {

	ObjVRepository getObjVRepository();

	ObjAccountRepository getAccountRepository();

	ObjBuildingRepository getBuildingRepository();

	CodePartListType getIncludeSetType();

	CodePartListType getExcludeSetType();

	CodePartListType getBuildingSetType();

}
