package io.zeitwert.ddd.persistence.jooq.base;

import org.jooq.DSLContext;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.obj.model.Obj;

public abstract class ObjPersistenceProviderBase<O extends Obj>
		extends AggregatePersistenceProviderBase<O>
		implements ObjPropertyProviderMixin, ObjPersistenceProviderMixin<O> {

	public ObjPersistenceProviderBase(
			Class<? extends AggregateRepository<O, ?>> repoIntfClass,
			Class<? extends Aggregate> baseClass,
			DSLContext dslContext) {
		super(repoIntfClass, baseClass, dslContext);
		this.mapFields();
	}

	@Override
	public Class<?> getEntityClass() {
		return null;
	}

}
