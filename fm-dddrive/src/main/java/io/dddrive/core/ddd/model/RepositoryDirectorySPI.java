package io.dddrive.core.ddd.model;

import io.dddrive.core.enums.model.Enumerated;
import io.dddrive.core.enums.model.Enumeration;

public interface RepositoryDirectorySPI {

	<E extends Enumerated> void addEnumeration(Class<E> enumClass, Enumeration<E> enumeration);

	void addRepository(Class<? extends Aggregate> intfClass, AggregateRepository<? extends Aggregate> repo);

	<A extends Aggregate> void addPartRepository(Class<? extends Part<A>> intfClass, PartRepository<A, ? extends Part<A>> repo);

}
