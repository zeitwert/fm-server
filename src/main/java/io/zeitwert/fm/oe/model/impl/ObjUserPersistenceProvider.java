package io.zeitwert.fm.oe.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.persistence.jooq.AggregateState;
import io.zeitwert.ddd.persistence.jooq.base.ObjExtnPersistenceProviderBase;
import io.zeitwert.fm.oe.model.db.Tables;
import io.zeitwert.fm.oe.model.db.tables.records.ObjUserRecord;

@Configuration("userPersistenceProvider")
@DependsOn("codePartListTypeEnum")
public class ObjUserPersistenceProvider extends ObjExtnPersistenceProviderBase<ObjUser> {

	public ObjUserPersistenceProvider(DSLContext dslContext) {
		super(ObjUser.class, dslContext);
		this.mapField("email", AggregateState.EXTN, "email", String.class);
		this.mapField("name", AggregateState.EXTN, "name", String.class);
		this.mapField("description", AggregateState.EXTN, "description", String.class);
		this.mapField("role", AggregateState.EXTN, "role_list", String.class);
		this.mapField("avatarImage", AggregateState.EXTN, "avatar_img_id", Integer.class);
		this.mapField("password", AggregateState.EXTN, "password", String.class);
		this.mapField("needPasswordChange", AggregateState.EXTN, "need_password_change", Boolean.class);
		this.mapCollection("tenantSet", "user.tenantList", ObjTenant.class);
	}

	@Override
	protected boolean hasAccount() {
		return false;
	}

	@Override
	public ObjUser doCreate() {
		return this.doCreate(this.dslContext().newRecord(Tables.OBJ_USER));
	}

	@Override
	public ObjUser doLoad(Integer objId) {
		requireThis(objId != null, "objId not null");
		ObjUserRecord userRecord = this.dslContext().fetchOne(Tables.OBJ_USER,
				Tables.OBJ_USER.OBJ_ID.eq(objId));
		if (userRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return this.doLoad(objId, userRecord);
	}

}
