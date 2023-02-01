package io.zeitwert.fm.account.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.math.BigDecimal;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.context.annotation.Configuration;

import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.account.model.base.ObjAccountBase;
import io.zeitwert.fm.account.model.db.Tables;
import io.zeitwert.fm.account.model.db.tables.records.ObjAccountRecord;
import io.zeitwert.fm.obj.model.base.FMObjPersistenceProviderBase;

@Configuration("accountPersistenceProvider")
public class ObjAccountPersistenceProvider extends FMObjPersistenceProviderBase<ObjAccount> {

	public ObjAccountPersistenceProvider(DSLContext dslContext) {
		super(ObjAccountRepository.class, ObjAccountBase.class, dslContext);
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

	@Override
	public Class<?> getEntityClass() {
		return ObjAccount.class;
	}

	@Override
	public boolean isReal() {
		return true;
	}

	@Override
	public ObjAccount doCreate() {
		return this.doCreate(this.getDSLContext().newRecord(Tables.OBJ_ACCOUNT));
	}

	@Override
	public ObjAccount doLoad(Integer objId) {
		requireThis(objId != null, "objId not null");
		ObjAccountRecord accountRecord = this.getDSLContext().fetchOne(Tables.OBJ_ACCOUNT,
				Tables.OBJ_ACCOUNT.OBJ_ID.eq(objId));
		if (accountRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return this.doLoad(objId, accountRecord);
	}

}
