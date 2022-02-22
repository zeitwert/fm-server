
package fm.comunas.ddd.app.service.api;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.UpdatableRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import io.crnk.core.boot.CrnkBoot;
import io.crnk.core.engine.registry.ResourceRegistry;
import io.crnk.core.queryspec.FilterOperator;
import io.crnk.core.queryspec.mapper.DefaultQuerySpecUrlMapper;
import fm.comunas.ddd.aggregate.model.Aggregate;
import fm.comunas.ddd.aggregate.model.AggregateRepository;
import fm.comunas.ddd.aggregate.model.enums.CodeAggregateType;
import fm.comunas.ddd.aggregate.model.enums.CodeAggregateTypeEnum;
import fm.comunas.ddd.app.service.api.impl.Repositories;
import fm.comunas.ddd.enums.model.Enumerated;
import fm.comunas.ddd.enums.model.Enumeration;
import fm.comunas.ddd.part.model.Part;
import fm.comunas.ddd.part.model.PartRepository;
import fm.comunas.ddd.property.model.enums.CodePartListType;
import fm.comunas.ddd.property.model.enums.CodePartListTypeEnum;
import fm.comunas.ddd.session.model.SessionInfo;

import java.util.Optional;

@Service("appContext")
@DependsOn({ "crnkBoot", "codeAggregateTypeEnum", "codePartListTypeEnum" })
public final class AppContext {

	private static final String SCHEMA_NAME = "public";
	private static Schema SCHEMA;

	private static AppContext INSTANCE;

	private final ApplicationContext applicationContext;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final DSLContext dslContext;
	private final CrnkBoot crnkBoot;
	private final Repositories repos;
	private final Enumerations enums;

	@Autowired
	protected AppContext(final ApplicationContext applicationContext, ApplicationEventPublisher applicationEventPublisher,
			final DSLContext dslContext, Enumerations enums) {
		this.applicationContext = applicationContext;
		this.applicationEventPublisher = applicationEventPublisher;
		this.dslContext = dslContext;
		this.crnkBoot = applicationContext.getBean(CrnkBoot.class);
		this.repos = new Repositories();
		this.enums = enums;
		AppContext.INSTANCE = this;
	}

	public static AppContext getInstance() {
		return INSTANCE;
	}

	// TODO get rid of (at least reduce usage)
	public Schema getSchema() {
		if (SCHEMA == null) {
			SCHEMA = this.dslContext.meta().getSchemas(AppContext.SCHEMA_NAME).get(0);
		}
		return SCHEMA;
	}

	public void addRepository(String aggregateTypeId, final Class<? extends Aggregate> intfClass,
			final AggregateRepository<? extends Aggregate, ? extends Record> repo) {
		this.repos.addRepository(aggregateTypeId, intfClass, repo);
	}

	public <Aggr extends Aggregate> AggregateRepository<Aggr, ?> getRepository(Class<Aggr> intfClass) {
		return this.repos.getRepository(intfClass);
	}

	@SuppressWarnings("unchecked")
	public <Aggr extends Aggregate> Optional<Aggr> getAggregate(SessionInfo sessionInfo, Class<Aggr> intfClass,
			Integer id) {
		AggregateRepository<?, ?> repo = this.repos.getRepository(intfClass);
		return (Optional<Aggr>) (id == null ? Optional.empty() : repo.get(sessionInfo, id));
	}

	public <A extends Aggregate> void addPartRepository(String partTypeId, final Class<? extends Part<A>> intfClass,
			final PartRepository<A, ? extends Part<A>> repo) {
		this.repos.addPartRepository(partTypeId, intfClass, repo);
	}

	public <A extends Aggregate, P extends Part<A>> PartRepository<A, P> getPartRepository(Class<P> intfClass) {
		return this.repos.getPartRepository(intfClass);
	}

	public <EN extends Enumeration<? extends Enumerated>> EN getEnumeration(Class<EN> enumClass) {
		return (EN) this.enums.getEnumeration(enumClass);
	}

	public <E extends Enumerated, EN extends Enumeration<E>> E getEnumerated(Class<EN> enumClass, String itemId) {
		return this.enums.getEnumeration(enumClass).getItem(itemId);
	}

	public CodeAggregateType getAggregateType(String aggregateTypeId) {
		CodeAggregateType aggregateType = this.enums.getEnumeration(CodeAggregateTypeEnum.class).getItem(aggregateTypeId);
		Assert.isTrue(aggregateType != null, "found aggregateType " + aggregateTypeId);
		return aggregateType;
	}

	public CodePartListType getPartListType(String partListTypeId) {
		CodePartListType partListType = this.enums.getEnumeration(CodePartListTypeEnum.class).getItem(partListTypeId);
		Assert.isTrue(partListType != null, "found partListType " + partListTypeId);
		return partListType;
	}

	public <T> T getBean(Class<T> requiredType) {
		return this.applicationContext.getBean(requiredType);
	}

	public <R extends UpdatableRecord<?>> R newRecord(Table<R> recordType) {
		return this.dslContext.newRecord(recordType);
	}

	public ResourceRegistry getResourceRegistry() {
		return this.crnkBoot.getResourceRegistry();
	}

	public void addFilterOperator(FilterOperator filter) {
		DefaultQuerySpecUrlMapper mapper = (DefaultQuerySpecUrlMapper) this.crnkBoot.getUrlMapper();
		mapper.addSupportedOperator(filter);
	}

	public void publishApplicationEvent(ApplicationEvent applicationEvent) {
		applicationEventPublisher.publishEvent(applicationEvent);
	}

}
