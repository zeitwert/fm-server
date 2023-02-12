
package io.dddrive.ddd.model.base;

import static io.dddrive.util.Invariant.assertThis;
import static io.dddrive.util.Invariant.requireThis;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.context.ApplicationEvent;

import io.crnk.core.queryspec.FilterOperator;
import io.crnk.core.queryspec.PathSpec;
import io.crnk.core.queryspec.QuerySpec;
import io.dddrive.app.event.AggregateStoredEvent;
import io.dddrive.app.service.api.AppContext;
import io.dddrive.app.service.api.AppContextSPI;
import io.dddrive.ddd.model.Aggregate;
import io.dddrive.ddd.model.AggregatePersistenceProvider;
import io.dddrive.ddd.model.AggregateRepository;
import io.dddrive.ddd.model.PartRepository;
import io.dddrive.ddd.model.enums.CodeAggregateType;
import io.dddrive.ddd.model.enums.CodeAggregateTypeEnum;
import io.dddrive.property.model.PropertyProvider;
import io.dddrive.property.model.impl.PropertyFilter;
import io.dddrive.property.model.impl.PropertyHandler;
import io.dddrive.search.service.api.SearchService;
import javassist.util.proxy.ProxyFactory;

public abstract class AggregateRepositoryBase<A extends Aggregate, V extends Object>
		implements AggregateRepository<A, V>, AggregateRepositorySPI<A, V> {

	private final Class<? extends Aggregate> intfClass;
	private final String aggregateTypeId;
	private final AppContext appContext;

	private final List<PartRepository<? super A, ?>> partRepositories = new ArrayList<>();

	private final ProxyFactory proxyFactory;
	private final Class<?>[] proxyFactoryParamTypeList;

	private boolean didAfterCreate = false;
	private boolean didAfterLoad = false;
	private boolean didBeforeStore = false;
	private boolean didAfterStore = false;

	protected AggregateRepositoryBase(
			Class<? extends AggregateRepository<A, V>> repoIntfClass,
			Class<? extends Aggregate> intfClass,
			Class<? extends Aggregate> baseClass,
			String aggregateTypeId,
			AppContext appContext) {
		this.intfClass = intfClass;
		this.aggregateTypeId = aggregateTypeId;
		this.appContext = appContext;
		this.proxyFactory = new ProxyFactory();
		this.proxyFactory.setSuperclass(baseClass);
		this.proxyFactory.setFilter(PropertyFilter.INSTANCE);
		this.proxyFactoryParamTypeList = new Class<?>[] { repoIntfClass, Object.class };
		((AppContextSPI) appContext).addRepository(intfClass, this);
	}

	@Override
	@SuppressWarnings("unchecked")
	public final AggregatePersistenceProvider<A> getPersistenceProvider() {
		return (AggregatePersistenceProvider<A>) this.appContext.getAggregatePersistenceProvider(this.intfClass);
	}

	@Override
	public final PropertyProvider getPropertyProvider() {
		return this.appContext.getPropertyProvider(this.intfClass);
	}

	@Override
	@SuppressWarnings("unchecked")
	public final Class<A> getAggregateClass() {
		return (Class<A>) this.intfClass;
	}

	@Override
	public final CodeAggregateType getAggregateType() {
		return CodeAggregateTypeEnum.getAggregateType(this.aggregateTypeId);
	}

	@Override
	public final AppContext getAppContext() {
		return this.appContext;
	}

	protected void addPartRepository(PartRepository<? super A, ?> partRepository) {
		requireThis(partRepository != null, "partRepository is not null");
		this.partRepositories.add(partRepository);
	}

	public boolean hasAccountId() {
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public final A newAggregate(Object state) {
		A aggregate = null;
		try {
			Object[] params = new Object[] { this, state };
			aggregate = (A) this.proxyFactory.create(this.proxyFactoryParamTypeList, params, PropertyHandler.INSTANCE);
		} catch (NoSuchMethodException | IllegalArgumentException | InstantiationException | IllegalAccessException
				| InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException(this.getClassName() + ": could not create aggregate");
		}
		return aggregate;
	}

	@Override
	public final A create(Integer tenantId) {

		AggregatePersistenceProvider<A> persistenceProvider = this.getPersistenceProvider();
		Integer aggregateId = persistenceProvider.nextAggregateId();
		A aggregate = persistenceProvider.doCreate();

		Integer doInitSeqNr = ((AggregateBase) aggregate).doInitSeqNr;
		((AggregateSPI) aggregate).doInit(aggregateId, tenantId);
		assertThis(((AggregateBase) aggregate).doInitSeqNr > doInitSeqNr,
				this.getClassName(aggregate) + ": doInit was propagated");

		this.doInitParts(aggregate);

		aggregate.calcAll();

		this.didAfterCreate = false;
		this.doAfterCreate(aggregate);
		assertThis(this.didAfterCreate, this.getClassName() + ": doAfterCreate was propagated");

		return aggregate;
	}

	@Override
	public final void doInitParts(A aggregate) {
		for (PartRepository<? super A, ?> partRepo : this.partRepositories) {
			partRepo.init(aggregate);
		}
	}

	@Override
	public void doAfterCreate(A aggregate) {
		this.didAfterCreate = true;
		Integer doAfterCreateSeqNr = ((AggregateBase) aggregate).doAfterCreateSeqNr;
		((AggregateSPI) aggregate).doAfterCreate();
		assertThis(((AggregateBase) aggregate).doAfterCreateSeqNr > doAfterCreateSeqNr,
				this.getClassName(aggregate) + ": doAfterCreate was propagated");
	}

	@Override
	public final A get(Integer id) {

		requireThis(id != null, "id not null");
		AggregatePersistenceProvider<A> persistenceProvider = this.getPersistenceProvider();
		A aggregate = persistenceProvider.doLoad(id);

		this.doInitParts(aggregate);
		this.doLoadParts(aggregate);

		Integer doAssignPartsSeqNr = ((AggregateBase) aggregate).doAssignPartsSeqNr;
		((AggregateSPI) aggregate).doAssignParts();
		assertThis(((AggregateBase) aggregate).doAssignPartsSeqNr > doAssignPartsSeqNr,
				this.getClassName(aggregate) + ": doAssignParts was propagated");

		aggregate.calcVolatile();

		this.didAfterLoad = false;
		this.doAfterLoad(aggregate);
		assertThis(this.didAfterLoad, this.getClassName() + ": doAfterLoad was propagated");

		return aggregate;
	}

	@Override
	public final void doLoadParts(A aggregate) {
		List<PartRepository<? super A, ?>> repos = new ArrayList<>(this.partRepositories);
		Collections.reverse(repos);
		for (PartRepository<? super A, ?> partRepo : repos) {
			partRepo.load(aggregate);
		}
	}

	@Override
	public void doAfterLoad(A aggregate) {
		this.didAfterLoad = true;
		Integer doAfterLoadSeqNr = ((AggregateBase) aggregate).doAfterLoadSeqNr;
		((AggregateSPI) aggregate).doAfterLoad();
		assertThis(((AggregateBase) aggregate).doAfterLoadSeqNr > doAfterLoadSeqNr,
				this.getClassName(aggregate) + ": doAfterLoad was propagated");
	}

	@Override
	public final void discard(A aggregate) {
		((AggregateBase) aggregate).setStale();
	}

	@Override
	public final void store(A aggregate) {

		this.didBeforeStore = false;
		this.doBeforeStore(aggregate);
		assertThis(this.didBeforeStore, this.getClassName() + ": doBeforeStore was propagated");

		this.getPersistenceProvider().doStore(aggregate);
		this.doStoreParts(aggregate);
		this.storeSearch(aggregate);

		this.didAfterStore = false;
		this.doAfterStore(aggregate);
		assertThis(this.didAfterStore, this.getClassName() + ": doAfterStore was propagated");

	}

	@Override
	public void doBeforeStore(A aggregate) {
		this.didBeforeStore = true;
		Integer doBeforeStoreSeqNr = ((AggregateBase) aggregate).doBeforeStoreSeqNr;
		((AggregateSPI) aggregate).doBeforeStore();
		assertThis(((AggregateBase) aggregate).doBeforeStoreSeqNr > doBeforeStoreSeqNr,
				this.getClassName(aggregate) + ": doBeforeStore was propagated");
	}

	@Override
	public final void doStoreParts(A aggregate) { // TODO: take away from interface
		for (PartRepository<? super A, ?> partRepo : this.partRepositories) {
			partRepo.store(aggregate);
		}
	}

	private final void storeSearch(A aggregate) {
		((AggregateBase) aggregate).doStoreSearch();
	}

	protected final void storeSearch(Aggregate aggregate, List<String> texts, List<String> tokens) {
		this.appContext.getBean(SearchService.class).storeSearch(aggregate, texts, tokens);
	}

	@Override
	public void doAfterStore(A aggregate) {
		this.didAfterStore = true;
		Integer doAfterStoreSeqNr = ((AggregateBase) aggregate).doAfterStoreSeqNr;
		((AggregateSPI) aggregate).doAfterStore();
		assertThis(((AggregateBase) aggregate).doAfterStoreSeqNr > doAfterStoreSeqNr,
				this.getClassName(aggregate) + ": doAfterStore was propagated");
		this.discard(aggregate);
		ApplicationEvent aggregateStoredEvent = new AggregateStoredEvent(aggregate, aggregate);
		this.appContext.publishApplicationEvent(aggregateStoredEvent);
	}

	@Override
	public abstract List<V> doFind(QuerySpec querySpec);

	@Override
	public final List<V> getByForeignKey(String fkName, Integer targetId) {
		QuerySpec querySpec = new QuerySpec(Aggregate.class);
		querySpec.addFilter(PathSpec.of(fkName).filter(FilterOperator.EQ, targetId));
		return this.find(querySpec);
	}

	protected String getClassName() {
		return this.getClass().getSimpleName();
	}

	protected String getClassName(A aggregate) {
		return aggregate.getClass().getSuperclass().getSimpleName();
	}

}
