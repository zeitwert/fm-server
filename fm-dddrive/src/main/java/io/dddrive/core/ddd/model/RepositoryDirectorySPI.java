package io.dddrive.core.ddd.model;

import io.dddrive.core.enums.model.Enumerated;
import io.dddrive.core.enums.model.Enumeration;

public interface RepositoryDirectorySPI {

	void addRepository(Class<? extends Aggregate> intfClass, AggregateRepository<? extends Aggregate> repo);

	<A extends Aggregate> void addPartRepository(Class<? extends Part<A>> intfClass, PartRepository<A, ? extends Part<A>> repo);

	void addPersistenceProvider(Class<? extends Aggregate> intfClass, AggregatePersistenceProvider<? extends Aggregate> repo);

	<P extends Part<?>> void addPartPersistenceProvider(Class<P> intfClass, PartPersistenceProvider<P> ppp);

	<E extends Enumerated> void addEnumeration(Class<E> enumClass, Enumeration<E> enumeration);

}
