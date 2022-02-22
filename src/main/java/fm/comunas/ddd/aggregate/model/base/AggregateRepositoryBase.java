
package fm.comunas.ddd.aggregate.model.base;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

import io.crnk.core.queryspec.FilterOperator;
import io.crnk.core.queryspec.FilterSpec;
import io.crnk.core.queryspec.PathSpec;
import io.crnk.core.queryspec.QuerySpec;
import fm.comunas.ddd.aggregate.model.Aggregate;
import fm.comunas.ddd.aggregate.model.AggregateRepository;
import fm.comunas.ddd.aggregate.model.enums.CodeAggregateType;
import fm.comunas.ddd.app.event.AggregateStoredEvent;
import fm.comunas.ddd.app.service.api.AppContext;
import fm.comunas.ddd.oe.model.ObjTenant;
import fm.comunas.ddd.oe.model.ObjUser;
import fm.comunas.ddd.property.model.base.PropertyFilter;
import fm.comunas.ddd.property.model.base.PropertyHandler;
import fm.comunas.ddd.session.model.SessionCache;
import fm.comunas.ddd.session.model.SessionInfo;
import fm.comunas.ddd.session.model.impl.SessionCacheImpl;
import fm.comunas.ddd.util.SqlUtils;
import javassist.util.proxy.ProxyFactory;

public abstract class AggregateRepositoryBase<A extends Aggregate, V extends Record>
		implements AggregateRepository<A, V>, AggregateRepositorySPI<A, V> {

	private final String aggregateTypeId;
	private final AppContext appContext;
	private final ProxyFactory proxyFactory;
	private final Class<?>[] proxyFactoryParamTypeList;

	protected final DSLContext dslContext;
	private final SessionCache<A> aggregateCache = new SessionCacheImpl<>();

	private boolean didDoLoadParts = false;
	private boolean didDoInit = false;
	private boolean didDoInitParts = false;
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

	protected AppContext getAppContext() {
		return this.appContext;
	}

	protected void require(boolean condition, String message) {
		Assert.isTrue(condition, "Precondition failed: " + message);
	}

	@Override
	public CodeAggregateType getAggregateType() {
		return this.getAppContext().getAggregateType(this.aggregateTypeId);
	}

	@SuppressWarnings("unchecked")
	protected A newAggregate(SessionInfo sessionInfo, UpdatableRecord<?> objRecord, UpdatableRecord<?> extnRecord) {
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
	public final Optional<A> get(SessionInfo sessionInfo, Integer id) {
		require(id != null, "id not null");
		if (this.aggregateCache.hasItem(sessionInfo, id)) {
			return Optional.of(this.aggregateCache.getItem(sessionInfo, id));
		}
		Optional<A> maybeAggregate = this.doLoad(sessionInfo, id);
		if (maybeAggregate.isPresent()) {
			A aggregate = maybeAggregate.get();
			this.didDoLoadParts = false;
			this.doLoadParts(aggregate);
			Assert.isTrue(this.didDoLoadParts, this.getClass().getSimpleName() + ": doLoadParts was called");
			this.aggregateCache.addItem(sessionInfo, aggregate);
			((AggregateSPI) aggregate).calcVolatile();
		}
		return maybeAggregate;
	}

	@Override
	public abstract Optional<A> doLoad(SessionInfo sessionInfo, Integer id);

	@Override
	public void doLoadParts(A aggregate) {
		this.didDoLoadParts = true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<V> find(SessionInfo sessionInfo, QuerySpec querySpec) {
		//@formatter:off
		querySpec.addFilter(PathSpec.of("tenant_id").filter(FilterOperator.EQ, sessionInfo.getTenant().getId()));
		if (this.getCommunityIdField() != null && sessionInfo.getCustomValue("community") != null) {
			String communityId = ((Map<String, Object>) sessionInfo.getCustomValue("community")).get("id").toString();
			querySpec.addFilter(
				FilterSpec.or(
					PathSpec.of(this.getCommunityIdField()).filter(FilterOperator.EQ, communityId),
					PathSpec.of(this.getCommunityIdField()).filter(FilterOperator.EQ, null)
				)
			);
		}
		//@formatter:on
		return this.doFind(querySpec);
	}

	protected String getCommunityIdField() {
		return null;
	}

	@Override
	public abstract List<V> doFind(QuerySpec querySpec);

	@Override
	public List<V> getByForeignKey(SessionInfo sessionInfo, String fkName, Integer targetId) {
		QuerySpec querySpec = new QuerySpec(Aggregate.class);
		FilterSpec filterSpec = PathSpec.of(fkName).filter(FilterOperator.EQ, targetId);
		querySpec.setFilters(Arrays.asList(filterSpec));
		return this.doFind(querySpec);
	}

	@SuppressWarnings("unchecked")
	protected List<V> doFind(Table<V> table, Field<Integer> idField, QuerySpec querySpec) {
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

	@Override
	public abstract Integer nextAggregateId();

	@Override
	public A create(SessionInfo sessionInfo) {
		Integer aggregateId = this.nextAggregateId();
		A aggregate = this.doCreate(sessionInfo);
		this.didDoInit = false;
		this.doInit(aggregate, aggregateId, sessionInfo.getTenant(), sessionInfo.getUser());
		Assert.isTrue(this.didDoInit, this.getClass().getSimpleName() + ": doInit was called");
		this.didDoInitParts = false;
		this.doInitParts(aggregate);
		Assert.isTrue(this.didDoInitParts, this.getClass().getSimpleName() + ": doInitParts was called");
		((AggregateSPI) aggregate).calcAll();
		this.aggregateCache.addItem(sessionInfo, aggregate);
		return aggregate;
	}

	@Override
	public abstract A doCreate(SessionInfo sessionInfo);

	@Override
	public void doInit(A aggregate, Integer aggregateId, ObjTenant tenant, ObjUser user) {
		this.didDoInit = true;
		((AggregateSPI) aggregate).doInit(aggregateId, tenant.getId(), user.getId());
	}

	@Override
	public void doInitParts(A aggregate) {
		this.didDoInitParts = true;
	}

	@Override
	public void store(A aggregate) {

		this.didBeforeStore = false;
		this.beforeStore(aggregate);
		Assert.isTrue(this.didBeforeStore, this.getClass().getSimpleName() + ": beforeStore was called");

		((AggregateSPI) aggregate).doStore(aggregate.getMeta().getSessionInfo().getUser().getId());

		this.didDoStoreParts = false;
		this.doStoreParts(aggregate);
		Assert.isTrue(this.didDoStoreParts, this.getClass().getSimpleName() + ": doStoreParts was called");

		this.didAfterStore = false;
		this.afterStore(aggregate);
		Assert.isTrue(this.didAfterStore, this.getClass().getSimpleName() + ": afterStore was called");

	}

	@Override
	public void beforeStore(A aggregate) {
		((AggregateSPI) aggregate).beforeStore();
		this.didBeforeStore = true;
	}

	@Override
	public void doStoreParts(A aggregate) {
		this.didDoStoreParts = true;
	}

	@Override
	public void afterStore(A aggregate) {
		ApplicationEvent aggregateStoredEvent = new AggregateStoredEvent(aggregate, aggregate);
		this.getAppContext().publishApplicationEvent(aggregateStoredEvent);
		this.didAfterStore = true;
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
