package io.zeitwert.fm.account.model.impl;

import java.math.BigDecimal;

import org.springframework.context.annotation.Configuration;

import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.obj.model.base.FMObjPropertyProviderBase;

@Configuration("accountPropertyProvider")
public class ObjAccountPropertyProvider extends FMObjPropertyProviderBase {

	@Override
	public Class<?> getEntityClass() {
		return ObjAccount.class;
	}

	public ObjAccountPropertyProvider() {
		super();
		this.mapField("key", DbTableType.EXTN, "intl_key", String.class);
		this.mapField("name", DbTableType.EXTN, "name", String.class);
		this.mapField("description", DbTableType.EXTN, "description", String.class);
		this.mapField("accountType", DbTableType.EXTN, "account_type_id", String.class);
		this.mapField("clientSegment", DbTableType.EXTN, "client_segment_id", String.class);
		this.mapField("referenceCurrency", DbTableType.EXTN, "reference_currency_id", String.class);
		this.mapField("inflationRate", DbTableType.EXTN, "inflation_rate", BigDecimal.class);
		this.mapField("logoImage", DbTableType.EXTN, "logo_img_id", Integer.class);
		this.mapField("mainContact", DbTableType.EXTN, "main_contact_id", Integer.class);
	}

}
