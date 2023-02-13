
package io.dddrive.app.service.api.base;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import io.dddrive.app.model.RequestContext;
import io.dddrive.app.service.api.impl.Enumerations;
import io.dddrive.app.service.api.impl.Repositories;
import io.dddrive.ddd.model.Aggregate;
import io.dddrive.ddd.model.AggregatePersistenceProvider;
import io.dddrive.ddd.model.AggregateRepository;
import io.dddrive.ddd.model.Part;
import io.dddrive.ddd.model.PartPersistenceProvider;
import io.dddrive.ddd.model.PartRepository;
import io.dddrive.ddd.service.api.AggregateCache;
import io.dddrive.enums.model.Enumerated;
import io.dddrive.enums.model.Enumeration;
import io.dddrive.property.model.PropertyProvider;

public abstract class AppContextBase {

	private final ApplicationContext applicationContext;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final Repositories repos;
	private final Map<Class<?>, PropertyProvider> propertyProviders = new HashMap<>();
	private final Map<Class<?>, AggregatePersistenceProvider<?>> aggregatePersistenceProviders = new HashMap<>();
	private final Map<Class<?>, PartPersistenceProvider<?, ? extends Part<?>>> partPersistenceProviders = new HashMap<>();
	private final Map<Class<? extends Aggregate>, AggregateCache<?>> cacheByIntf = new HashMap<>();
	private final Enumerations enums;
	private final RequestContext requestContext;

	protected AppContextBase(
			ApplicationContext applicationContext,
			ApplicationEventPublisher applicationEventPublisher,
			Enumerations enums,
			RequestContext requestContext) {
		this.applicationContext = applicationContext;
		this.applicationEventPublisher = applicationEventPublisher;
		this.repos = new Repositories();
		this.enums = enums;
		this.requestContext = requestContext;
	}

	@EventListener
	public void init(ContextRefreshedEvent event) {
		this.initPropertyProviders();
		this.initPersistenceProviders();
	}

	private void initPropertyProviders() {
		this.propertyProviders.clear();
		this.applicationContext
				.getBeansOfType(PropertyProvider.class, false, true)
				.values()
				.forEach(pp -> this.propertyProviders.put(pp.getEntityClass(), pp));
	}

	@SuppressWarnings("unchecked")
	private void initPersistenceProviders() {
		this.aggregatePersistenceProviders.clear();
		this.applicationContext
				.getBeansOfType(AggregatePersistenceProvider.class, false, true)
				.values()
				.forEach(pp -> this.aggregatePersistenceProviders.put(pp.getEntityClass(), pp));
		this.partPersistenceProviders.clear();
		this.applicationContext
				.getBeansOfType(PartPersistenceProvider.class, false, true)
				.values()
				.forEach(pp -> this.partPersistenceProviders.put(pp.getEntityClass(), pp));
	}

	public RequestContext getRequestContext() {
		return this.requestContext;
	}

	public void addRepository(Class<? extends Aggregate> intfClass,
			AggregateRepository<? extends Aggregate, ? extends Object> repo) {
		this.repos.addRepository(intfClass, repo);
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

	public <A extends Aggregate> void addPartRepository(Class<? extends Part<A>> intfClass,
			PartRepository<A, ? extends Part<A>> repo) {
		this.repos.addPartRepository(intfClass, repo);
	}

	public <A extends Aggregate, P extends Part<A>> PartRepository<A, P> getPartRepository(Class<P> intfClass) {
		return this.repos.getPartRepository(intfClass);
	}

	public <E extends Enumerated> void addEnumeration(Class<E> enumClass, Enumeration<E> enumeration) {
		this.enums.addEnumeration(enumClass, enumeration);
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

}
