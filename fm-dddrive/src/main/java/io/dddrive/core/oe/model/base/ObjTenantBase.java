package io.dddrive.core.oe.model.base;

import java.time.OffsetDateTime;

import io.dddrive.core.obj.model.ObjRepository;
import io.dddrive.core.obj.model.base.ObjBase;
import io.dddrive.core.oe.model.ObjTenant;
import io.dddrive.core.property.model.BaseProperty;

public abstract class ObjTenantBase extends ObjBase implements ObjTenant {

	//@formatter:off
	protected final BaseProperty<String> key = this.addBaseProperty("key", String.class);
	protected final BaseProperty<String> name = this.addBaseProperty("name", String.class);
	protected final BaseProperty<String> description = this.addBaseProperty("description", String.class);
	//@formatter:on

	public ObjTenantBase(ObjRepository<ObjTenant> repository) {
		super(repository);
	}

//	@Override
//	public void doCalcSearch() {
//		super.doCalcSearch();
//		this.addSearchText(this.getName());
//		this.addSearchText(this.getDescription());
//	}

	@Override
	public void doAfterCreate(Object userId, OffsetDateTime timestamp) {
		super.doAfterCreate(userId, timestamp);
		this.tenant.setId(this.getId());
	}

	@Override
	protected void doCalcAll() {
		super.doCalcAll();
		this.calcCaption();
	}

	protected void calcCaption() {
		this.setCaption(this.getName());
	}

}
