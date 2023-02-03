package io.zeitwert.ddd.persistence.jooq.base;

import org.jooq.DSLContext;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.doc.model.Doc;

public abstract class DocExtnPersistenceProviderBase<D extends Doc> extends DocPersistenceProviderBase<D> {

	public DocExtnPersistenceProviderBase(
			Class<? extends AggregateRepository<D, ?>> repoIntfClass,
			Class<? extends Aggregate> baseClass,
			DSLContext dslContext) {
		super(repoIntfClass, baseClass, dslContext);
		this.mapField("extnDocId", EXTN, "doc_id", Integer.class);
		this.mapField("extnTenantId", EXTN, "tenant_id", Integer.class);
	}

}
