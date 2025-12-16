package io.dddrive.core.ddd.model.impl;

import static io.dddrive.util.Invariant.assertThis;

import java.util.HashMap;
import java.util.Map;

import io.dddrive.core.ddd.model.*;
import io.dddrive.core.enums.model.Enumerated;
import io.dddrive.core.enums.model.Enumeration;

public final class RepositoryDirectoryImpl implements RepositoryDirectory, RepositoryDirectorySPI {

	private static RepositoryDirectoryImpl instance;

	private final Map<Class<? extends Aggregate>, AggregateRepository<?>> repoByIntf = new HashMap<>();
	private final Map<Class<? extends Part<?>>, PartRepository<?, ?>> partRepoByIntf = new HashMap<>();

	private final Map<Class<? extends Aggregate>, AggregatePersistenceProvider<?>> appByIntf = new HashMap<>();
//	private final Map<Class<? extends Part<?>>, PartPersistenceProvider<?, ?>> pppByIntf = new HashMap<>();

	private final Map<String, Enumeration<? extends Enumerated>> enumsById = new HashMap<>();
	private final Map<Class<? extends Enumerated>, Enumeration<? extends Enumerated>> enumsByEnumeratedClass = new HashMap<>();

	private RepositoryDirectoryImpl() {
	}

	public static RepositoryDirectoryImpl getInstance() {
		if (instance == null) {
			instance = new RepositoryDirectoryImpl();
		}
		return instance;
	}

	@Override
	public Enumeration<? extends Enumerated> getEnumeration(String module, String name) {
		return this.enumsById.get(module + "." + name + "Enum");
	}

	@Override
	@SuppressWarnings("unchecked")
	public <E extends Enumerated> Enumeration<E> getEnumeration(Class<E> enumClass) {
		return (Enumeration<E>) this.enumsByEnumeratedClass.get(enumClass);
	}

//	@Override
//	public <E extends Enumerated> E getEnumerated(Class<E> enumClass, String itemId) {
//		return this.getEnumeration(enumClass).getItem(itemId);
//	}

	@Override
	public <E extends Enumerated> void addEnumeration(Class<E> enumeratedClass, Enumeration<E> e) {
		this.enumsById.put(e.getModule() + "." + e.getId(), e);
		this.enumsByEnumeratedClass.put(enumeratedClass, e);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <A extends Aggregate> AggregateRepository<A> getRepository(Class<A> intfClass) {
		return (AggregateRepository<A>) this.repoByIntf.get(intfClass);
	}

	@Override
	public void addRepository(Class<? extends Aggregate> intfClass, AggregateRepository<? extends Aggregate> repo) {
		assertThis(this.getRepository(intfClass) == null, "unique repo for class " + intfClass);
		this.repoByIntf.put(intfClass, repo);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <A extends Aggregate, P extends Part<A>> PartRepository<A, P> getPartRepository(Class<P> intfClass) {
		return (PartRepository<A, P>) this.partRepoByIntf.get(intfClass);
	}

	@Override
	public <A extends Aggregate> void addPartRepository(Class<? extends Part<A>> intfClass, PartRepository<A, ? extends Part<A>> repo) {
		//assertThis(this.getPartRepository(intfClass) == null, "unique repo for class " + intfClass);
		this.partRepoByIntf.put(intfClass, repo);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <A extends Aggregate> AggregatePersistenceProvider<A> getPersistenceProvider(Class<A> intfClass) {
		return (AggregatePersistenceProvider<A>) appByIntf.get(intfClass);
	}

	@Override
	public void addPersistenceProvider(Class<? extends Aggregate> intfClass, AggregatePersistenceProvider<? extends Aggregate> app) {
		assertThis(this.getPersistenceProvider(intfClass) == null, "unique persistence provider for class " + intfClass);
		this.appByIntf.put(intfClass, app);
	}

}
