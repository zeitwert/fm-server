package io.zeitwert.jooq.persistence;

import org.jooq.DSLContext;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.obj.model.Obj;

public abstract class ObjExtnPersistenceProviderBase<O extends Obj> extends ObjPersistenceProviderBase<O> {

	public ObjExtnPersistenceProviderBase(Class<? extends Aggregate> intfClass, DSLContext dslContext) {
		super(intfClass, dslContext);
	}

	@Override
	public void mapProperties() {
		super.mapProperties();
		this.mapField("extnObjId", AggregateState.EXTN, "obj_id", Integer.class);
		this.mapField("extnTenantId", AggregateState.EXTN, "tenant_id", Integer.class);
		// this is only used for Tenant, User, they don't have an extnAccountId, but
		// need to map somewhere
		this.mapField("extnAccountId", AggregateState.BASE, "account_id", Integer.class);
	}

}
