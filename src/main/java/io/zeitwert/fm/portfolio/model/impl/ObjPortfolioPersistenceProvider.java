package io.zeitwert.fm.portfolio.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import io.zeitwert.fm.portfolio.model.ObjPortfolio;
import io.zeitwert.fm.portfolio.model.db.Tables;
import io.zeitwert.fm.portfolio.model.db.tables.records.ObjPortfolioRecord;
import io.zeitwert.jooq.persistence.AggregateState;
import io.zeitwert.jooq.persistence.ObjExtnPersistenceProviderBase;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.fm.building.model.ObjBuilding;

@Configuration("portfolioPersistenceProvider")
@DependsOn("codePartListTypeEnum")
public class ObjPortfolioPersistenceProvider extends ObjExtnPersistenceProviderBase<ObjPortfolio> {

	public ObjPortfolioPersistenceProvider(DSLContext dslContext) {
		super(ObjPortfolio.class, dslContext);
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

}
