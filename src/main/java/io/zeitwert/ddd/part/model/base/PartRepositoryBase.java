
package io.zeitwert.ddd.part.model.base;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;
import org.springframework.context.event.EventListener;
import org.springframework.util.Assert;

import static io.zeitwert.ddd.util.Check.require;

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
	private final DSLContext dslContext;

	private final ProxyFactory proxyFactory;
	private final Class<?>[] paramTypeList;

	private final PartCache<A, P> partCache = new PartCache<>();

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
		this.paramTypeList = new Class<?>[] { PartRepository.class, aggregateIntfClass, UpdatableRecord.class };
	}
	//@formatter:on

	protected final AppContext getAppContext() {
		return this.appContext;
	}

	protected final DSLContext getDSLContext() {
		return this.dslContext;
	}

	@Override
	public boolean hasPartId() {
		return true;
	}

	@Override
	public final void init(A aggregate) {
		this.partCache.initParts(aggregate);
	}

	@SuppressWarnings("unchecked")
	protected final P newPart(A aggregate, UpdatableRecord<?> dbRecord) {
		P part = null;
		try {
			part = (P) this.proxyFactory.create(paramTypeList, new Object[] { this, aggregate, dbRecord },
					PropertyHandler.INSTANCE);
		} catch (NoSuchMethodException | IllegalArgumentException | InstantiationException | IllegalAccessException
				| InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException("could not create part " + this.getClass().getSimpleName());
		}
		return part;
	}

	@Override
	public abstract Integer nextPartId();

	@Override
	public final P create(A aggregate, CodePartListType partListType) {
		return this.create(aggregate, null, partListType);
	}

	@Override
	public final P create(A aggregate, Part<?> parent, CodePartListType partListType) {
		P p = this.doCreate(aggregate);
		Assert.isTrue(p != null, "part created");
		this.didDoInit = false;
		this.doInit(p, this.hasPartId() ? this.nextPartId() : null, aggregate, parent, partListType);
		Assert.isTrue(this.didDoInit, this.getClass().getSimpleName() + ": doInit was called");
		Assert.isTrue(!this.hasPartId() || PartStatus.CREATED == p.getMeta().getStatus(), "status CREATED");
		this.partCache.addPart(p);
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
		((PartSPI<A>) p).doAfterCreate();
	}

	@Override
	public final void load(A aggregate) {
		this.partCache.initParts(aggregate);
		this.doLoad(aggregate).forEach(p -> this.partCache.addPart(p));
	}

	@Override
	public abstract List<P> doLoad(A aggregate);

	@Override
	public List<P> getPartList(A aggregate, CodePartListType partListType) {
		return this.partCache.getParts(aggregate).stream()
				.filter(p -> isAggregateLevel(p) && partListType.getId().equals(p.getPartListTypeId())).toList();
	}

	@Override
	public List<P> getPartList(A aggregate, Part<?> parent, CodePartListType partListType) {
		return this.partCache.getParts(aggregate).stream()
				.filter(p -> p.getParentPartId() == parent.getId() && partListType.getId().equals(p.getPartListTypeId()))
				.toList();
	}

	private boolean isAggregateLevel(Part<?> part) {
		return part.getParentPartId() == null || part.getParentPartId() == 0;
	}

	@SuppressWarnings("unchecked")
	public final void store(A aggregate) {
		require(this.partCache.isInitialised(aggregate), this.getClass().getSimpleName() + ": aggregate initialised");
		for (P part : this.partCache.getParts(aggregate)) {
			((PartSPI<A>) part).doStore();
		}
	}

	@EventListener
	@SuppressWarnings("unchecked")
	public void handleAggregateStoredEvent(AggregateStoredEvent event) {
		this.partCache.clearParts((A) event.getAggregate());
	}

}
