package io.zeitwert.fm.doc.model.base;

import java.util.List;

import org.jooq.TableRecord;

import io.crnk.core.queryspec.QuerySpec;
import io.dddrive.ddd.model.AggregateRepository;
import io.dddrive.doc.model.Doc;
import io.dddrive.jooq.doc.JooqDocExtnRepositoryBase;
import io.zeitwert.fm.app.model.RequestContextFM;
import io.zeitwert.fm.ddd.model.base.AggregateFindMixin;

public abstract class FMDocExtnRepositoryBase<D extends Doc, V extends TableRecord<?>>
		extends JooqDocExtnRepositoryBase<D, V>
		implements DocPersistenceProviderMixin<D>, AggregateFindMixin<V> {

	public FMDocExtnRepositoryBase(
			Class<? extends AggregateRepository<D, V>> repoIntfClass,
			Class<? extends Doc> intfClass,
			Class<? extends Doc> baseClass,
			String aggregateTypeId) {
		super(repoIntfClass, intfClass, baseClass, aggregateTypeId);
	}

	@Override
	public final AggregateRepository<D, V> getRepository() {
		return this;
	}

	@Override
	public final List<V> find(QuerySpec querySpec) {
		return this.doFind(this.queryWithFilter(querySpec, (RequestContextFM) this.getAppContext().getRequestContext()));
	}

}
