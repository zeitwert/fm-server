package io.zeitwert.fm.doc.model.base;

import org.jooq.DSLContext;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.doc.model.base.DocPersistenceProviderBase;
import io.zeitwert.fm.doc.model.FMDoc;

public abstract class FMDocPersistenceProviderBase<O extends FMDoc> extends DocPersistenceProviderBase<O> {

	public FMDocPersistenceProviderBase(
			Class<? extends AggregateRepository<O, ?>> repoIntfClass,
			Class<? extends Aggregate> baseClass,
			DSLContext dslContext) {
		super(repoIntfClass, baseClass, dslContext);
		this.mapField("account", BASE, "account_id", Integer.class);
	}

}
