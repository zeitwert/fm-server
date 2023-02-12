
package io.dddrive.app.service.api;

import org.springframework.context.ApplicationEvent;

import io.dddrive.app.model.RequestContext;
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

public interface AppContext {

	RequestContext getRequestContext();

	<A extends Aggregate> AggregateRepository<A, ?> getRepository(Class<A> intfClass);

	PropertyProvider getPropertyProvider(Class<?> intfClass);

	<A extends Aggregate> AggregatePersistenceProvider<A> getAggregatePersistenceProvider(Class<A> intfClass);

	<A extends Aggregate, P extends Part<A>> PartPersistenceProvider<A, P> getPartPersistenceProvider(
			Class<? extends Part<A>> intfClass);

	<Aggr extends Aggregate> AggregateCache<Aggr> getCache(Class<Aggr> intfClass);

	<A extends Aggregate, P extends Part<A>> PartRepository<A, P> getPartRepository(Class<P> intfClass);

	<E extends Enumerated> Enumeration<E> getEnumeration(Class<E> enumClass);

	<E extends Enumerated> E getEnumerated(Class<E> enumClass, String itemId);

	<T> T getBean(Class<T> requiredType);

	void publishApplicationEvent(ApplicationEvent applicationEvent);

}
