
package io.dddrive.obj.model.base;

import io.dddrive.obj.model.Obj;
import io.dddrive.obj.model.ObjRepository;
import io.dddrive.oe.model.ObjTenant;
import io.dddrive.property.model.ReferenceProperty;
import io.dddrive.property.model.SimpleProperty;

public abstract class ObjExtnBase extends ObjBase {

	//@formatter:off
	protected final SimpleProperty<Integer> extnObjId = this.addSimpleProperty("extnObjId", Integer.class);
	protected final ReferenceProperty<ObjTenant> extnTenantId = this.addReferenceProperty("extnTenantId", ObjTenant.class);
	protected final SimpleProperty<Integer> extnAccountId = this.addSimpleProperty("extnAccountId", Integer.class);
	//@formatter:on

	protected ObjExtnBase(ObjRepository<? extends Obj, ? extends Object> repository, Object state) {
		super(repository, state);
	}

	@Override
	public final void doInit(Integer id, Integer tenantId) {
		super.doInit(id, tenantId);
		try {
			this.disableCalc();
			this.extnObjId.setValue(id);
			this.extnTenantId.setId(tenantId);
		} finally {
			this.enableCalc();
		}
	}

	public final Integer getAccountId() {
		return this.accountId.getValue();
	}

	public final void setAccountId(Integer id) {
		this.accountId.setValue(id);
		this.extnAccountId.setValue(id);
	}

}
