
package io.zeitwert.ddd.part.model.base;

import static io.zeitwert.ddd.util.Check.assertThis;
import static io.zeitwert.ddd.util.Check.requireThis;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.springframework.context.event.EventListener;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.base.AggregateBase;
import io.zeitwert.ddd.app.event.AggregateStoredEvent;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.PartPersistenceProvider;
import io.zeitwert.ddd.part.model.PartPersistenceStatus;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.persistence.jooq.PartState;
import io.zeitwert.ddd.property.model.PropertyProvider;
import io.zeitwert.ddd.property.model.impl.PropertyFilter;
import io.zeitwert.ddd.property.model.impl.PropertyHandler;
import javassist.util.proxy.ProxyFactory;

public abstract class PartRepositoryBase<A extends Aggregate, P extends Part<A>>
		implements PartRepository<A, P>, PartRepositorySPI<A, P> {

	private final Class<? extends Part<A>> intfClass;

	private final ProxyFactory proxyFactory;
	private final Class<?>[] paramTypeList;

	protected PartRepositoryBase(
			Class<? extends A> aggregateIntfClass,
			Class<? extends Part<A>> intfClass,
			Class<? extends Part<A>> baseClass,
			String partTypeId,
			AppContext appContext) {
		this.intfClass = intfClass;
		appContext.addPartRepository(partTypeId, intfClass, this);
		this.proxyFactory = new ProxyFactory();
		this.proxyFactory.setSuperclass(baseClass);
		this.proxyFactory.setFilter(PropertyFilter.INSTANCE);
		this.paramTypeList = new Class<?>[] { PartRepository.class, aggregateIntfClass, PartState.class };
	}

	@Override
	@SuppressWarnings("unchecked")
	public final PartPersistenceProvider<A, P> getPersistenceProvider() {
		return (PartPersistenceProvider<A, P>) AppContext.getInstance().getPartPersistenceProvider(this.intfClass);
	}

	@Override
	public final PropertyProvider getPropertyProvider() {
		return AppContext.getInstance().getPropertyProvider(this.intfClass);
	}

	@Override
	public boolean hasPartId() {
		return true;
	}

	private final boolean hasPartCache(A aggregate) {
		return ((AggregateBase) aggregate).hasPartCache(this.intfClass);
	}

	@Override
	public final void init(A aggregate) {
		((AggregateBase) aggregate).initPartCache(this.intfClass);
	}

	@SuppressWarnings("unchecked")
	private final PartCache<P> getPartCache(A aggregate) {
		return (PartCache<P>) ((AggregateBase) aggregate).getPartCache(this.intfClass);
	}

	@Override
	@SuppressWarnings("unchecked")
	public final P newPart(A aggregate, PartState partState) {
		P part = null;
		try {
			part = (P) this.proxyFactory.create(this.paramTypeList, new Object[] { this, aggregate, partState },
					PropertyHandler.INSTANCE);
		} catch (NoSuchMethodException | IllegalArgumentException | InstantiationException | IllegalAccessException
				| InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException("could not create part " + this.getClassName());
		}
		return part;
	}

	@Override
	public final P create(A aggregate, CodePartListType partListType) {
		return this.create(aggregate, null, partListType);
	}

	@Override
	@SuppressWarnings("unchecked")
	public final P create(Part<?> parent, CodePartListType partListType) {
		A aggregate = (A) parent.getMeta().getAggregate();
		return this.create(aggregate, parent, partListType);
	}

	@SuppressWarnings("unchecked")
	private P create(A aggregate, Part<?> parent, CodePartListType partListType) {

		requireThis(this.hasPartCache(aggregate), this.getClassName() + ": aggregate initialised");

		PartPersistenceProvider<A, P> persistenceProvider = this.getPersistenceProvider();
		Integer partId = this.hasPartId() ? persistenceProvider.nextPartId() : null;
		P p = persistenceProvider.doCreate(aggregate);
		assertThis(p != null, "part created");
		if (p == null) {
			return null; // make compiler happy (potential null pointer)
		}

		persistenceProvider.doInit(p, partId, aggregate, parent, partListType);

		assertThis(!this.hasPartId() || PartPersistenceStatus.CREATED == p.getMeta().getPersistenceStatus(),
				"status CREATED");

		p.calcAll();
		this.getPartCache(aggregate).addPart(p);

		Integer doAfterCreateSeqNr = ((PartBase<?>) p).doAfterCreateSeqNr;
		((PartSPI<A>) p).doAfterCreate();
		assertThis(((PartBase<?>) p).doAfterCreateSeqNr > doAfterCreateSeqNr,
				p.getClass().getSimpleName() + ": doAfterCreate was propagated");

		return p;
	}

	@Override
	public final void load(A aggregate) {

		PartPersistenceProvider<A, P> persistenceProvider = this.getPersistenceProvider();
		List<P> parts = persistenceProvider.doLoad(aggregate);

		parts.forEach(p -> this.getPartCache(aggregate).addPart(p));
		for (P part : parts) {
			Integer doAssignPartsSeqNr = ((PartBase<?>) part).doAssignPartsSeqNr;
			((PartSPI<?>) part).doAssignParts();
			assertThis(((PartBase<?>) part).doAssignPartsSeqNr > doAssignPartsSeqNr,
					this.getClassName(part) + ": doAssignParts was propagated");
		}
		for (P part : parts) {
			part.calcVolatile();
		}
		for (P part : parts) {
			Integer doAfterLoadSeqNr = ((PartBase<?>) part).doAfterLoadSeqNr;
			((PartSPI<?>) part).doAfterLoad();
			assertThis(((PartBase<?>) part).doAfterLoadSeqNr > doAfterLoadSeqNr,
					this.getClassName(part) + ": doAfterLoad was propagated");
		}
	}

	@Override
	public List<P> getParts(A aggregate, CodePartListType partListType) {
		return this.getPartCache(aggregate).getParts().stream()
				.filter(p -> this.isAggregateLevel(p) && partListType.getId().equals(p.getMeta().getPartListTypeId())).toList();
	}

	private boolean isAggregateLevel(Part<?> part) {
		return part.getMeta().getParentPartId() == null || part.getMeta().getParentPartId() == 0;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<P> getParts(Part<?> parent, CodePartListType partListType) {
		A aggregate = (A) parent.getMeta().getAggregate();
		return this.getPartCache(aggregate).getParts().stream()
				.filter(p -> (p.getMeta().getParentPartId().equals(parent.getId()))
						&& partListType.getId().equals(p.getMeta().getPartListTypeId()))
				.toList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public final void store(A aggregate) {
		requireThis(this.hasPartCache(aggregate), this.getClassName() + ": aggregate initialised");
		PartPersistenceProvider<A, P> persistenceProvider = this.getPersistenceProvider();
		List<P> allParts = this.getPartCache(aggregate).getParts();
		List<P> activeParts = allParts.stream()
				.filter(p -> p.getMeta().getPersistenceStatus() != PartPersistenceStatus.DELETED)
				.toList();
		for (P part : activeParts) {
			Integer doBeforeStoreSeqNr = ((PartBase<?>) part).doBeforeStoreSeqNr;
			((PartSPI<A>) part).doBeforeStore();
			assertThis(((PartBase<?>) part).doBeforeStoreSeqNr > doBeforeStoreSeqNr,
					this.getClassName(part) + ": doBeforeStore was propagated");
		}
		for (P part : allParts) {
			persistenceProvider.doStore(part);
		}
		for (P part : activeParts) {
			Integer doAfterStoreSeqNr = ((PartBase<?>) part).doAfterStoreSeqNr;
			((PartSPI<A>) part).doAfterStore();
			assertThis(((PartBase<?>) part).doAfterStoreSeqNr > doAfterStoreSeqNr,
					this.getClassName(part) + ": doAfterStore was propagated");
		}
	}

	@EventListener
	@SuppressWarnings("unchecked")
	public void handleAggregateStoredEvent(AggregateStoredEvent event) {
		A aggregate = (A) event.getAggregate();
		if (this.hasPartCache(aggregate)) {
			this.getPartCache(aggregate).clearParts();
		}
	}

	protected String getClassName() {
		return this.getClass().getSimpleName();
	}

	protected String getClassName(P part) {
		return part.getClass().getSimpleName();
	}

}
