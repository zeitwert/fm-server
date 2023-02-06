
package io.zeitwert.ddd.obj.model.base;

import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjRepository;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;

import org.jooq.TableRecord;

public abstract class ObjExtnBase extends ObjBase {

	//@formatter:off
	protected final SimpleProperty<Integer> extnObjId = this.addSimpleProperty("extnObjId", Integer.class);
	protected final ReferenceProperty<ObjTenant> extnTenantId = this.addReferenceProperty("extnTenantId", ObjTenant.class);
	protected final SimpleProperty<Integer> extnAccountId = this.addSimpleProperty("extnAccountId", Integer.class);
	//@formatter:on

	protected ObjExtnBase(ObjRepository<? extends Obj, ? extends TableRecord<?>> repository, Object state) {
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
