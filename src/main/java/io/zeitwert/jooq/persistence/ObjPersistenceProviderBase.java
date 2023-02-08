package io.zeitwert.jooq.persistence;

import org.jooq.DSLContext;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.jooq.property.ObjPropertyProviderMixin;

public abstract class ObjPersistenceProviderBase<O extends Obj>
		extends AggregatePersistenceProviderBase<O>
		implements ObjPropertyProviderMixin, ObjPersistenceProviderMixin<O> {

	public ObjPersistenceProviderBase(Class<? extends Aggregate> intfClass, DSLContext dslContext) {
		super(intfClass, dslContext);
		this.mapProperties();
	}

}
