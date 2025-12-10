package io.dddrive.core.ddd.model;

import io.dddrive.core.ddd.model.impl.RepositoryDirectoryImpl;
import io.dddrive.core.enums.model.Enumerated;
import io.dddrive.core.enums.model.Enumeration;

public interface RepositoryDirectory {

	static RepositoryDirectory getInstance() {
		return RepositoryDirectoryImpl.getInstance();
	}

	<E extends Enumerated> Enumeration<E> getEnumeration(Class<E> enumClass);

	Enumeration<? extends Enumerated> getEnumeration(String module, String enumName);

	<A extends Aggregate> AggregateRepository<A> getRepository(Class<A> intfClass);

	<A extends Aggregate, P extends Part<A>> PartRepository<A, P> getPartRepository(Class<P> intfClass);

}
