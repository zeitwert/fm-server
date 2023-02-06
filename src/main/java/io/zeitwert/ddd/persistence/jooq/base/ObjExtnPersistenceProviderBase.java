package io.zeitwert.ddd.persistence.jooq.base;

import org.jooq.DSLContext;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.obj.model.Obj;

public abstract class ObjExtnPersistenceProviderBase<O extends Obj> extends ObjPersistenceProviderBase<O> {

	public ObjExtnPersistenceProviderBase(
			Class<? extends AggregateRepository<O, ?>> repoIntfClass,
			Class<? extends Aggregate> baseClass,
			DSLContext dslContext) {
		super(repoIntfClass, baseClass, dslContext);
		this.mapField("extnObjId", EXTN, "obj_id", Integer.class);
		this.mapField("extnTenantId", EXTN, "tenant_id", Integer.class);
		if (this.hasAccount()) {
			this.mapField("extnAccountId", EXTN, "account_id", Integer.class);
		} else {
			this.mapField("extnAccountId", BASE, "account_id", Integer.class);
		}
	}

	protected boolean hasAccount() {
		return true;
	}

}
