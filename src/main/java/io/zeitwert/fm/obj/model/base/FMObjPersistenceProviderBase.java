package io.zeitwert.fm.obj.model.base;

import org.jooq.DSLContext;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.obj.model.base.ObjPersistenceProviderBase;
import io.zeitwert.fm.obj.model.FMObj;

public class FMObjPersistenceProviderBase<O extends FMObj> extends ObjPersistenceProviderBase<O> {

	public FMObjPersistenceProviderBase(
			Class<? extends AggregateRepository<O, ?>> repoIntfClass,
			Class<? extends Aggregate> baseClass,
			DSLContext dslContext) {
		super(repoIntfClass, baseClass, dslContext);
		this.mapField("account", DbTableType.BASE, "account_id", Integer.class);
	}

}
