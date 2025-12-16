package io.dddrive.core.ddd.model.impl;

import io.dddrive.core.ddd.model.*;
import io.dddrive.core.property.model.Property;
import io.dddrive.core.property.model.impl.PropertyFilter;
import io.dddrive.core.property.model.impl.PropertyHandler;
import javassist.util.proxy.ProxyFactory;

import java.util.Set;

import static io.dddrive.util.Invariant.requireThis;

public final class PartRepositoryImpl<A extends Aggregate, P extends Part<A>> implements PartRepository<A, P> {

	private static final Set<String> NotLoggedProperties = Set.of("id");

	private final Class<? extends P> intfClass;
	private final Class<? extends P> baseClass;
	private final ProxyFactory partProxyFactory;
	private final Class<?>[] partProxyFactoryParamTypeList;

	public PartRepositoryImpl(Class<? extends A> aggregateIntfClass, Class<? extends P> intfClass, Class<? extends P> baseClass) {
		this.intfClass = intfClass;
		this.baseClass = baseClass;
		this.partProxyFactory = new ProxyFactory();
		this.partProxyFactory.setSuperclass(baseClass);
		this.partProxyFactory.setFilter(PropertyFilter.INSTANCE);
		this.partProxyFactoryParamTypeList = new Class<?>[]{aggregateIntfClass, PartRepository.class, Property.class, Integer.class};
	}

	@Override
	public boolean doLogChange(String propertyName) {
		return !NotLoggedProperties.contains(propertyName);
	}

	@Override
	@SuppressWarnings("unchecked")
	public P create(A aggregate, Property<?> property, Integer partId) {
		boolean isInLoad = aggregate.getMeta().isInLoad();
		requireThis(!isInLoad || partId != null, "partId != null on load");
		requireThis(isInLoad || partId == null, "partId == null on create");
		AggregateRepositorySPI<A> repo = (AggregateRepositorySPI<A>) aggregate.getMeta().getRepository();
		int id = isInLoad ? partId : repo.getPersistenceProvider().nextPartId(aggregate, this.intfClass);
		try {
			P part = (P) this.partProxyFactory.create(this.partProxyFactoryParamTypeList, new Object[]{aggregate, this, property, id}, PropertyHandler.INSTANCE);
			requireThis(isInLoad || part.getMeta().isNew(), "load or part.isNew");
			requireThis(!isInLoad || !part.getMeta().isNew(), "outside load or !part.isNew");
			if (!isInLoad && part instanceof PartSPI) {
				((PartSPI<A>) part).doAfterCreate();
			}
			return part;
		} catch (ReflectiveOperationException | RuntimeException e) {
			throw new RuntimeException("Could not create part " + this.baseClass.getSimpleName(), e); // Adjusted error message
		}
	}

}
