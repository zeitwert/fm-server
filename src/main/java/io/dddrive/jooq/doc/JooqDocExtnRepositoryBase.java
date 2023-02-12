package io.dddrive.jooq.doc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.TableRecord;

import io.crnk.core.queryspec.QuerySpec;
import io.dddrive.app.service.api.AppContext;
import io.dddrive.ddd.model.Aggregate;
import io.dddrive.ddd.model.AggregateRepository;
import io.dddrive.doc.model.Doc;
import io.dddrive.doc.model.base.DocRepositoryBase;
import io.dddrive.jooq.ddd.JooqAggregateFinderMixin;

public abstract class JooqDocExtnRepositoryBase<D extends Doc, V extends TableRecord<?>>
		extends DocRepositoryBase<D, V>
		implements DocExtnPropertyProviderMixin, DocPersistenceProviderMixin<D>, JooqAggregateFinderMixin<V> {

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

	@Override
	public final List<V> find(QuerySpec querySpec) {
		return this.doFind(this.queryWithFilter(querySpec, this.getAppContext().getRequestContext()));
	}

}
