package io.zeitwert.fm.oe.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.oe.model.base.ObjUserBase;
import io.zeitwert.ddd.persistence.jooq.base.ObjExtnPersistenceProviderBase;
import io.zeitwert.fm.oe.model.db.Tables;
import io.zeitwert.fm.oe.model.db.tables.records.ObjUserRecord;

@Configuration("userPersistenceProvider")
@DependsOn("codePartListTypeEnum")
public class ObjUserPersistenceProvider extends ObjExtnPersistenceProviderBase<ObjUser> {

	public ObjUserPersistenceProvider(DSLContext dslContext) {
		super(ObjUserRepository.class, ObjUserBase.class, dslContext);
		this.mapField("email", EXTN, "email", String.class);
		this.mapField("name", EXTN, "name", String.class);
		this.mapField("description", EXTN, "description", String.class);
		this.mapField("role", EXTN, "role_list", String.class);
		this.mapField("avatarImage", EXTN, "avatar_img_id", Integer.class);
		this.mapField("password", EXTN, "password", String.class);
		this.mapField("needPasswordChange", EXTN, "need_password_change", Boolean.class);
		this.mapCollection("tenantSet", "user.tenantList", ObjTenant.class);
	}

	@Override
	public Class<?> getEntityClass() {
		return ObjUser.class;
	}

	@Override
	protected boolean hasAccount() {
		return false;
	}

	@Override
	public ObjUser doCreate() {
		return this.doCreate(this.getDSLContext().newRecord(Tables.OBJ_USER));
	}

	@Override
	public ObjUser doLoad(Integer objId) {
		requireThis(objId != null, "objId not null");
		ObjUserRecord userRecord = this.getDSLContext().fetchOne(Tables.OBJ_USER,
				Tables.OBJ_USER.OBJ_ID.eq(objId));
		if (userRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return this.doLoad(objId, userRecord);
	}

}
