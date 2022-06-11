
package io.zeitwert.ddd.aggregate.model.base;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.SortField;
import org.jooq.Table;
import org.jooq.UpdatableRecord;
import org.jooq.impl.DSL;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.util.Assert;

import static io.zeitwert.ddd.util.Check.require;

import io.crnk.core.queryspec.FilterOperator;
import io.crnk.core.queryspec.FilterSpec;
import io.crnk.core.queryspec.PathSpec;
import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateType;
import io.zeitwert.ddd.app.event.AggregateStoredEvent;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.property.model.base.PropertyFilter;
import io.zeitwert.ddd.property.model.base.PropertyHandler;
import io.zeitwert.ddd.session.model.SessionCache;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.ddd.session.model.impl.SessionCacheImpl;
import io.zeitwert.ddd.util.SqlUtils;
import javassist.util.proxy.ProxyFactory;

public abstract class AggregateRepositoryBase<A extends Aggregate, V extends Record>
		implements AggregateRepository<A, V>, AggregateRepositorySPI<A, V> {

	private final String aggregateTypeId;
	private final AppContext appContext;
	private final ProxyFactory proxyFactory;
	private final Class<?>[] proxyFactoryParamTypeList;

	private final DSLContext dslContext;
	private final SessionCache<A> aggregateCache = new SessionCacheImpl<>();

	private boolean didDoInitParts = false;
	private boolean didAfterCreate = false;
	private boolean didDoLoadParts = false;
	private boolean didAfterLoad = false;
	private boolean didBeforeStore = false;
	private boolean didDoStoreParts = false;
	private boolean didAfterStore = false;

	//@formatter:off
	protected AggregateRepositoryBase(
		final Class<? extends AggregateRepository<A, V>> repoIntfClass,
		final Class<? extends Aggregate> intfClass,
		final Class<? extends Aggregate> baseClass,
		final String aggregateTypeId,
		final AppContext appContext,
		final DSLContext dslContext
	) {
		this.aggregateTypeId = aggregateTypeId;
		this.appContext = appContext;
		this.dslContext = dslContext;
		this.appContext.addRepository(aggregateTypeId, intfClass, this);
		this.proxyFactory = new ProxyFactory();
		this.proxyFactory.setSuperclass(baseClass);
		this.proxyFactory.setFilter(PropertyFilter.INSTANCE);
		this.proxyFactoryParamTypeList = new Class<?>[] { SessionInfo.class, repoIntfClass, UpdatableRecord.class, UpdatableRecord.class };
	}
	//@formatter:on

	protected final AppContext getAppContext() {
		return this.appContext;
	}

	protected final DSLContext getDSLContext() {
		return this.dslContext;
	}

	@Override
	public final CodeAggregateType getAggregateType() {
		return this.getAppContext().getAggregateType(this.aggregateTypeId);
	}

	protected String getAccountIdField() {
		return null;
	}

	/**
	 * Create a new aggregate, used from both create and load to create a new object
	 */
	@SuppressWarnings("unchecked")
	protected final A newAggregate(SessionInfo sessionInfo, UpdatableRecord<?> objRecord, UpdatableRecord<?> extnRecord) {
		A aggregate = null;
		try {
			aggregate = (A) this.proxyFactory.create(proxyFactoryParamTypeList,
					new Object[] { sessionInfo, this, objRecord, extnRecord }, PropertyHandler.INSTANCE);
		} catch (NoSuchMethodException | IllegalArgumentException | InstantiationException | IllegalAccessException
				| InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException(this.getClass().getSimpleName() + ": could not create aggregate");
		}
		return aggregate;
	}

	@Override
	public abstract Integer nextAggregateId();

	@Override
	public final A create(SessionInfo sessionInfo) {

		Integer aggregateId = this.nextAggregateId();
		A aggregate = this.doCreate(sessionInfo);

		((AggregateSPI) aggregate).doInit(aggregateId, sessionInfo.getTenant().getId());

		this.didDoInitParts = false;
		this.doInitParts(aggregate);
		Assert.isTrue(this.didDoInitParts, this.getClass().getSimpleName() + ": doInitParts was called");

		((AggregateSPI) aggregate).calcAll();
		this.aggregateCache.addItem(sessionInfo, aggregate);

		this.didAfterCreate = false;
		this.doAfterCreate(aggregate);
		Assert.isTrue(this.didAfterCreate, this.getClass().getSimpleName() + ": doAfterCreate was called");

		return aggregate;
	}

	@Override
	public abstract A doCreate(SessionInfo sessionInfo);

	@Override
	public void doInitParts(A aggregate) {
		this.didDoInitParts = true;
	}

	@Override
	public void doAfterCreate(A aggregate) {
		this.didAfterCreate = true;
		((AggregateSPI) aggregate).doAfterCreate();
	}

	@Override
	public final A get(SessionInfo sessionInfo, Integer id) {

		require(id != null, "id not null");
		if (this.aggregateCache.hasItem(sessionInfo, id)) {
			return this.aggregateCache.getItem(sessionInfo, id);
		}

		A aggregate = this.doLoad(sessionInfo, id);

		this.didDoLoadParts = false;
		this.doLoadParts(aggregate);
		Assert.isTrue(this.didDoLoadParts, this.getClass().getSimpleName() + ": doLoadParts was called");

		this.aggregateCache.addItem(sessionInfo, aggregate);
		((AggregateSPI) aggregate).calcVolatile();

		this.didAfterLoad = false;
		this.doAfterLoad(aggregate);
		Assert.isTrue(this.didAfterLoad, this.getClass().getSimpleName() + ": doAfterLoad was called");

		return aggregate;
	}

	@Override
	public abstract A doLoad(SessionInfo sessionInfo, Integer id);

	@Override
	public void doLoadParts(A aggregate) {
		this.didDoLoadParts = true;
	}

	@Override
	public void doAfterLoad(A aggregate) {
		this.didAfterLoad = true;
		((AggregateSPI) aggregate).doAfterLoad();
	}

	@Override
	public final void store(A aggregate) {

		this.didBeforeStore = false;
		this.doBeforeStore(aggregate);
		Assert.isTrue(this.didBeforeStore, this.getClass().getSimpleName() + ": doBeforeStore was called");

		((AggregateSPI) aggregate).doStore();

		this.didDoStoreParts = false;
		this.doStoreParts(aggregate);
		Assert.isTrue(this.didDoStoreParts, this.getClass().getSimpleName() + ": doStoreParts was called");

		this.didAfterStore = false;
		this.doAfterStore(aggregate);
		Assert.isTrue(this.didAfterStore, this.getClass().getSimpleName() + ": doAfterStore was called");

	}

	@Override
	public void doBeforeStore(A aggregate) {
		this.didBeforeStore = true;
		((AggregateSPI) aggregate).doBeforeStore();
	}

	@Override
	public void doStoreParts(A aggregate) {
		this.didDoStoreParts = true;
	}

	@Override
	public void doAfterStore(A aggregate) {
		this.didAfterStore = true;
		((AggregateSPI) aggregate).doAfterStore();
		ApplicationEvent aggregateStoredEvent = new AggregateStoredEvent(aggregate, aggregate);
		this.getAppContext().publishApplicationEvent(aggregateStoredEvent);
	}

	@Override
	public final List<V> find(SessionInfo sessionInfo, QuerySpec querySpec) {
		//@formatter:off
		querySpec.addFilter(PathSpec.of("tenant_id").filter(FilterOperator.EQ, sessionInfo.getTenant().getId()));
		if (this.getAccountIdField() != null && sessionInfo.hasAccount()) {
			Integer accountId = sessionInfo.getAccountId();
			querySpec.addFilter(
				FilterSpec.or(
					PathSpec.of(this.getAccountIdField()).filter(FilterOperator.EQ, accountId),
					PathSpec.of(this.getAccountIdField()).filter(FilterOperator.EQ, null)
				)
			);
		}
		//@formatter:on
		return this.doFind(querySpec);
	}

	@Override
	public abstract List<V> doFind(QuerySpec querySpec);

	@Override
	public final List<V> getByForeignKey(SessionInfo sessionInfo, String fkName, Integer targetId) {
		QuerySpec querySpec = new QuerySpec(Aggregate.class);
		FilterSpec filterSpec = PathSpec.of(fkName).filter(FilterOperator.EQ, targetId);
		querySpec.setFilters(Arrays.asList(filterSpec));
		return this.doFind(querySpec);
	}

	@SuppressWarnings("unchecked")
	protected final List<V> doFind(Table<V> table, Field<Integer> idField, QuerySpec querySpec) {
		Condition whereClause = DSL.noCondition();
		if (querySpec != null) {
			for (FilterSpec filter : querySpec.getFilters()) {
				if (filter.getOperator().equals(FilterOperator.OR) && filter.getExpression() != null) {
					whereClause = SqlUtils.orFilter(dslContext, whereClause, table, idField, filter);
				} else {
					whereClause = SqlUtils.andFilter(this.dslContext, whereClause, table, idField, filter);
				}
			}
		}

		//@formatter:off
		 SelectConditionStep<V> select = (SelectConditionStep<V>) this.dslContext
			.select()
			.from(table)
			.where(whereClause);
		//@formatter:on

		// Sort.
		Result<V> recordList = null;
		List<SortField<?>> sortFields = List.of();
		if (querySpec.getSort().size() > 0) {
			sortFields = SqlUtils.sortFilter(table, querySpec.getSort());
		} else if (table.field("modified_at") != null) {
			sortFields = List.of(table.field("modified_at").desc());
		} else {
			sortFields = List.of(table.field("id").desc());
		}
		recordList = select.orderBy(sortFields).limit(querySpec.getOffset(), querySpec.getLimit()).fetch();

		return recordList;
	}

	@EventListener
	private void handleAggregateStoredEvent(AggregateStoredEvent event) {
		Integer aggregateId = event.getAggregate().getId();
		List<A> itemList = this.aggregateCache.getItemList(aggregateId);
		for (A aggregate : itemList) {
			((AggregateBase) aggregate).setStale();
			if (aggregate.getMeta().getSessionInfo() == event.getAggregate().getMeta().getSessionInfo()) {
				this.aggregateCache.removeItem(aggregate.getMeta().getSessionInfo(), aggregateId);
			}
		}
	}

}
