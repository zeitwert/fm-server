
package io.zeitwert.ddd.oe.model.base;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.jooq.TableRecord;
import org.springframework.security.crypto.password.PasswordEncoder;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.base.ObjRepositoryBase;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.oe.service.api.ObjTenantCache;
import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.ddd.property.model.enums.CodePartListTypeEnum;

public abstract class ObjUserRepositoryBase extends ObjRepositoryBase<ObjUser, TableRecord<?>>
		implements ObjUserRepository {

	private static final String AGGREGATE_TYPE = "obj_user";

	private final PasswordEncoder passwordEncoder;
	private CodePartListType tenantListType;

	protected ObjUserRepositoryBase(
			final AppContext appContext,
			final DSLContext dslContext,
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

}
