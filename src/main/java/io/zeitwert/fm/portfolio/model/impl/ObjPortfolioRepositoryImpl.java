
package io.zeitwert.fm.portfolio.model.impl;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import static io.zeitwert.ddd.util.Check.requireThis;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.building.model.ObjBuilding;
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

import javax.annotation.PostConstruct;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.collaboration.model.ObjNoteRepository;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjPartItemRepository;
import io.zeitwert.ddd.obj.model.ObjPartTransitionRepository;
import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.ddd.session.model.SessionInfo;

@Component("objPortfolioRepository")
@DependsOn({ "objRepository", "objAccountRepository", "objBuildingRepository" })
public class ObjPortfolioRepositoryImpl extends FMObjRepositoryBase<ObjPortfolio, ObjPortfolioVRecord>
		implements ObjPortfolioRepository {

	private static final String ITEM_TYPE = "obj_portfolio";

	private final ObjVRepository objVRepository;

	private final ObjAccountRepository accountRepository;

	private final ObjBuildingRepository buildingRepository;

	private final CodePartListType includeSetType;

	private final CodePartListType excludeSetType;

	private final CodePartListType buildingSetType;

	@Autowired
	//@formatter:off
	protected ObjPortfolioRepositoryImpl(
		final AppContext appContext,
		final DSLContext dslContext,
		final ObjPartTransitionRepository transitionRepository,
		final ObjPartItemRepository itemRepository,
		final ObjNoteRepository noteRepository
	) {
		super(
			ObjPortfolioRepository.class,
			ObjPortfolio.class,
			ObjPortfolioBase.class,
			ITEM_TYPE,
			appContext,
			dslContext,
			transitionRepository,
			itemRepository,
			noteRepository
		);
		this.objVRepository = (ObjVRepository) this.getAppContext().getRepository(Obj.class);
		this.accountRepository = (ObjAccountRepository) this.getAppContext().getRepository(ObjAccount.class);
		this.buildingRepository = (ObjBuildingRepository) this.getAppContext().getRepository(ObjBuilding.class);
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
	protected String getAccountIdField() {
		return ObjPortfolioFields.ACCOUNT_ID.getName();
	}

	@Override
	public ObjPortfolio doCreate(SessionInfo sessionInfo) {
		return this.doCreate(sessionInfo, this.getDSLContext().newRecord(Tables.OBJ_PORTFOLIO));
	}

	@Override
	public ObjPortfolio doLoad(SessionInfo sessionInfo, Integer objId) {
		requireThis(objId != null, "objId not null");
		ObjPortfolioRecord portfolioRecord = this.getDSLContext().fetchOne(Tables.OBJ_PORTFOLIO,
				Tables.OBJ_PORTFOLIO.OBJ_ID.eq(objId));
		if (portfolioRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return this.doLoad(sessionInfo, objId, portfolioRecord);
	}

	@Override
	public List<ObjPortfolioVRecord> doFind(SessionInfo sessionInfo, QuerySpec querySpec) {
		return this.doFind(sessionInfo, Tables.OBJ_PORTFOLIO_V, Tables.OBJ_PORTFOLIO_V.ID, querySpec);
	}

}
