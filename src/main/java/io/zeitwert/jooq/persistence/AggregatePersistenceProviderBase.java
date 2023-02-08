package io.zeitwert.jooq.persistence;

import java.util.HashMap;
import java.util.Map;

import org.jooq.DSLContext;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregatePersistenceProvider;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.app.service.api.AppContext;

public abstract class AggregatePersistenceProviderBase<A extends Aggregate> implements AggregatePersistenceProvider<A> {

	private final Class<? extends Aggregate> intfClass;
	private final DSLContext dslContext;
	private final Map<String, Object> dbConfigMap = new HashMap<>();

	public AggregatePersistenceProviderBase(Class<? extends Aggregate> intfClass, DSLContext dslContext) {
		this.intfClass = intfClass;
		this.dslContext = dslContext;
	}

	@Override
	public final Class<? extends Aggregate> getEntityClass() {
		return this.intfClass;
	}

	public final DSLContext dslContext() {
		return this.dslContext;
	}

	public final Map<String, Object> dbConfigMap() {
		return this.dbConfigMap;
	}

	@SuppressWarnings("unchecked")
	public final AggregateRepository<A, ?> getRepository() {
		return (AggregateRepository<A, ?>) AppContext.getInstance().getRepository(this.intfClass);
	}

}
