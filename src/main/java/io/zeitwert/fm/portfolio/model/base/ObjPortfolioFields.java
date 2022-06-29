package io.zeitwert.fm.portfolio.model.base;

import io.zeitwert.ddd.obj.model.base.ObjExtnFields;

import org.jooq.Field;
import org.jooq.impl.DSL;

public interface ObjPortfolioFields extends ObjExtnFields {

	static final Field<String> NAME = DSL.field("name", String.class);
	static final Field<String> DESCRIPTION = DSL.field("description", String.class);
	static final Field<String> PORTFOLIO_NR = DSL.field("portfolio_nr", String.class);
	static final Field<Integer> ACCOUNT_ID = DSL.field("account_id", Integer.class);

	static final String INCLUDE_LIST = "portfolio.includeList";
	static final String EXCLUDE_LIST = "portfolio.excludeList";
	static final String BUILDING_LIST = "portfolio.buildingList";

}
