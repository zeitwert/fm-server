
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
import io.zeitwert.ddd.obj.model.ObjPartItemRepository;
import io.zeitwert.ddd.obj.model.ObjPartTransitionRepository;
import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
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

	private final ObjVRepository objVRepository;
	private final ObjAccountRepository accountRepository;
	private final ObjBuildingRepository buildingRepository;
	private final CodePartListType includeSetType;
	private final CodePartListType excludeSetType;
	private final CodePartListType buildingSetType;

	//@formatter:off
	protected ObjPortfolioRepositoryImpl(
		final AppContext appContext,
		final DSLContext dslContext,
		final ObjPartTransitionRepository transitionRepository,
		final ObjPartItemRepository itemRepository,
		final ObjNoteRepository noteRepository,
		final ObjVRepository objVRepository,
		final ObjAccountRepository accountRepository,
		final ObjBuildingRepository buildingRepository
	) {
		super(
			ObjPortfolioRepository.class,
			ObjPortfolio.class,
			ObjPortfolioBase.class,
			AGGREGATE_TYPE,
			appContext,
			dslContext,
			transitionRepository,
			itemRepository,
			noteRepository
		);
		this.objVRepository = objVRepository;
		this.accountRepository = accountRepository;
		this.buildingRepository = buildingRepository;
		this.includeSetType = this.getAppContext().getPartListType(ObjPortfolioFields.INCLUDE_LIST);
		this.excludeSetType = this.getAppContext().getPartListType(ObjPortfolioFields.EXCLUDE_LIST);
		this.buildingSetType = this.getAppContext().getPartListType(ObjPortfolioFields.BUILDING_LIST);
	}
	//@formatter:on

	@Override
	@PostConstruct
	public void registerPartRepositories() {
		super.registerPartRepositories();
		this.addPartRepository(this.getItemRepository());
	}

	public ObjVRepository getObjVRepository() {
		return this.objVRepository;
	}

	public ObjAccountRepository getAccountRepository() {
		return this.accountRepository;
	}

	public ObjBuildingRepository getBuildingRepository() {
		return this.buildingRepository;
	}

	public CodePartListType getIncludeSetType() {
		return this.includeSetType;
	}

	public CodePartListType getExcludeSetType() {
		return this.excludeSetType;
	}

	public CodePartListType getBuildingSetType() {
		return this.buildingSetType;
	}

	@Override
	protected boolean hasAccountId() {
		return true;
	}

	@Override
	public ObjPortfolio doCreate(RequestContext requestCtx) {
		return this.doCreate(requestCtx, this.getDSLContext().newRecord(Tables.OBJ_PORTFOLIO));
	}

	@Override
	public ObjPortfolio doLoad(RequestContext requestCtx, Integer objId) {
		requireThis(objId != null, "objId not null");
		ObjPortfolioRecord portfolioRecord = this.getDSLContext().fetchOne(Tables.OBJ_PORTFOLIO,
				Tables.OBJ_PORTFOLIO.OBJ_ID.eq(objId));
		if (portfolioRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return this.doLoad(requestCtx, objId, portfolioRecord);
	}

	@Override
	public List<ObjPortfolioVRecord> doFind(RequestContext requestCtx, QuerySpec querySpec) {
		return this.doFind(requestCtx, Tables.OBJ_PORTFOLIO_V, Tables.OBJ_PORTFOLIO_V.ID, querySpec);
	}

}
