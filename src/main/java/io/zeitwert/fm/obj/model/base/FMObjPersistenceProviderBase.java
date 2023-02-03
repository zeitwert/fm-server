package io.zeitwert.fm.obj.model.base;

import org.jooq.DSLContext;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.persistence.jooq.base.ObjExtnPersistenceProviderBase;
import io.zeitwert.fm.obj.model.FMObj;

public abstract class FMObjPersistenceProviderBase<O extends FMObj> extends ObjExtnPersistenceProviderBase<O> {

	public FMObjPersistenceProviderBase(
			Class<? extends AggregateRepository<O, ?>> repoIntfClass,
			Class<? extends Aggregate> baseClass,
			DSLContext dslContext) {
		super(repoIntfClass, baseClass, dslContext);
		this.mapField("account", BASE, "account_id", Integer.class);
		this.mapField("extnAccountId", EXTN, "account_id", Integer.class);
	}

}
