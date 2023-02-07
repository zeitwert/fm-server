package io.zeitwert.ddd.persistence.jooq.base;

import org.jooq.DSLContext;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.doc.model.Doc;

public abstract class DocPersistenceProviderBase<D extends Doc>
		extends AggregatePersistenceProviderBase<D>
		implements DocPropertyProviderMixin, DocPersistenceProviderMixin<D> {

	public DocPersistenceProviderBase(Class<? extends Aggregate> intfClass, DSLContext dslContext) {
		super(intfClass, dslContext);
		this.mapFields();
	}

}
