
package io.zeitwert.fm.account.model.impl;

import static io.dddrive.util.Invariant.requireThis;

import java.math.BigDecimal;
import java.util.List;

import org.jooq.exception.NoDataFoundException;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.dddrive.jooq.ddd.AggregateState;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.account.model.base.ObjAccountBase;
import io.zeitwert.fm.account.model.db.Tables;
import io.zeitwert.fm.account.model.db.tables.records.ObjAccountRecord;
import io.zeitwert.fm.account.model.db.tables.records.ObjAccountVRecord;
import io.zeitwert.fm.obj.model.base.FMObjExtnRepositoryBase;

@Component("objAccountRepository")
public class ObjAccountRepositoryImpl extends FMObjExtnRepositoryBase<ObjAccount, ObjAccountVRecord>
		implements ObjAccountRepository {

	private static final String AGGREGATE_TYPE = "obj_account";

	protected ObjAccountRepositoryImpl() {
		super(ObjAccountRepository.class, ObjAccount.class, ObjAccountBase.class, AGGREGATE_TYPE);
	}

	@Override
	public void mapProperties() {
		super.mapProperties();
		this.mapField("name", AggregateState.EXTN, "name", String.class);
		this.mapField("description", AggregateState.EXTN, "description", String.class);
		this.mapField("accountType", AggregateState.EXTN, "account_type_id", String.class);
		this.mapField("clientSegment", AggregateState.EXTN, "client_segment_id", String.class);
		this.mapField("referenceCurrency", AggregateState.EXTN, "reference_currency_id", String.class);
		this.mapField("inflationRate", AggregateState.EXTN, "inflation_rate", BigDecimal.class);
		this.mapField("logoImage", AggregateState.EXTN, "logo_img_id", Integer.class);
		this.mapField("mainContact", AggregateState.EXTN, "main_contact_id", Integer.class);
	}

	@Override
	public ObjAccount doCreate() {
		return this.doCreate(this.dslContext().newRecord(Tables.OBJ_ACCOUNT));
	}

	@Override
	public ObjAccount doLoad(Integer objId) {
		requireThis(objId != null, "objId not null");
		ObjAccountRecord accountRecord = this.dslContext().fetchOne(Tables.OBJ_ACCOUNT,
				Tables.OBJ_ACCOUNT.OBJ_ID.eq(objId));
		if (accountRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return this.doLoad(objId, accountRecord);
	}

	@Override
	public List<ObjAccountVRecord> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.OBJ_ACCOUNT_V, Tables.OBJ_ACCOUNT_V.ID, querySpec);
	}

}
