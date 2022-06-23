
package io.zeitwert.ddd.part.model.base;

import static io.zeitwert.ddd.util.Check.assertThis;
import static io.zeitwert.ddd.util.Check.requireThis;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;
import org.springframework.context.event.EventListener;

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
	@SuppressWarnings("unchecked")
	public final P create(Part<?> parent, CodePartListType partListType) {
		A aggregate = (A) parent.getMeta().getAggregate();
		return this.create(aggregate, parent, partListType);
	}

	@SuppressWarnings("unchecked")
	private P create(A aggregate, Part<?> parent, CodePartListType partListType) {

		requireThis(this.partCache.isInitialised(aggregate),
				this.getClass().getSimpleName() + ": aggregate " + aggregate.getId() + " initialised");

		P p = this.doCreate(aggregate);
		assertThis(p != null, "part created");

		Integer partId = this.hasPartId() ? this.nextPartId() : null;
		Integer doInitSeqNr = ((PartBase<?>) p).doInitSeqNr;
		((PartSPI<A>) p).doInit(partId, aggregate, parent, partListType);
		assertThis(((PartBase<?>) p).doInitSeqNr > doInitSeqNr, p.getClass().getSimpleName() + ": doInit was propagated");
		assertThis(!this.hasPartId() || PartStatus.CREATED == p.getMeta().getStatus(), "status CREATED");

		p.calcAll();
		this.partCache.addPart(p);

		Integer doAfterCreateSeqNr = ((PartBase<?>) p).doAfterCreateSeqNr;
		((PartSPI<A>) p).doAfterCreate();
		assertThis(((PartBase<?>) p).doAfterCreateSeqNr > doAfterCreateSeqNr,
				p.getClass().getSimpleName() + ": doAfterCreate was propagated");

		return p;
	}

	@Override
	public abstract P doCreate(A aggregate);

	@Override
	public final void load(A aggregate) {
		List<P> parts = this.doLoad(aggregate);
		parts.forEach(p -> this.partCache.addPart(p));
		for (P part : parts) {
			Integer doAssignPartsSeqNr = ((PartBase<?>) part).doAssignPartsSeqNr;
			((PartSPI<?>) part).doAssignParts();
			assertThis(((PartBase<?>) part).doAssignPartsSeqNr > doAssignPartsSeqNr,
					part.getClass().getSimpleName() + ": doAssignParts was propagated");
		}
		for (P part : parts) {
			part.calcVolatile();
		}
		for (P part : parts) {
			Integer doAfterLoadSeqNr = ((PartBase<?>) part).doAfterLoadSeqNr;
			((PartSPI<?>) part).doAfterLoad();
			assertThis(((PartBase<?>) part).doAfterLoadSeqNr > doAfterLoadSeqNr,
					part.getClass().getSimpleName() + ": doAfterLoad was propagated");
		}
	}

	@Override
	public abstract List<P> doLoad(A aggregate);

	@Override
	public List<P> getPartList(A aggregate, CodePartListType partListType) {
		return this.partCache.getParts(aggregate).stream()
				.filter(p -> isAggregateLevel(p) && partListType.getId().equals(p.getPartListTypeId())).toList();
	}

	private boolean isAggregateLevel(Part<?> part) {
		return part.getParentPartId() == null || part.getParentPartId() == 0;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<P> getPartList(Part<?> parent, CodePartListType partListType) {
		A aggregate = (A) parent.getMeta().getAggregate();
		return this.partCache.getParts(aggregate).stream()
				.filter(p -> (p.getParentPartId().equals(parent.getId())) && partListType.getId().equals(p.getPartListTypeId()))
				.toList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public final void store(A aggregate) {
		requireThis(this.partCache.isInitialised(aggregate), this.getClass().getSimpleName() + ": aggregate initialised");
		List<P> allParts = this.partCache.getParts(aggregate);
		List<P> activeParts = allParts.stream().filter(p -> p.getMeta().getStatus() != PartStatus.DELETED).toList();
		for (P part : activeParts) {
			Integer doBeforeStoreSeqNr = ((PartBase<?>) part).doBeforeStoreSeqNr;
			((PartSPI<A>) part).doBeforeStore();
			assertThis(((PartBase<?>) part).doBeforeStoreSeqNr > doBeforeStoreSeqNr,
					part.getClass().getSimpleName() + ": doBeforeStore was propagated");
		}
		for (P part : allParts) {
			Integer doStoreSeqNr = ((PartBase<?>) part).doStoreSeqNr;
			((PartSPI<A>) part).doStore();
			assertThis(((PartBase<?>) part).doStoreSeqNr > doStoreSeqNr,
					part.getClass().getSimpleName() + ": doStore was propagated");
		}
		for (P part : activeParts) {
			Integer doAfterStoreSeqNr = ((PartBase<?>) part).doAfterStoreSeqNr;
			((PartSPI<A>) part).doAfterStore();
			assertThis(((PartBase<?>) part).doAfterStoreSeqNr > doAfterStoreSeqNr,
					part.getClass().getSimpleName() + ": doAfterStore was propagated");
		}
	}

	@EventListener
	@SuppressWarnings("unchecked")
	public void handleAggregateStoredEvent(AggregateStoredEvent event) {
		this.partCache.clearParts((A) event.getAggregate());
	}

}
