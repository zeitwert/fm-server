package io.dddrive.jooq.doc;

import java.util.HashMap;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.TableRecord;

import io.dddrive.app.service.api.AppContext;
import io.dddrive.ddd.model.Aggregate;
import io.dddrive.ddd.model.AggregateRepository;
import io.dddrive.doc.model.Doc;
import io.dddrive.doc.model.base.DocRepositoryBase;

public abstract class JooqDocRepositoryBase<D extends Doc, V extends TableRecord<?>>
		extends DocRepositoryBase<D, V>
		implements DocPropertyProviderMixin {

	private final DSLContext dslContext;
	private final Map<String, Object> dbConfigMap = new HashMap<>();

	public JooqDocRepositoryBase(
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
	public final Map<String, Object> dbConfigMap() {
		return this.dbConfigMap;
	}

	public final DSLContext dslContext() {
		return this.dslContext;
	}

}
