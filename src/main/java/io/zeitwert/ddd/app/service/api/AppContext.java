
package io.zeitwert.ddd.app.service.api;

import static io.zeitwert.ddd.util.Check.assertThis;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.UpdatableRecord;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateType;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateTypeEnum;
import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.ddd.app.service.api.impl.Repositories;
import io.zeitwert.ddd.enums.model.Enumerated;
import io.zeitwert.ddd.enums.model.Enumeration;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.ddd.property.model.enums.CodePartListTypeEnum;

@Service("appContext")
@DependsOn({ "codeAggregateTypeEnum", "codePartListTypeEnum" })
public final class AppContext {

	private static final String SCHEMA_NAME = "public";
	private static Schema SCHEMA;

	private static AppContext INSTANCE;

	private final ApplicationContext applicationContext;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final DSLContext dslContext;
	private final Repositories repos;
	private final Enumerations enums;

	protected AppContext(final ApplicationContext applicationContext, ApplicationEventPublisher applicationEventPublisher,
			final DSLContext dslContext, Enumerations enums) {
		this.applicationContext = applicationContext;
		this.applicationEventPublisher = applicationEventPublisher;
		this.dslContext = dslContext;
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
		assertThis(aggregateType != null, "found aggregateType " + aggregateTypeId);
		return aggregateType;
	}

	public CodePartListType getPartListType(String partListTypeId) {
		CodePartListType partListType = this.enums.getEnumeration(CodePartListTypeEnum.class).getItem(partListTypeId);
		assertThis(partListType != null, "found partListType " + partListTypeId);
		return partListType;
	}

	public <T> T getBean(Class<T> requiredType) {
		return this.applicationContext.getBean(requiredType);
	}

	public <R extends UpdatableRecord<?>> R newRecord(Table<R> recordType) {
		return this.dslContext.newRecord(recordType);
	}

	public void publishApplicationEvent(ApplicationEvent applicationEvent) {
		applicationEventPublisher.publishEvent(applicationEvent);
	}

}
