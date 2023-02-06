
package io.zeitwert.ddd.app.service.api;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregatePersistenceProvider;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.aggregate.service.api.AggregateCache;
import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.ddd.app.service.api.impl.Repositories;
import io.zeitwert.ddd.enums.model.Enumerated;
import io.zeitwert.ddd.enums.model.Enumeration;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.PartPersistenceProvider;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.ddd.property.model.PropertyProvider;
import io.zeitwert.ddd.session.model.RequestContext;

@Service("appContext")
@DependsOn({ "codeAggregateTypeEnum", "codePartListTypeEnum" })
public final class AppContext {

	public static final String SCHEMA_NAME = "public";
	private static Schema SCHEMA;

	private static AppContext INSTANCE;

	private final ApplicationContext applicationContext;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final DSLContext dslContext;
	private final Repositories repos;
	private final Map<Class<?>, PropertyProvider> propertyProviders = new HashMap<>();
	private final Map<Class<?>, AggregatePersistenceProvider<?>> aggregatePersistenceProviders = new HashMap<>();
	private final Map<Class<?>, PartPersistenceProvider<?, ? extends Part<?>>> partPersistenceProviders = new HashMap<>();
	private Map<Class<? extends Aggregate>, AggregateCache<?>> cacheByIntf = new HashMap<>();
	private final Enumerations enums;
	private final RequestContext requestContext;

	protected AppContext(final ApplicationContext applicationContext, ApplicationEventPublisher applicationEventPublisher,
			final DSLContext dslContext, Enumerations enums, RequestContext requestContext) {
		this.applicationContext = applicationContext;
		this.applicationEventPublisher = applicationEventPublisher;
		this.dslContext = dslContext;
		this.repos = new Repositories();
		this.enums = enums;
		this.requestContext = requestContext;
		AppContext.INSTANCE = this;
	}

	@PostConstruct
	public void initPropertyProviders() {
		this.applicationContext
				.getBeansOfType(PropertyProvider.class, false, true)
				.values()
				.forEach(pp -> this.propertyProviders.put(pp.getEntityClass(), pp));
	}

	@PostConstruct
	@SuppressWarnings("unchecked")
	public void initPersistenceProviders() {
		this.applicationContext
				.getBeansOfType(AggregatePersistenceProvider.class, false, true)
				.values()
				.forEach(pp -> this.aggregatePersistenceProviders.put(pp.getEntityClass(), pp));
		this.applicationContext
				.getBeansOfType(PartPersistenceProvider.class, false, true)
				.values()
				.forEach(pp -> this.partPersistenceProviders.put(pp.getEntityClass(), pp));
	}

	public static AppContext getInstance() {
		return INSTANCE;
	}

	public RequestContext getRequestContext() { // TODO: remove this method
		return this.requestContext;
	}

	public DSLContext getDslContext() { // TODO: remove this method
		return this.dslContext;
	}

	public void addRepository(String aggregateTypeId, final Class<? extends Aggregate> intfClass,
			final AggregateRepository<? extends Aggregate, ? extends Record> repo) {
		this.repos.addRepository(aggregateTypeId, intfClass, repo);
	}

	public <A extends Aggregate> AggregateRepository<A, ?> getRepository(Class<A> intfClass) {
		return this.repos.getRepository(intfClass);
	}

	public PropertyProvider getPropertyProvider(Class<?> intfClass) {
		return this.propertyProviders.get(intfClass);
	}

	@SuppressWarnings("unchecked")
	public <A extends Aggregate> AggregatePersistenceProvider<A> getAggregatePersistenceProvider(Class<A> intfClass) {
		return (AggregatePersistenceProvider<A>) this.aggregatePersistenceProviders.get(intfClass);
	}

	@SuppressWarnings("unchecked")
	public <A extends Aggregate, P extends Part<A>> PartPersistenceProvider<A, P> getPartPersistenceProvider(
			Class<? extends Part<A>> intfClass) {
		return (PartPersistenceProvider<A, P>) this.partPersistenceProviders.get(intfClass);
	}

	public void addCache(Class<? extends Aggregate> intfClass, AggregateCache<? extends Aggregate> cache) {
		this.cacheByIntf.put(intfClass, cache);
	}

	@SuppressWarnings("unchecked")
	public <Aggr extends Aggregate> AggregateCache<Aggr> getCache(Class<Aggr> intfClass) {
		return (AggregateCache<Aggr>) this.cacheByIntf.get(intfClass);
	}

	public <A extends Aggregate> void addPartRepository(String partTypeId, final Class<? extends Part<A>> intfClass,
			final PartRepository<A, ? extends Part<A>> repo) {
		this.repos.addPartRepository(partTypeId, intfClass, repo);
	}

	public <A extends Aggregate, P extends Part<A>> PartRepository<A, P> getPartRepository(Class<P> intfClass) {
		return this.repos.getPartRepository(intfClass);
	}

	public <E extends Enumerated> Enumeration<E> getEnumeration(Class<E> enumClass) {
		return this.enums.getEnumeration(enumClass);
	}

	public <E extends Enumerated> E getEnumerated(Class<E> enumClass, String itemId) {
		return this.enums.getEnumeration(enumClass).getItem(itemId);
	}

	public <T> T getBean(Class<T> requiredType) {
		return this.applicationContext.getBean(requiredType);
	}

	public void publishApplicationEvent(ApplicationEvent applicationEvent) {
		this.applicationEventPublisher.publishEvent(applicationEvent);
	}

	public Table<?> getTable(String tableName) {
		return this.getSchema().getTable(tableName);
	}

	private Schema getSchema() {
		if (SCHEMA == null) {
			SCHEMA = this.dslContext.meta().getSchemas(AppContext.SCHEMA_NAME).get(0);
		}
		return SCHEMA;
	}

}
