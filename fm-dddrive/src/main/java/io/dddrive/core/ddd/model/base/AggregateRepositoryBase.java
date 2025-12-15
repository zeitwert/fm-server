package io.dddrive.core.ddd.model.base;

import static io.dddrive.util.Invariant.assertThis;
import static io.dddrive.util.Invariant.requireThis;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.dddrive.core.ddd.model.Aggregate;
import io.dddrive.core.ddd.model.AggregateMeta;
import io.dddrive.core.ddd.model.AggregateRepository;
import io.dddrive.core.ddd.model.AggregateRepositorySPI;
import io.dddrive.core.ddd.model.AggregateSPI;
import io.dddrive.core.ddd.model.Part;
import io.dddrive.core.ddd.model.PartRepository;
import io.dddrive.core.ddd.model.RepositoryDirectory;
import io.dddrive.core.ddd.model.RepositoryDirectorySPI;
import io.dddrive.core.ddd.model.enums.CodeAggregateType;
import io.dddrive.core.ddd.model.enums.CodeAggregateTypeEnum;
import io.dddrive.core.ddd.model.impl.PartRepositoryImpl;
import io.dddrive.core.property.model.impl.PropertyFilter;
import io.dddrive.core.property.model.impl.PropertyHandler;
import javassist.util.proxy.ProxyFactory;

public abstract class AggregateRepositoryBase<A extends Aggregate>
		implements AggregateRepository<A>, AggregateRepositorySPI<A> {

	private static final Set<String> NotLoggedProperties = Set.of("id", "maxPartId", "version", "createdByUser",
			"createdAt", "modifiedByUser", "modifiedAt");

	private final Class<? extends Aggregate> baseClass;
	private final String aggregateTypeId;
	private final ProxyFactory aggregateProxyFactory;
	private final Class<?>[] aggregateProxyFactoryParamTypeList;
	private final Cache<Object, A> objCache = Caffeine.newBuilder().maximumSize(200).recordStats().build();
	private boolean didAfterCreate = false;
	private boolean didAfterLoad = false;
	private boolean didBeforeStore = false;
	private boolean didAfterStore = false;

	protected AggregateRepositoryBase(
			Class<? extends AggregateRepository<A>> repoIntfClass,
			Class<? extends Aggregate> intfClass,
			Class<? extends Aggregate> baseClass,
			String aggregateTypeId) {
		this.baseClass = baseClass;
		this.aggregateTypeId = aggregateTypeId;
		this.aggregateProxyFactory = new ProxyFactory();
		this.aggregateProxyFactory.setSuperclass(baseClass);
		this.aggregateProxyFactory.setFilter(PropertyFilter.INSTANCE);
		this.aggregateProxyFactoryParamTypeList = new Class<?>[] { repoIntfClass, Boolean.TYPE };
		((RepositoryDirectorySPI) this.getDirectory()).addRepository(intfClass, this);
		this.registerParts();
	}

	@Override
	public final CodeAggregateType getAggregateType() {
		return CodeAggregateTypeEnum.getAggregateType(this.aggregateTypeId);
	}

	@Override
	public final RepositoryDirectory getDirectory() {
		return RepositoryDirectory.getInstance();
	}

	@Override
	public final String idToString(Object id) {
		return id == null ? null : this.getPersistenceProvider().idToString(id);
	}

	@Override
	public final Object idFromString(String id) {
		return id == null ? null : this.getPersistenceProvider().idFromString(id);
	}

	@Override
	public boolean doLogChange(String propertyName) {
		return !NotLoggedProperties.contains(propertyName);
	}

	protected <AA extends Aggregate> void addPart(Class<AA> aggregateIntfClass, Class<? extends Part<AA>> partIntfClass,
			Class<? extends Part<AA>> partBaseClass) {
		PartRepository<AA, ? extends Part<AA>> partRepository = new PartRepositoryImpl<>(aggregateIntfClass, partIntfClass,
				partBaseClass);
		((RepositoryDirectorySPI) this.getDirectory()).addPartRepository(partIntfClass, partRepository);
	}

	@Override
	public final A create(Object tenantId, Object userId, OffsetDateTime timestamp) {

		requireThis(tenantId != null, "tenantId not null");
		requireThis(userId != null, "userId not null");
		requireThis(timestamp != null, "timestamp not null");

		AggregatePersistenceProvider<A> persistenceProvider = this.getPersistenceProvider();
		Object aggregateId = persistenceProvider.nextAggregateId();
		A aggregate = this.createAggregate(true);

		Integer doCreateSeqNr = ((AggregateBase) aggregate).doCreateSeqNr;
		((AggregateSPI) aggregate).doCreate(aggregateId, tenantId, userId, timestamp);
		assertThis(((AggregateBase) aggregate).doCreateSeqNr > doCreateSeqNr,
				this.getBaseClassName(aggregate) + ": doCreate was propagated");

		aggregate.calcAll();

		this.didAfterCreate = false;
		this.doAfterCreate(aggregate, userId, timestamp);
		assertThis(this.didAfterCreate, this.getBaseClassName() + ": doAfterCreate was propagated");

		return aggregate;
	}

	@Override
	public void doAfterCreate(A aggregate, Object userId, OffsetDateTime timestamp) {
		this.didAfterCreate = true;
		Integer doAfterCreateSeqNr = ((AggregateBase) aggregate).doAfterCreateSeqNr;
		((AggregateSPI) aggregate).doAfterCreate(userId, timestamp);
		assertThis(((AggregateBase) aggregate).doAfterCreateSeqNr > doAfterCreateSeqNr,
				this.getBaseClassName(aggregate) + ": doAfterCreate was propagated");
	}

	@Override
	public final A get(Object id) {
		if (id == null) {
			return null;
		}
		return this.objCache.get(id, (aggrId) -> this.get(aggrId, true));
	}

	@Override
	public final A load(Object id) {
		return this.get(id, false);
	}

	private A get(Object id, boolean isFrozen) {

		requireThis(id != null, "id not null");
		AggregatePersistenceProvider<A> persistenceProvider = this.getPersistenceProvider();
		requireThis(persistenceProvider.isValidId(id), "valid id " + id + " (" + id.getClass().getSimpleName() + ")");

		A aggregate = this.createAggregate(false);
		((AggregateMeta) aggregate).beginLoad();
		persistenceProvider.doLoad(aggregate, id);
		((AggregateMeta) aggregate).endLoad();

		aggregate.calcVolatile();

		if (isFrozen) {
			((AggregateBase) aggregate).freeze();
		}

		this.didAfterLoad = false;
		this.doAfterLoad(aggregate);
		assertThis(this.didAfterLoad, this.getBaseClassName() + ": doAfterLoad was propagated");

		return aggregate;
	}

	@SuppressWarnings("unchecked")
	private A createAggregate(boolean isNew) {
		try {
			return (A) this.aggregateProxyFactory.create(this.aggregateProxyFactoryParamTypeList,
					new Object[] { this, isNew }, PropertyHandler.INSTANCE);
		} catch (ReflectiveOperationException | RuntimeException e) {
			throw new RuntimeException("Could not create aggregate " + this.getBaseClassName(), e);
		}
	}

	@Override
	public void doAfterLoad(A aggregate) {
		this.didAfterLoad = true;
		Integer doAfterLoadSeqNr = ((AggregateBase) aggregate).doAfterLoadSeqNr;
		((AggregateSPI) aggregate).doAfterLoad();
		assertThis(((AggregateBase) aggregate).doAfterLoadSeqNr > doAfterLoadSeqNr,
				this.getBaseClassName(aggregate) + ": doAfterLoad was propagated");
	}

	@Override
	public final void store(A aggregate, Object userId, OffsetDateTime timestamp) {

		try {

			this.didBeforeStore = false;
			this.doBeforeStore(aggregate, userId, timestamp);
			assertThis(this.didBeforeStore, this.getBaseClassName() + ": doBeforeStore was propagated");

			this.getPersistenceProvider().doStore(aggregate);

			this.didAfterStore = false;
			this.doAfterStore(aggregate);
			assertThis(this.didAfterStore, this.getBaseClassName() + ": doAfterStore was propagated");

		} catch (Exception e) {
			throw new RuntimeException(this.getBaseClassName() + ": could not store aggregate", e);
		}

	}

	@Override
	public void doBeforeStore(A aggregate, Object userId, OffsetDateTime timestamp) {
		this.didBeforeStore = true;
		Integer doBeforeStoreSeqNr = ((AggregateBase) aggregate).doBeforeStoreSeqNr;
		((AggregateSPI) aggregate).doBeforeStore(userId, timestamp);
		assertThis(((AggregateBase) aggregate).doBeforeStoreSeqNr > doBeforeStoreSeqNr,
				this.getBaseClassName(aggregate) + ": doBeforeStore was propagated");
	}

	@Override
	public void doAfterStore(A aggregate) {
		this.didAfterStore = true;
		Integer doAfterStoreSeqNr = ((AggregateBase) aggregate).doAfterStoreSeqNr;
		((AggregateSPI) aggregate).doAfterStore();
		assertThis(((AggregateBase) aggregate).doAfterStoreSeqNr > doAfterStoreSeqNr,
				this.getBaseClassName(aggregate) + ": doAfterStore was propagated");
		this.handleAggregateStored(aggregate.getId());
	}

	@Override
	public List<Object> getAll(Object tenantId) {
		return this.getPersistenceProvider().getAll(tenantId);
	}

	@Override
	public List<Object> getByForeignKey(String fkName, Object targetId) {
		return this.getPersistenceProvider().getByForeignKey(fkName, targetId);
	}

	protected String getBaseClassName() {
		return this.baseClass.getSimpleName();
	}

	protected String getBaseClassName(A aggregate) {
		return aggregate.getClass().getSuperclass().getSimpleName();
	}

	public void handleAggregateStored(Object id) {
		if (this.objCache.getIfPresent(id) != null) {
			this.objCache.invalidate(id);
		}
	}

}
