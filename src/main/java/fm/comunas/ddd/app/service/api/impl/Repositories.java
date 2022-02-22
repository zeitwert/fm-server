
package fm.comunas.ddd.app.service.api.impl;

import fm.comunas.ddd.aggregate.model.Aggregate;
import fm.comunas.ddd.aggregate.model.AggregateRepository;
import fm.comunas.ddd.part.model.Part;
import fm.comunas.ddd.part.model.PartRepository;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;

public final class Repositories {

	private Map<String, Class<? extends Aggregate>> intfClassByModuleClassName = new HashMap<>();
	private Map<Class<? extends Aggregate>, AggregateRepository<?, ?>> repoByIntf = new HashMap<>();

	private Map<String, Class<? extends Part<? extends Aggregate>>> partIntfClassByModuleClassName = new HashMap<>();
	private Map<Class<? extends Part<?>>, PartRepository<?, ?>> partRepoByIntf = new HashMap<>();

	public void addRepository(String aggregateTypeId, final Class<? extends Aggregate> intfClass,
			final AggregateRepository<?, ?> repo) {
		Assert.isTrue(this.getRepository(intfClass) == null, "unique repo for class " + intfClass);
		String module = aggregateTypeId.split("_")[aggregateTypeId.split("_").length - 1];
		String className = intfClass.getSimpleName();
		Assert.isTrue(this.getRepository(module, className) == null, "unique repo for " + module + ":" + className);
		this.intfClassByModuleClassName.put(module + ":" + className, intfClass);
		this.repoByIntf.put(intfClass, repo);
	}

	@SuppressWarnings("unchecked")
	public <A extends Aggregate> AggregateRepository<A, ?> getRepository(final Class<A> intfClass) {
		return (AggregateRepository<A, ?>) this.repoByIntf.get(intfClass);
	}

	@SuppressWarnings("unchecked")
	public <A extends Aggregate> AggregateRepository<A, ?> getRepository(final String module, final String className) {
		return (AggregateRepository<A, ?>) this.repoByIntf
				.get(this.intfClassByModuleClassName.get(module + ":" + className));
	}

	public void addPartRepository(String partTypeId, final Class<? extends Part<?>> intfClass,
			final PartRepository<?, ?> repo) {
		Assert.isTrue(this.getPartRepository(intfClass) == null, "unique repo for class " + intfClass);
		String module = partTypeId.split("_")[partTypeId.split("_").length - 1];
		String className = intfClass.getSimpleName();
		Assert.isTrue(this.getRepository(module, className) == null, "unique repo for " + module + ":" + className);
		this.partIntfClassByModuleClassName.put(module + ":" + className, intfClass);
		this.partRepoByIntf.put(intfClass, repo);
	}

	@SuppressWarnings("unchecked")
	public <A extends Aggregate, P extends Part<A>> PartRepository<A, P> getPartRepository(
			final Class<? extends Part<?>> intfClass) {
		return (PartRepository<A, P>) this.partRepoByIntf.get(intfClass);
	}

	@SuppressWarnings("unchecked")
	public <A extends Aggregate, P extends Part<A>> PartRepository<A, P> getPartRepository(final String module,
			final String className) {
		return (PartRepository<A, P>) this.partRepoByIntf
				.get(this.intfClassByModuleClassName.get(module + ":" + className));
	}

}
