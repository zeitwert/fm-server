
package io.zeitwert.fm.portfolio.model.impl;

import static io.dddrive.util.Invariant.requireThis;

import java.util.List;

import org.jooq.exception.NoDataFoundException;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.dddrive.jooq.ddd.AggregateState;
import io.dddrive.obj.model.Obj;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.building.service.api.ObjBuildingCache;
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase;
import io.zeitwert.fm.obj.service.api.ObjVCache;
import io.zeitwert.fm.portfolio.model.ObjPortfolio;
import io.zeitwert.fm.portfolio.model.ObjPortfolioRepository;
import io.zeitwert.fm.portfolio.model.base.ObjPortfolioBase;
import io.zeitwert.fm.portfolio.model.db.Tables;
import io.zeitwert.fm.portfolio.model.db.tables.records.ObjPortfolioRecord;
import io.zeitwert.fm.portfolio.model.db.tables.records.ObjPortfolioVRecord;
import io.zeitwert.fm.portfolio.service.api.ObjPortfolioCache;

@Component("objPortfolioRepository")
@DependsOn({ "objRepository", "objAccountRepository", "objBuildingRepository" })
public class ObjPortfolioRepositoryImpl extends FMObjRepositoryBase<ObjPortfolio, ObjPortfolioVRecord>
		implements ObjPortfolioRepository {

	private static final String AGGREGATE_TYPE = "obj_portfolio";

	private ObjVCache objCache;
	private ObjAccountRepository accountCache;
	private ObjBuildingCache buildingCache;
	private ObjBuildingRepository buildingRepo;
	private ObjPortfolioCache portfolioCache;

	protected ObjPortfolioRepositoryImpl(
			ObjVCache objCache,
			ObjAccountRepository accountCache,
			ObjBuildingCache buildingCache,
			ObjBuildingRepository buildingRepo,
			@Lazy ObjPortfolioCache portfolioCache) {
		super(ObjPortfolioRepository.class, ObjPortfolio.class, ObjPortfolioBase.class, AGGREGATE_TYPE);
		this.objCache = objCache;
		this.accountCache = accountCache;
		this.buildingCache = buildingCache;
		this.buildingRepo = buildingRepo;
		this.portfolioCache = portfolioCache;
	}

	@Override
	public ObjVCache getObjCache() {
		return this.objCache;
	}

	@Override
	public ObjAccountRepository getAccountCache() {
		return this.accountCache;
	}

	@Override
	public ObjBuildingCache getBuildingCache() {
		return this.buildingCache;
	}

	@Override
	public ObjBuildingRepository getBuildingRepo() {
		return this.buildingRepo;
	}

	@Override
	public ObjPortfolioCache getPortfolioCache() {
		return this.portfolioCache;
	}

	@Override
	public void mapProperties() {
		super.mapProperties();
		this.mapField("name", AggregateState.EXTN, "name", String.class);
		this.mapField("description", AggregateState.EXTN, "description", String.class);
		this.mapField("portfolioNr", AggregateState.EXTN, "portfolio_nr", String.class);
		this.mapCollection("includeSet", "portfolio.includeList", Obj.class);
		this.mapCollection("excludeSet", "portfolio.excludeList", Obj.class);
		this.mapCollection("buildingSet", "portfolio.buildingList", ObjBuilding.class);
	}

	@Override
	public ObjPortfolio doCreate() {
		return this.doCreate(this.dslContext().newRecord(Tables.OBJ_PORTFOLIO));
	}

	@Override
	public ObjPortfolio doLoad(Integer objId) {
		requireThis(objId != null, "objId not null");
		ObjPortfolioRecord portfolioRecord = this.dslContext().fetchOne(Tables.OBJ_PORTFOLIO,
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
