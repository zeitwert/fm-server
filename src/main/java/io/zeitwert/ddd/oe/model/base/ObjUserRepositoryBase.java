
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

public abstract class ObjUserRepositoryBase extends ObjRepositoryBase<ObjUser, TableRecord<?>>
		implements ObjUserRepository {

	private static final String AGGREGATE_TYPE = "obj_user";

	private final PasswordEncoder passwordEncoder;

	protected ObjUserRepositoryBase(AppContext appContext, DSLContext dslContext, PasswordEncoder passwordEncoder) {
		super(ObjUserRepository.class, ObjUser.class, ObjUserBase.class, AGGREGATE_TYPE, appContext, dslContext);
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public PasswordEncoder getPasswordEncoder() {
		return this.passwordEncoder;
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
