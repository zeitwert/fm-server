
package io.zeitwert.ddd.obj.model.base;

import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableRecord;
import org.jooq.Record;

import io.crnk.core.queryspec.FilterOperator;
import io.crnk.core.queryspec.PathSpec;
import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.aggregate.model.base.AggregateRepositoryBase;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjRepository;
import io.zeitwert.ddd.util.SqlUtils;

import java.util.List;

public abstract class ObjRepositoryBase<O extends Obj, V extends TableRecord<?>>
		extends AggregateRepositoryBase<O, V>
		implements ObjRepository<O, V> {

	protected ObjRepositoryBase(
			Class<? extends AggregateRepository<O, V>> repoIntfClass,
			Class<? extends Obj> intfClass,
			Class<? extends Obj> baseClass,
			String aggregateTypeId,
			AppContext appContext) {
		super(repoIntfClass, intfClass, baseClass, aggregateTypeId, appContext);
	}

	@Override
	public void registerPartRepositories() {
		this.addPartRepository(ObjRepository.getTransitionRepository());
	}

	@Override
	public void delete(O obj) {
		obj.delete();
		this.store(obj);
	}

	@Override
	protected List<V> doFind(Table<? extends Record> table, Field<Integer> idField, QuerySpec querySpec) {
		if (!SqlUtils.hasFilterFor(querySpec, "isClosed")) {
			querySpec.addFilter(PathSpec.of(ObjFields.CLOSED_AT.getName()).filter(FilterOperator.EQ, null));
		}
		return super.doFind(table, idField, querySpec);
	}

}
