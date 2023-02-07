package io.zeitwert.jooq.persistence;

import org.jooq.DSLContext;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.jooq.property.DocPropertyProviderMixin;

public abstract class DocPersistenceProviderBase<D extends Doc>
		extends AggregatePersistenceProviderBase<D>
		implements DocPropertyProviderMixin, DocPersistenceProviderMixin<D> {

	public DocPersistenceProviderBase(Class<? extends Aggregate> intfClass, DSLContext dslContext) {
		super(intfClass, dslContext);
		this.mapFields();
	}

}
