
package io.zeitwert.ddd.oe.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.base.ObjRepositoryBase;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.oe.model.base.ObjUserBase;
import io.zeitwert.ddd.oe.model.base.ObjUserFields;
import io.zeitwert.ddd.oe.model.db.Tables;
import io.zeitwert.ddd.oe.model.db.tables.records.ObjUserRecord;
import io.zeitwert.ddd.oe.model.db.tables.records.ObjUserVRecord;
import io.zeitwert.ddd.oe.service.api.ObjTenantCache;
import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.ddd.property.model.enums.CodePartListTypeEnum;

@Component("objUserRepository")
public class ObjUserRepositoryImpl extends ObjRepositoryBase<ObjUser, ObjUserVRecord> implements ObjUserRepository {

	private static final String AGGREGATE_TYPE = "obj_user";

	private final PasswordEncoder passwordEncoder;
	private CodePartListType tenantListType;

	protected ObjUserRepositoryImpl(
			final AppContext appContext,
			final DSLContext dslContext,
			@Lazy // break cycle from WebSecurityConfig
			final PasswordEncoder passwordEncoder) {
		super(
				ObjUserRepository.class,
				ObjUser.class,
				ObjUserBase.class,
				AGGREGATE_TYPE,
				appContext,
				dslContext);
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public PasswordEncoder getPasswordEncoder() {
		return this.passwordEncoder;
	}

	@Override
	public CodePartListType getTenantSetType() {
		if (this.tenantListType == null) {
			this.tenantListType = CodePartListTypeEnum.getPartListType(ObjUserFields.TENANT_LIST);
		}
		return this.tenantListType;
	}

	@Override
	public ObjTenantCache getTenantCache() {
		return this.getAppContext().getBean(ObjTenantCache.class);
	}

	@Override
	@PostConstruct
	public void registerPartRepositories() {
		super.registerPartRepositories();
		this.addPartRepository(this.getItemRepository());
	}

	@Override
	public ObjUser doCreate() {
		return this.doCreate(this.getDSLContext().newRecord(Tables.OBJ_USER));
	}

	@Override
	public ObjUser doLoad(Integer objId) {
		requireThis(objId != null, "objId not null");
		ObjUserRecord userRecord = this.getDSLContext().fetchOne(Tables.OBJ_USER, Tables.OBJ_USER.OBJ_ID.eq(objId));
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
	public Optional<ObjUser> getByEmail(String email) {
		ObjUserVRecord userRecord = this.getDSLContext().fetchOne(Tables.OBJ_USER_V, Tables.OBJ_USER_V.EMAIL.eq(email));
		if (userRecord == null) {
			return Optional.empty();
		}
		return Optional.of(this.get(userRecord.getId()));
	}

}
