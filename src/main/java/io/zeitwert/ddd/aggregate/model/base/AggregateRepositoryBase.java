
package io.zeitwert.ddd.aggregate.model.base;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SortField;
import org.jooq.Table;
import org.jooq.UpdatableRecord;
import org.jooq.impl.DSL;
import org.springframework.context.ApplicationEvent;

import static io.zeitwert.ddd.util.Check.assertThis;
import static io.zeitwert.ddd.util.Check.requireThis;

import io.crnk.core.queryspec.FilterOperator;
import io.crnk.core.queryspec.FilterSpec;
import io.crnk.core.queryspec.PathSpec;
import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateType;
import io.zeitwert.ddd.app.event.AggregateStoredEvent;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.oe.model.ObjTenantRepository;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.ddd.property.model.base.PropertyFilter;
import io.zeitwert.ddd.property.model.base.PropertyHandler;
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.ddd.util.SqlUtils;
import javassist.util.proxy.ProxyFactory;

public abstract class AggregateRepositoryBase<A extends Aggregate, V extends Record>
		implements AggregateRepository<A, V>, AggregateRepositorySPI<A, V> {

	private final String aggregateTypeId;
	private final AppContext appContext;
	private final ProxyFactory proxyFactory;
	private final Class<?>[] proxyFactoryParamTypeList;

	private final DSLContext dslContext;
	private final List<PartRepository<? super A, ?>> partRepositories = new ArrayList<>();

	private boolean didAfterCreate = false;
	private boolean didAfterLoad = false;
	private boolean didBeforeStore = false;
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
		this.proxyFactoryParamTypeList = new Class<?>[] { repoIntfClass, UpdatableRecord.class, UpdatableRecord.class };
	}
	//@formatter:on

	protected AppContext getAppContext() {
		return this.appContext;
	}

	protected final DSLContext getDSLContext() {
		return this.dslContext;
	}

	@Override
	public final CodeAggregateType getAggregateType() {
		return this.getAppContext().getAggregateType(this.aggregateTypeId);
	}

	protected void addPartRepository(PartRepository<? super A, ?> partRepository) {
		this.partRepositories.add(partRepository);
	}

	protected boolean hasAccountId() {
		return false;
	}

	/**
	 * Create a new aggregate, used from both create and load to create a new object
	 */
	@SuppressWarnings("unchecked")
	protected final A newAggregate(UpdatableRecord<?> baseRecord, UpdatableRecord<?> extnRecord) {
		A aggregate = null;
		try {
			aggregate = (A) this.proxyFactory.create(this.proxyFactoryParamTypeList,
					new Object[] { this, baseRecord, extnRecord }, PropertyHandler.INSTANCE);
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
	public final A create(Integer tenantId) {

		Integer aggregateId = this.nextAggregateId();
		A aggregate = this.doCreate();

		Integer doInitSeqNr = ((AggregateBase) aggregate).doInitSeqNr;
		((AggregateSPI) aggregate).doInit(aggregateId, tenantId);
		assertThis(((AggregateBase) aggregate).doInitSeqNr > doInitSeqNr,
				aggregate.getClass().getSimpleName() + ": doInit was propagated");

		this.doInitParts(aggregate);

		aggregate.calcAll();

		this.didAfterCreate = false;
		this.doAfterCreate(aggregate);
		assertThis(this.didAfterCreate, this.getClass().getSimpleName() + ": doAfterCreate was propagated");

		return aggregate;
	}

	@Override
	public abstract A doCreate();

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
				aggregate.getClass().getSimpleName() + ": doAfterCreate was propagated");
	}

	@Override
	public final A get(Integer id) {

		requireThis(id != null, "id not null");
		A aggregate = this.doLoad(id);

		this.doInitParts(aggregate);
		this.doLoadParts(aggregate);

		Integer doAssignPartsSeqNr = ((AggregateBase) aggregate).doAssignPartsSeqNr;
		((AggregateSPI) aggregate).doAssignParts();
		assertThis(((AggregateBase) aggregate).doAssignPartsSeqNr > doAssignPartsSeqNr,
				aggregate.getClass().getSimpleName() + ": doAssignParts was propagated");

		aggregate.calcVolatile();

		this.didAfterLoad = false;
		this.doAfterLoad(aggregate);
		assertThis(this.didAfterLoad, this.getClass().getSimpleName() + ": doAfterLoad was propagated");

		return aggregate;
	}

	@Override
	public abstract A doLoad(Integer id);

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
				aggregate.getClass().getSimpleName() + ": doAfterLoad was propagated");
	}

	@Override
	public final void discard(A aggregate) {
		((AggregateBase) aggregate).setStale();
	}

	@Override
	public final void store(A aggregate) {

		this.didBeforeStore = false;
		this.doBeforeStore(aggregate);
		assertThis(this.didBeforeStore, this.getClass().getSimpleName() + ": doBeforeStore was propagated");

		Integer doStoreSeqNr = ((AggregateBase) aggregate).doStoreSeqNr;
		((AggregateSPI) aggregate).doStore();
		assertThis(((AggregateBase) aggregate).doStoreSeqNr > doStoreSeqNr,
				aggregate.getClass().getSimpleName() + ": doStore was propagated");

		this.doStoreParts(aggregate);

		this.didAfterStore = false;
		this.doAfterStore(aggregate);
		assertThis(this.didAfterStore, this.getClass().getSimpleName() + ": doAfterStore was propagated");

	}

	@Override
	public void doBeforeStore(A aggregate) {
		this.didBeforeStore = true;
		Integer doBeforeStoreSeqNr = ((AggregateBase) aggregate).doBeforeStoreSeqNr;
		((AggregateSPI) aggregate).doBeforeStore();
		assertThis(((AggregateBase) aggregate).doBeforeStoreSeqNr > doBeforeStoreSeqNr,
				aggregate.getClass().getSimpleName() + ": doBeforeStore was propagated");
	}

	@Override
	public final void doStoreParts(A aggregate) {
		for (PartRepository<? super A, ?> partRepo : this.partRepositories) {
			partRepo.store(aggregate);
		}
	}

	@Override
	public void doAfterStore(A aggregate) {
		this.didAfterStore = true;
		Integer doAfterStoreSeqNr = ((AggregateBase) aggregate).doAfterStoreSeqNr;
		((AggregateSPI) aggregate).doAfterStore();
		assertThis(((AggregateBase) aggregate).doAfterStoreSeqNr > doAfterStoreSeqNr,
				aggregate.getClass().getSimpleName() + ": doAfterStore was propagated");
		this.discard(aggregate);
		ApplicationEvent aggregateStoredEvent = new AggregateStoredEvent(aggregate, aggregate);
		this.getAppContext().publishApplicationEvent(aggregateStoredEvent);
	}

	@Override
	public final List<V> find(QuerySpec querySpec) {
		String tenantField = AggregateFields.TENANT_ID.getName();
		RequestContext requestCtx = this.appContext.getRequestContext();
		Integer tenantId = requestCtx.getTenantId();
		if (tenantId != ObjTenantRepository.KERNEL_TENANT_ID) { // in kernel tenant everything is visible
			querySpec.addFilter(PathSpec.of(tenantField).filter(FilterOperator.EQ, tenantId));
		}
		if (this.hasAccountId() && requestCtx.hasAccount()) {
			String accountField = AggregateFields.ACCOUNT_ID.getName();
			Integer accountId = requestCtx.getAccountId();
			querySpec.addFilter(PathSpec.of(accountField).filter(FilterOperator.EQ, accountId));
		}
		return this.doFind(querySpec);
	}

	@Override
	public abstract List<V> doFind(QuerySpec querySpec);

	@Override
	public final List<V> getByForeignKey(String fkName, Integer targetId) {
		QuerySpec querySpec = new QuerySpec(Aggregate.class);
		querySpec.addFilter(PathSpec.of(fkName).filter(FilterOperator.EQ, targetId));
		return this.doFind(querySpec);
	}

	@SuppressWarnings("unchecked")
	protected List<V> doFind(Table<V> table, Field<Integer> idField, QuerySpec querySpec) {

		Condition whereClause = DSL.noCondition();

		if (querySpec != null) {
			for (FilterSpec filter : querySpec.getFilters()) {
				if (filter.getOperator().equals(FilterOperator.OR) && filter.getExpression() != null) {
					whereClause = SqlUtils.orFilter(this.dslContext, whereClause, table, idField, filter);
				} else {
					whereClause = SqlUtils.andFilter(this.dslContext, whereClause, table, idField, filter);
				}
			}
		}

		// Sort.
		List<SortField<?>> sortFields = List.of();
		if (querySpec != null && querySpec.getSort().size() > 0) {
			sortFields = SqlUtils.sortFilter(table, querySpec.getSort());
		} else if (table.field("modified_at") != null) {
			sortFields = List.of(table.field("modified_at").desc());
		} else {
			sortFields = List.of(table.field("id").desc());
		}

		Number offset = querySpec == null ? null : querySpec.getOffset();
		Number limit = querySpec == null ? null : querySpec.getLimit();

		return (Result<V>) this.dslContext
				.select()
				.from(table)
				.where(whereClause)
				.orderBy(sortFields)
				.limit(offset, limit)
				.fetch();

	}

}
