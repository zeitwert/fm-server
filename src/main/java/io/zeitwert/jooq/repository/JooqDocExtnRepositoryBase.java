package io.zeitwert.jooq.repository;

import java.util.HashMap;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.TableRecord;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.base.DocRepositoryBase;
import io.zeitwert.jooq.persistence.DocPersistenceProviderMixin;
import io.zeitwert.jooq.property.DocExtnPropertyProviderMixin;

public abstract class JooqDocExtnRepositoryBase<D extends Doc, V extends TableRecord<?>>
		extends DocRepositoryBase<D, V>
		implements DocExtnPropertyProviderMixin, DocPersistenceProviderMixin<D> {

	private final DSLContext dslContext;
	private final Map<String, Object> dbConfigMap = new HashMap<>();

	public JooqDocExtnRepositoryBase(
			Class<? extends AggregateRepository<D, V>> repoIntfClass,
			Class<? extends Doc> intfClass,
			Class<? extends Doc> baseClass,
			String aggregateTypeId,
			AppContext appContext,
			DSLContext dslContext) {
		super(repoIntfClass, intfClass, baseClass, aggregateTypeId, appContext);
		this.dslContext = dslContext;
		this.mapProperties();
	}

	@Override
	public final Class<? extends Aggregate> getEntityClass() {
		return this.getAggregateClass();
	}

	@Override
	public final DSLContext dslContext() {
		return this.dslContext;
	}

	@Override
	public final Map<String, Object> dbConfigMap() {
		return this.dbConfigMap;
	}

	@Override
	public final AggregateRepository<D, V> getRepository() {
		return this;
	}

}
