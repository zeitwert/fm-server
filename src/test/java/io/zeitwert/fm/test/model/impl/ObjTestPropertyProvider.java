package io.zeitwert.fm.test.model.impl;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.jooq.JSON;
import org.springframework.context.annotation.Configuration;

import io.zeitwert.fm.account.model.db.tables.CodeCountry;
import io.zeitwert.fm.obj.model.base.FMObjPropertyProviderBase;
import io.zeitwert.fm.test.model.ObjTest;
import io.zeitwert.fm.test.model.ObjTestPartNode;

@Configuration("testPropertyProvider")
public class ObjTestPropertyProvider extends FMObjPropertyProviderBase {

	@Override
	public Class<?> getEntityClass() {
		return ObjTest.class;
	}

	public ObjTestPropertyProvider() {
		super();
		this.mapField("shortText", DbTableType.EXTN, "short_text", String.class);
		this.mapField("longText", DbTableType.EXTN, "long_text", String.class);
		this.mapField("date", DbTableType.EXTN, "date", LocalDate.class);
		this.mapField("int", DbTableType.EXTN, "int", Integer.class);
		this.mapField("isDone", DbTableType.EXTN, "is_done", Boolean.class);
		this.mapField("json", DbTableType.EXTN, "json", JSON.class);
		this.mapField("nr", DbTableType.EXTN, "nr", BigDecimal.class);
		this.mapField("country", DbTableType.EXTN, "country_id", String.class);
		this.mapField("refTest", DbTableType.EXTN, "ref_test_id", Integer.class);
		this.mapCollection("countrySet", "test.countrySet", CodeCountry.class);
		this.mapCollection("nodeList", "test.nodeList", ObjTestPartNode.class);
	}

}
