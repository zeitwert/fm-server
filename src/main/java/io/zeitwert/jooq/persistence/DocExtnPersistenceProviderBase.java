package io.zeitwert.jooq.persistence;

import org.jooq.DSLContext;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.doc.model.Doc;

public abstract class DocExtnPersistenceProviderBase<D extends Doc> extends DocPersistenceProviderBase<D> {

	public DocExtnPersistenceProviderBase(Class<? extends Aggregate> intfClass, DSLContext dslContext) {
		super(intfClass, dslContext);
	}

	@Override
	public void mapFields() {
		super.mapFields();
		this.mapField("extnDocId", AggregateState.EXTN, "doc_id", Integer.class);
		this.mapField("extnTenantId", AggregateState.EXTN, "tenant_id", Integer.class);
		this.mapField("extnAccountId", AggregateState.EXTN, "account_id", Integer.class);
	}

}
