
package io.zeitwert.fm.portfolio.model.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.ObjRepository;
import io.zeitwert.ddd.obj.model.base.ObjRepositoryBase;
import io.zeitwert.fm.portfolio.model.ObjPortfolio;
import io.zeitwert.fm.portfolio.model.ObjPortfolioRepository;
import io.zeitwert.fm.portfolio.model.base.ObjPortfolioBase;
import io.zeitwert.fm.portfolio.model.db.Tables;
import io.zeitwert.fm.portfolio.model.db.tables.records.ObjPortfolioVRecord;

@Component("objPortfolioRepository")
@DependsOn({ "objRepository", "objAccountRepository", "objBuildingRepository" })
public class ObjPortfolioRepositoryImpl extends ObjRepositoryBase<ObjPortfolio, ObjPortfolioVRecord>
		implements ObjPortfolioRepository {

	private static final String AGGREGATE_TYPE = "obj_portfolio";

	protected ObjPortfolioRepositoryImpl(AppContext appContext) {
		super(ObjPortfolioRepository.class, ObjPortfolio.class, ObjPortfolioBase.class, AGGREGATE_TYPE, appContext);
	}

	@Override
	@PostConstruct
	public void registerPartRepositories() {
		super.registerPartRepositories();
		this.addPartRepository(ObjRepository.getItemRepository());
	}

	@Override
	protected boolean hasAccountId() {
		return true;
	}

	@Override
	public List<ObjPortfolioVRecord> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.OBJ_PORTFOLIO_V, Tables.OBJ_PORTFOLIO_V.ID, querySpec);
	}

}
