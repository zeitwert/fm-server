package io.zeitwert.fm.doc.model.base;

import org.jooq.DSLContext;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.persistence.jooq.base.DocExtnPersistenceProviderBase;

public abstract class FMDocPersistenceProviderBase<D extends Doc> extends DocExtnPersistenceProviderBase<D> {

	public FMDocPersistenceProviderBase(
			Class<? extends AggregateRepository<D, ?>> repoIntfClass,
			Class<? extends Aggregate> baseClass,
			DSLContext dslContext) {
		super(repoIntfClass, baseClass, dslContext);
		this.mapField("account", EXTN, "account_id", Integer.class);
		this.mapField("extnAccountId", EXTN, "account_id", Integer.class);
	}

}
