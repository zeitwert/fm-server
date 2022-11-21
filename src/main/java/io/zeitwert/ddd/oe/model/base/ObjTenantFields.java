package io.zeitwert.ddd.oe.model.base;

import io.zeitwert.ddd.obj.model.base.ObjExtnFields;

import java.math.BigDecimal;

import org.jooq.Field;
import org.jooq.impl.DSL;

public interface ObjTenantFields extends ObjExtnFields {

	static final Field<String> TENANT_TYPE_ID = DSL.field("tenant_type_id", String.class);
	static final Field<String> EXTL_KEY = DSL.field("extl_key", String.class);
	static final Field<String> NAME = DSL.field("name", String.class);
	static final Field<String> DESCRIPTION = DSL.field("description", String.class);
	static final Field<BigDecimal> INFLATION_RATE = DSL.field("inflation_rate", BigDecimal.class);
	static final Field<Integer> LOGO_IMAGE = DSL.field("logo_img_id", Integer.class);
	static final Field<Integer> BANNER_IMAGE = DSL.field("banner_img_id", Integer.class);

}
