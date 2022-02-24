
package io.zeitwert.ddd.part.model.base;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;
import org.springframework.context.event.EventListener;
import org.springframework.util.Assert;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.app.event.AggregateStoredEvent;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.ddd.property.model.base.PropertyFilter;
import io.zeitwert.ddd.property.model.base.PropertyHandler;
import io.zeitwert.ddd.property.model.enums.CodePartListType;
import javassist.util.proxy.ProxyFactory;

public abstract class PartRepositoryBase<A extends Aggregate, P extends Part<A>>
		implements PartRepository<A, P>, PartRepositorySPI<A, P> {

	private final AppContext appContext;
	protected final DSLContext dslContext;

	private final ProxyFactory proxyFactory;
	private final Class<?>[] paramTypeList;

	private final Map<A, List<P>> cache = new ConcurrentHashMap<>();

	private boolean didDoInit = false;

	//@formatter:off
	protected PartRepositoryBase(
		final Class<? extends A> aggregateIntfClass,
		final Class<? extends Part<A>> intfClass,
		final Class<? extends Part<A>> baseClass,
		final String partTypeId,
		final AppContext appContext,
		final DSLContext dslContext
	) {
		this.appContext = appContext;
		appContext.addPartRepository(partTypeId, intfClass, this);
		this.dslContext = dslContext;
		this.proxyFactory = new ProxyFactory();
		proxyFactory.setSuperclass(baseClass);
		proxyFactory.setFilter(PropertyFilter.INSTANCE);
		this.paramTypeList = new Class<?>[] { aggregateIntfClass, UpdatableRecord.class };
	}
	//@formatter:on

	protected AppContext getAppContext() {
		return this.appContext;
	}

	protected void require(boolean condition, String message) {
		Assert.isTrue(condition, "Precondition failed: " + message);
	}

	protected CodePartListType getPartListType(String partListTypeId) {
		return this.getAppContext().getPartListType(partListTypeId);
	}

	boolean isInitialised(A aggregate) {
		require(aggregate != null, "aggregate not null");
		return this.cache.containsKey(aggregate);
	}

	private void initParts(A aggregate) {
		require(aggregate != null, "aggregate not null");
		require(!this.isInitialised(aggregate), this.getClass().getName() + ": aggregate not yet initialised");
		this.cache.put(aggregate, new ArrayList<>());
		Assert.isTrue(this.isInitialised(aggregate), this.getClass().getName() + ": aggregate initialised");
	}

	List<P> getParts(A aggregate) {
		require(aggregate != null, "aggregate not null");
		require(this.isInitialised(aggregate), this.getClass().getSimpleName() + ": aggregate initialised");
		return this.cache.get(aggregate);
	}

	private void clearParts(A aggregate) {
		this.cache.remove(aggregate);
	}

	void addPart(P part) {
		A aggregate = part.getMeta().getAggregate();
		require(this.isInitialised(aggregate),
				this.getClass().getSimpleName() + ": aggregate " + aggregate.getId() + " initialised");
		this.cache.get(aggregate).add(part);
	}

	@SuppressWarnings("unchecked")
	protected P newPart(A aggregate, UpdatableRecord<?> dbRecord) {
		P part = null;
		try {
			part = (P) this.proxyFactory.create(paramTypeList, new Object[] { aggregate, dbRecord },
					PropertyHandler.INSTANCE);
		} catch (NoSuchMethodException | IllegalArgumentException | InstantiationException | IllegalAccessException
				| InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException("could not create part " + this.getClass().getSimpleName());
		}
		return part;
	}

	@Override
	public void init(A aggregate) {
		this.initParts(aggregate);
	}

	@Override
	public void load(A aggregate) {
		this.initParts(aggregate);
		this.doLoad(aggregate).forEach(p -> this.addPart(p));
	}

	@Override
	public abstract List<P> doLoad(A aggregate);

	private boolean isAggregateLevel(Part<?> part) {
		return part.getParentPartId() == null || part.getParentPartId() == 0;
	}

	@Override
	public List<P> getPartList(A aggregate, CodePartListType partListType) {
		return this.getParts(aggregate).stream()
				.filter(p -> isAggregateLevel(p) && partListType.getId().equals(p.getPartListTypeId())).toList();
	}

	@Override
	public List<P> getPartList(A aggregate, Part<?> parent, CodePartListType partListType) {
		return this.getParts(aggregate).stream()
				.filter(p -> p.getParentPartId() == parent.getId() && partListType.getId().equals(p.getPartListTypeId()))
				.toList();
	}

	@Override
	public boolean hasPartId() {
		return true;
	}

	@Override
	public abstract Integer nextPartId();

	@Override
	public P create(A aggregate, CodePartListType partListType) {
		return this.create(aggregate, null, partListType);
	}

	@Override
	public P create(A aggregate, Part<?> parent, CodePartListType partListType) {
		P p = this.doCreate(aggregate);
		Assert.isTrue(p != null, "part created");
		this.didDoInit = false;
		this.doInit(p, this.hasPartId() ? this.nextPartId() : null, aggregate, parent, partListType);
		Assert.isTrue(this.didDoInit, this.getClass().getSimpleName() + ": doInit was called");
		this.addPart(p);
		this.doAfterCreate(p);
		return p;
	}

	@Override
	public abstract P doCreate(A aggregate);

	@Override
	@SuppressWarnings("unchecked")
	public void doInit(P p, Integer partId, A aggregate, Part<?> parent, CodePartListType partListType) {
		this.didDoInit = true;
		((PartSPI<A>) p).doInit(partId, aggregate, parent, partListType);
	}

	@SuppressWarnings("unchecked")
	public final void doAfterCreate(P p) {
		((PartSPI<A>) p).afterCreate();
	}

	@SuppressWarnings("unchecked")
	public void store(A aggregate) {
		require(this.isInitialised(aggregate), this.getClass().getSimpleName() + ": aggregate initialised");
		this.beforeStore(aggregate);
		for (P part : this.getParts(aggregate)) {
			((PartSPI<A>) part).store();
		}
	}

	@Override
	public void beforeStore(A aggregate) {
	}

	@EventListener
	@SuppressWarnings("unchecked")
	public void handleAggregateStoredEvent(AggregateStoredEvent event) {
		this.clearParts((A) event.getAggregate());
	}

}
