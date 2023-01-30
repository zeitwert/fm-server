
package io.zeitwert.fm.portfolio.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.util.List;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.part.model.enums.CodePartListTypeEnum;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.obj.model.ObjVRepository;
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase;
import io.zeitwert.fm.portfolio.model.ObjPortfolio;
import io.zeitwert.fm.portfolio.model.ObjPortfolioRepository;
import io.zeitwert.fm.portfolio.model.base.ObjPortfolioBase;
import io.zeitwert.fm.portfolio.model.base.ObjPortfolioFields;
import io.zeitwert.fm.portfolio.model.db.Tables;
import io.zeitwert.fm.portfolio.model.db.tables.records.ObjPortfolioRecord;
import io.zeitwert.fm.portfolio.model.db.tables.records.ObjPortfolioVRecord;

@Component("objPortfolioRepository")
@DependsOn({ "objRepository", "objAccountRepository", "objBuildingRepository" })
public class ObjPortfolioRepositoryImpl extends FMObjRepositoryBase<ObjPortfolio, ObjPortfolioVRecord>
		implements ObjPortfolioRepository {

	private static final String AGGREGATE_TYPE = "obj_portfolio";

	private ObjVRepository objVRepository;
	private ObjAccountRepository accountRepository;
	private ObjBuildingRepository buildingRepository;
	private CodePartListType includeSetType;
	private CodePartListType excludeSetType;
	private CodePartListType buildingSetType;

	//@formatter:off
	protected ObjPortfolioRepositoryImpl(
		final AppContext appContext,
		final DSLContext dslContext
	) {
		super(
			ObjPortfolioRepository.class,
			ObjPortfolio.class,
			ObjPortfolioBase.class,
			AGGREGATE_TYPE,
			appContext,
			dslContext
		);
	}
	//@formatter:on

	@Override
	public ObjVRepository getObjVRepository() {
		if (this.objVRepository == null) {
			this.objVRepository = this.getAppContext().getBean(ObjVRepository.class);
		}
		return this.objVRepository;
	}

	@Override
	public ObjAccountRepository getAccountRepository() {
		if (this.accountRepository == null) {
			this.accountRepository = this.getAppContext().getBean(ObjAccountRepository.class);
		}
		return this.accountRepository;
	}

	@Override
	public ObjBuildingRepository getBuildingRepository() {
		if (this.buildingRepository == null) {
			this.buildingRepository = this.getAppContext().getBean(ObjBuildingRepository.class);
		}
		return this.buildingRepository;
	}

	@Override
	public CodePartListType getIncludeSetType() {
		if (this.includeSetType == null) {
			this.includeSetType = CodePartListTypeEnum.getPartListType(ObjPortfolioFields.INCLUDE_LIST);
		}
		return this.includeSetType;
	}

	@Override
	public CodePartListType getExcludeSetType() {
		if (this.excludeSetType == null) {
			this.excludeSetType = CodePartListTypeEnum.getPartListType(ObjPortfolioFields.EXCLUDE_LIST);
		}
		return this.excludeSetType;
	}

	@Override
	public CodePartListType getBuildingSetType() {
		if (this.buildingSetType == null) {
			this.buildingSetType = CodePartListTypeEnum.getPartListType(ObjPortfolioFields.BUILDING_LIST);
		}
		return this.buildingSetType;
	}

	@Override
	@PostConstruct
	public void registerPartRepositories() {
		super.registerPartRepositories();
		this.addPartRepository(this.getItemRepository());
	}

	@Override
	protected boolean hasAccountId() {
		return true;
	}

	@Override
	public ObjPortfolio doCreate() {
		return this.doCreate(this.getDSLContext().newRecord(Tables.OBJ_PORTFOLIO));
	}

	@Override
	public ObjPortfolio doLoad(Integer objId) {
		requireThis(objId != null, "objId not null");
		ObjPortfolioRecord portfolioRecord = this.getDSLContext().fetchOne(Tables.OBJ_PORTFOLIO,
				Tables.OBJ_PORTFOLIO.OBJ_ID.eq(objId));
		if (portfolioRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return this.doLoad(objId, portfolioRecord);
	}

	@Override
	public List<ObjPortfolioVRecord> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.OBJ_PORTFOLIO_V, Tables.OBJ_PORTFOLIO_V.ID, querySpec);
	}

}
