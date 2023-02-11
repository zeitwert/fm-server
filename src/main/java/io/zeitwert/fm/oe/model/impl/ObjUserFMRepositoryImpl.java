
package io.zeitwert.fm.oe.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.util.List;
import java.util.Optional;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.service.api.ObjTenantCache;
import io.zeitwert.fm.oe.model.ObjUserFM;
import io.zeitwert.fm.oe.model.ObjUserFMRepository;
import io.zeitwert.fm.oe.model.base.ObjUserFMBase;
import io.zeitwert.fm.oe.model.db.Tables;
import io.zeitwert.fm.oe.model.db.tables.records.ObjUserRecord;
import io.zeitwert.fm.oe.model.db.tables.records.ObjUserVRecord;
import io.zeitwert.jooq.persistence.AggregateState;
import io.zeitwert.jooq.repository.JooqObjExtnRepositoryBase;

@Component("objUserRepository")
public class ObjUserFMRepositoryImpl extends JooqObjExtnRepositoryBase<ObjUserFM, ObjUserVRecord>
		implements ObjUserFMRepository {

	private static final String AGGREGATE_TYPE = "obj_user";

	private final PasswordEncoder passwordEncoder;

	// passwordEncoder: break cycle from WebSecurityConfig TODO find better solution
	// (own class)
	protected ObjUserFMRepositoryImpl(AppContext appContext, DSLContext dslContext,
			@Lazy PasswordEncoder passwordEncoder) {
		super(ObjUserFMRepository.class, ObjUserFM.class, ObjUserFMBase.class, AGGREGATE_TYPE, appContext, dslContext);
		this.passwordEncoder = passwordEncoder;
	}

	public void mapProperties() {
		super.mapProperties();
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
	public boolean hasAccount() {
		return false;
	}

	@Override
	public boolean hasAccountId() {
		return false;
	}

	@Override
	public PasswordEncoder getPasswordEncoder() {
		return this.passwordEncoder;
	}

	@Override
	public ObjTenantCache getTenantCache() {
		return AppContext.getInstance().getBean(ObjTenantCache.class);
	}

	@Override
	public ObjUserFM doCreate() {
		return this.doCreate(this.dslContext().newRecord(Tables.OBJ_USER));
	}

	@Override
	public ObjUserFM doLoad(Integer objId) {
		requireThis(objId != null, "objId not null");
		ObjUserRecord userRecord = this.dslContext().fetchOne(Tables.OBJ_USER,
				Tables.OBJ_USER.OBJ_ID.eq(objId));
		if (userRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return this.doLoad(objId, userRecord);
	}

	@Override
	public List<ObjUserVRecord> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.OBJ_USER_V, Tables.OBJ_USER_V.ID, querySpec);
	}

	@Override
	public Optional<ObjUserFM> getByEmail(String email) {
		ObjUserVRecord userRecord = this.dslContext().fetchOne(Tables.OBJ_USER_V, Tables.OBJ_USER_V.EMAIL.eq(email));
		if (userRecord == null) {
			return Optional.empty();
		}
		return Optional.of(this.get(userRecord.getId()));
	}

}
