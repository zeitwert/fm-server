package io.zeitwert.fm.portfolio.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import io.zeitwert.fm.portfolio.model.ObjPortfolio;
import io.zeitwert.fm.portfolio.model.ObjPortfolioRepository;
import io.zeitwert.fm.portfolio.model.base.ObjPortfolioBase;
import io.zeitwert.fm.portfolio.model.db.Tables;
import io.zeitwert.fm.portfolio.model.db.tables.records.ObjPortfolioRecord;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.obj.model.base.FMObjPersistenceProviderBase;

@Configuration("portfolioPersistenceProvider")
@DependsOn("codePartListTypeEnum")
public class ObjPortfolioPersistenceProvider extends FMObjPersistenceProviderBase<ObjPortfolio> {

	public ObjPortfolioPersistenceProvider(DSLContext dslContext) {
		super(ObjPortfolioRepository.class, ObjPortfolioBase.class, dslContext);
		this.mapField("extnAccountId", EXTN, "account_id", Integer.class);
		this.mapField("name", EXTN, "name", String.class);
		this.mapField("description", EXTN, "description", String.class);
		this.mapField("portfolioNr", EXTN, "portfolio_nr", String.class);
		this.mapCollection("includeSet", "portfolio.includeList", Obj.class);
		this.mapCollection("excludeSet", "portfolio.excludeList", Obj.class);
		this.mapCollection("buildingSet", "portfolio.buildingList", ObjBuilding.class);

	}

	@Override
	public Class<?> getEntityClass() {
		return ObjPortfolio.class;
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

}
