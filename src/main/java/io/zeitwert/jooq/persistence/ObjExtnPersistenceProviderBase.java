package io.zeitwert.jooq.persistence;

import org.jooq.DSLContext;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.obj.model.Obj;

public abstract class ObjExtnPersistenceProviderBase<O extends Obj> extends ObjPersistenceProviderBase<O> {

	public ObjExtnPersistenceProviderBase(Class<? extends Aggregate> intfClass, DSLContext dslContext) {
		super(intfClass, dslContext);
	}

	@Override
	public void mapFields() {
		super.mapFields();
		this.mapField("extnObjId", AggregateState.EXTN, "obj_id", Integer.class);
		this.mapField("extnTenantId", AggregateState.EXTN, "tenant_id", Integer.class);
		if (this.hasAccount()) {
			this.mapField("extnAccountId", AggregateState.EXTN, "account_id", Integer.class);
		} else {
			this.mapField("extnAccountId", AggregateState.BASE, "account_id", Integer.class);
		}
	}

	protected boolean hasAccount() {
		return true;
	}

}
