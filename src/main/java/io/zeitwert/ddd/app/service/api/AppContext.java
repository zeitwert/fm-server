
package io.zeitwert.ddd.app.service.api;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Record9;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregatePersistenceProvider;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateType;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateTypeEnum;
import io.zeitwert.ddd.aggregate.service.api.AggregateCache;
import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.ddd.app.service.api.impl.Repositories;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStage;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStageEnum;
import io.zeitwert.ddd.enums.model.Enumerated;
import io.zeitwert.ddd.enums.model.Enumeration;
import io.zeitwert.ddd.oe.model.enums.CodeCountry;
import io.zeitwert.ddd.oe.model.enums.CodeCountryEnum;
import io.zeitwert.ddd.oe.model.enums.CodeLocale;
import io.zeitwert.ddd.oe.model.enums.CodeLocaleEnum;
import io.zeitwert.ddd.oe.model.enums.CodeTenantType;
import io.zeitwert.ddd.oe.model.enums.CodeTenantTypeEnum;
import io.zeitwert.ddd.oe.model.enums.CodeUserRole;
import io.zeitwert.ddd.oe.model.enums.CodeUserRoleEnum;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.PartPersistenceProvider;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.part.model.enums.CodePartListTypeEnum;
import io.zeitwert.ddd.property.model.PropertyProvider;
import io.zeitwert.ddd.session.model.RequestContext;

@Service("appContext")
@DependsOn({ "flyway", "flywayInitializer", "codeAggregateTypeEnum", "codePartListTypeEnum", "codeTenantTypeEnum",
		"codeUserRoleEnum", "codeCountryEnum", "codeLocaleEnum", "codeCaseStageEnum" })
public final class AppContext {

	public static final String SCHEMA_NAME = "public";
	private static Schema SCHEMA;

	private static final Field<String> ID = DSL.field("id", String.class);
	private static final Field<String> NAME = DSL.field("name", String.class);

	private static AppContext INSTANCE;

	private final ApplicationContext applicationContext;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final DSLContext dslContext;
	private final Repositories repos;
	private final Map<Class<?>, PropertyProvider> propertyProviders = new HashMap<>();
	private final Map<Class<?>, AggregatePersistenceProvider<?>> aggregatePersistenceProviders = new HashMap<>();
	private final Map<Class<?>, PartPersistenceProvider<?, ? extends Part<?>>> partPersistenceProviders = new HashMap<>();
	private Map<Class<? extends Aggregate>, AggregateCache<?>> cacheByIntf = new HashMap<>();
	private final Enumerations enums;
	private final RequestContext requestContext;

	protected AppContext(ApplicationContext applicationContext, ApplicationEventPublisher applicationEventPublisher,
			DSLContext dslContext, Enumerations enums, RequestContext requestContext) {
		this.applicationContext = applicationContext;
		this.applicationEventPublisher = applicationEventPublisher;
		this.dslContext = dslContext;
		this.repos = new Repositories();
		this.enums = enums;
		this.requestContext = requestContext;
		AppContext.INSTANCE = this;
	}

	@PostConstruct
	private void initKernelCodeTables() {
		this.initAggregateType();
		this.initPartListType();
		this.initTenantType();
		this.initUserRole();
		this.initCountry();
		this.initLocale();
		this.initCaseStage();
	}

	private void initAggregateType() {
		Table<?> codeAggregateType = this.getTable(CodeAggregateTypeEnum.TABLE_NAME);
		for (final Record2<String, String> item : this.getDslContext().select(ID,
				NAME).from(codeAggregateType).fetch()) {
			CodeAggregateType aggregateType = CodeAggregateType.builder()
					.enumeration(CodeAggregateTypeEnum.getInstance())
					.id(item.value1())
					.name(item.value2())
					.build();
			CodeAggregateTypeEnum.getInstance().addItem(aggregateType);
		}
	}

	private void initPartListType() {
		Table<?> codePartListType = this.getTable(CodePartListTypeEnum.TABLE_NAME);
		for (final Record2<String, String> item : this.getDslContext().select(ID,
				NAME).from(codePartListType).fetch()) {
			CodePartListType partListType = CodePartListType.builder()
					.enumeration(CodePartListTypeEnum.getInstance())
					.id(item.value1())
					.name(item.value2())
					.build();
			CodePartListTypeEnum.getInstance().addItem(partListType);
		}
	}

	private void initTenantType() {
		Table<?> codeTenantType = this.getTable(CodeTenantTypeEnum.TABLE_NAME);
		for (final Record2<String, String> item : this.getDslContext().select(ID,
				NAME).from(codeTenantType).fetch()) {
			CodeTenantType tenantType = CodeTenantType.builder()
					.enumeration(CodeTenantTypeEnum.getInstance())
					.id(item.value1())
					.name(item.value2())
					.build();
			CodeTenantTypeEnum.getInstance().addItem(tenantType);
		}
		CodeTenantTypeEnum.getInstance().init();
	}

	private void initUserRole() {
		Table<?> codeUserRole = this.getTable(CodeUserRoleEnum.TABLE_NAME);
		for (final Record2<String, String> item : this.getDslContext().select(ID,
				NAME).from(codeUserRole).fetch()) {
			CodeUserRole userRole = CodeUserRole.builder()
					.enumeration(CodeUserRoleEnum.getInstance())
					.id(item.value1())
					.name(item.value2())
					.build();
			CodeUserRoleEnum.getInstance().addItem(userRole);
		}
		CodeUserRoleEnum.getInstance().init();
	}

	private void initCountry() {
		Table<?> codeCountry = this.getTable(CodeCountryEnum.TABLE_NAME);
		for (final Record2<String, String> item : this.getDslContext().select(ID,
				NAME).from(codeCountry).fetch()) {
			CodeCountry country = CodeCountry.builder()
					.enumeration(CodeCountryEnum.getInstance())
					.id(item.value1())
					.name(item.value2())
					.build();
			CodeCountryEnum.getInstance().addItem(country);
		}
	}

	private void initLocale() {
		Table<?> codeLocale = this.getTable(CodeLocaleEnum.TABLE_NAME);
		for (final Record2<String, String> item : this.getDslContext().select(ID,
				NAME).from(codeLocale).fetch()) {
			CodeLocale locale = CodeLocale.builder()
					.enumeration(CodeLocaleEnum.getInstance())
					.id(item.value1())
					.name(item.value2())
					.build();
			CodeLocaleEnum.getInstance().addItem(locale);
		}
	}

	private void initCaseStage() {
		Table<?> codeCaseStage = this.getTable(CodeCaseStageEnum.TABLE_NAME);
		for (final Record9<String, String, String, String, String, Integer, String, String, String> item : this
				.getDslContext().select(
						ID,
						NAME,
						CodeCaseStageEnum.CASE_DEF_ID,
						CodeCaseStageEnum.CASE_STAGE_TYPE_ID,
						CodeCaseStageEnum.DESCRIPTION,
						CodeCaseStageEnum.SEQ_NR,
						CodeCaseStageEnum.ABSTRACT_CASE_STAGE_ID,
						CodeCaseStageEnum.ACTION,
						CodeCaseStageEnum.AVAILABLE_ACTIONS)
				.from(codeCaseStage).fetch()) {
			CodeCaseStageEnum.getInstance().addItem(
					new CodeCaseStage(
							CodeCaseStageEnum.getInstance(),
							item.value1(),
							item.value3(),
							item.value4(),
							item.value2(),
							item.value5(),
							item.value6(),
							item.value7(),
							item.value8(),
							item.value9()));
		}
	}

	@EventListener
	public void init(ContextRefreshedEvent event) {
		this.initPropertyProviders();
		this.initPersistenceProviders();
	}

	private void initPropertyProviders() {
		this.propertyProviders.clear();
		this.applicationContext
				.getBeansOfType(PropertyProvider.class, false, true)
				.values()
				.forEach(pp -> this.propertyProviders.put(pp.getEntityClass(), pp));
	}

	@SuppressWarnings("unchecked")
	private void initPersistenceProviders() {
		this.aggregatePersistenceProviders.clear();
		this.applicationContext
				.getBeansOfType(AggregatePersistenceProvider.class, false, true)
				.values()
				.forEach(pp -> this.aggregatePersistenceProviders.put(pp.getEntityClass(), pp));
		this.partPersistenceProviders.clear();
		this.applicationContext
				.getBeansOfType(PartPersistenceProvider.class, false, true)
				.values()
				.forEach(pp -> this.partPersistenceProviders.put(pp.getEntityClass(), pp));
	}

	public static AppContext getInstance() {
		return INSTANCE;
	}

	public RequestContext getRequestContext() { // TODO: remove this method
		return this.requestContext;
	}

	public DSLContext getDslContext() { // TODO: remove this method
		return this.dslContext;
	}

	public void addRepository(String aggregateTypeId, final Class<? extends Aggregate> intfClass,
			final AggregateRepository<? extends Aggregate, ? extends Object> repo) {
		this.repos.addRepository(aggregateTypeId, intfClass, repo);
	}

	public <A extends Aggregate> AggregateRepository<A, ?> getRepository(Class<A> intfClass) {
		return this.repos.getRepository(intfClass);
	}

	public PropertyProvider getPropertyProvider(Class<?> intfClass) {
		return this.propertyProviders.get(intfClass);
	}

	@SuppressWarnings("unchecked")
	public <A extends Aggregate> AggregatePersistenceProvider<A> getAggregatePersistenceProvider(Class<A> intfClass) {
		return (AggregatePersistenceProvider<A>) this.aggregatePersistenceProviders.get(intfClass);
	}

	@SuppressWarnings("unchecked")
	public <A extends Aggregate, P extends Part<A>> PartPersistenceProvider<A, P> getPartPersistenceProvider(
			Class<? extends Part<A>> intfClass) {
		return (PartPersistenceProvider<A, P>) this.partPersistenceProviders.get(intfClass);
	}

	public void addCache(Class<? extends Aggregate> intfClass, AggregateCache<? extends Aggregate> cache) {
		this.cacheByIntf.put(intfClass, cache);
	}

	@SuppressWarnings("unchecked")
	public <Aggr extends Aggregate> AggregateCache<Aggr> getCache(Class<Aggr> intfClass) {
		return (AggregateCache<Aggr>) this.cacheByIntf.get(intfClass);
	}

	public <A extends Aggregate> void addPartRepository(String partTypeId, final Class<? extends Part<A>> intfClass,
			final PartRepository<A, ? extends Part<A>> repo) {
		this.repos.addPartRepository(partTypeId, intfClass, repo);
	}

	public <A extends Aggregate, P extends Part<A>> PartRepository<A, P> getPartRepository(Class<P> intfClass) {
		return this.repos.getPartRepository(intfClass);
	}

	public <E extends Enumerated> void addEnumeration(Class<E> enumClass, Enumeration<E> enumeration) {
		this.enums.addEnumeration(enumClass, enumeration);
	}

	public <E extends Enumerated> Enumeration<E> getEnumeration(Class<E> enumClass) {
		return this.enums.getEnumeration(enumClass);
	}

	public <E extends Enumerated> E getEnumerated(Class<E> enumClass, String itemId) {
		return this.enums.getEnumeration(enumClass).getItem(itemId);
	}

	public <T> T getBean(Class<T> requiredType) {
		return this.applicationContext.getBean(requiredType);
	}

	public void publishApplicationEvent(ApplicationEvent applicationEvent) {
		this.applicationEventPublisher.publishEvent(applicationEvent);
	}

	public Table<?> getTable(String tableName) {
		return this.getSchema().getTable(tableName);
	}

	private Schema getSchema() {
		if (SCHEMA == null) {
			SCHEMA = this.dslContext.meta().getSchemas(AppContext.SCHEMA_NAME).get(0);
		}
		return SCHEMA;
	}

}
