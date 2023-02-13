
package io.zeitwert.fm.server.config.bootstrap;

import java.util.List;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Record9;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import io.dddrive.ddd.model.enums.CodeAggregateType;
import io.dddrive.ddd.model.enums.CodeAggregateTypeEnum;
import io.dddrive.ddd.model.enums.CodePartListType;
import io.dddrive.ddd.model.enums.CodePartListTypeEnum;
import io.dddrive.doc.model.enums.CodeCaseDef;
import io.dddrive.doc.model.enums.CodeCaseDefEnum;
import io.dddrive.doc.model.enums.CodeCaseStage;
import io.dddrive.doc.model.enums.CodeCaseStageEnum;
import io.dddrive.oe.model.enums.CodeCountry;
import io.dddrive.oe.model.enums.CodeCountryEnum;
import io.dddrive.oe.model.enums.CodeLocale;
import io.dddrive.oe.model.enums.CodeLocaleEnum;
import io.dddrive.oe.model.enums.CodeTenantType;
import io.dddrive.oe.model.enums.CodeTenantTypeEnum;
import io.dddrive.oe.model.enums.CodeUserRole;
import io.dddrive.oe.model.enums.CodeUserRoleEnum;

@Service("kernelBootstrap")
@DependsOn({ "flyway", "flywayInitializer", "codeAggregateTypeEnum", "codePartListTypeEnum", "codeTenantTypeEnum",
		"codeUserRoleEnum", "codeCountryEnum", "codeLocaleEnum", "codeCaseDefEnum", "codeCaseStageEnum" })
public final class KernelBootstrap {

	public static final String SCHEMA_NAME = "public";

	private static final Field<String> ID = DSL.field("id", String.class);
	private static final Field<String> NAME = DSL.field("name", String.class);

	static public final Field<String> CODE_CASE_STAGE__CASE_DEF_ID = DSL.field("case_def_id", String.class);
	static public final Field<String> CODE_CASE_STAGE__CASE_STAGE_TYPE_ID = DSL.field("case_stage_type_id", String.class);
	static public final Field<String> CODE_CASE_STAGE__DESCRIPTION = DSL.field("description", String.class);
	static public final Field<Integer> CODE_CASE_STAGE__SEQ_NR = DSL.field("seq_nr", Integer.class);
	static public final Field<String> CODE_CASE_STAGE__ABSTRACT_CASE_STAGE_ID = DSL.field("abstract_case_stage_id",
			String.class);
	static public final Field<String> CODE_CASE_STAGE__ACTION = DSL.field("action", String.class);
	static public final Field<String> CODE_CASE_STAGE__AVAILABLE_ACTIONS = DSL.field("available_actions", String.class);

	private final DSLContext dslContext;
	private final Schema schema;

	protected KernelBootstrap(DSLContext dslContext) {
		this.dslContext = dslContext;
		this.schema = this.dslContext.meta().getSchemas(SCHEMA_NAME).get(0);
	}

	@PostConstruct
	private void initCodeTables() {
		try {
			this.initAggregateType();
			this.initPartListType();
			this.initTenantType();
			this.initUserRole();
			this.initCountry();
			this.initLocale();
			this.initCaseDef();
			this.initCaseStage();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private void initAggregateType() {
		Table<?> codeAggregateType = this.schema.getTable("code_aggregate_type");
		for (final Record2<String, String> item : this.dslContext.select(ID, NAME).from(codeAggregateType).fetch()) {
			CodeAggregateType aggregateType = CodeAggregateType.builder()
					.enumeration(CodeAggregateTypeEnum.getInstance())
					.id(item.value1())
					.name(item.value2())
					.build();
			CodeAggregateTypeEnum.getInstance().addItem(aggregateType);
		}
	}

	private void initPartListType() {
		Table<?> codePartListType = this.schema.getTable("code_part_list_type");
		for (final Record2<String, String> item : this.dslContext.select(ID,
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
		Table<?> codeTenantType = this.schema.getTable("code_tenant_type");
		for (final Record2<String, String> item : this.dslContext.select(ID,
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
		Table<?> codeUserRole = this.schema.getTable("code_user_role");
		for (final Record2<String, String> item : this.dslContext.select(ID,
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
		Table<?> codeCountry = this.schema.getTable("code_country");
		for (final Record2<String, String> item : this.dslContext.select(ID,
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
		Table<?> codeLocale = this.schema.getTable("code_locale");
		for (final Record2<String, String> item : this.dslContext.select(ID,
				NAME).from(codeLocale).fetch()) {
			CodeLocale locale = CodeLocale.builder()
					.enumeration(CodeLocaleEnum.getInstance())
					.id(item.value1())
					.name(item.value2())
					.build();
			CodeLocaleEnum.getInstance().addItem(locale);
		}
	}

	private void initCaseDef() {
		Table<?> codeDef = this.schema.getTable("code_case_def");
		for (final Record2<String, String> item : this.dslContext.select(ID,
				NAME).from(codeDef).fetch()) {
			CodeCaseDef caseDef = CodeCaseDef.builder()
					.enumeration(CodeCaseDefEnum.getInstance())
					.id(item.value1())
					.name(item.value2())
					.build();
			CodeCaseDefEnum.getInstance().addItem(caseDef);
		}
	}

	private void initCaseStage() {
		Table<?> codeCaseStage = this.schema.getTable("code_case_stage");
		for (Record9<String, String, String, String, String, Integer, String, String, String> item : this.dslContext.select(
				ID,
				NAME,
				CODE_CASE_STAGE__CASE_DEF_ID,
				CODE_CASE_STAGE__CASE_STAGE_TYPE_ID,
				CODE_CASE_STAGE__DESCRIPTION,
				CODE_CASE_STAGE__SEQ_NR,
				CODE_CASE_STAGE__ABSTRACT_CASE_STAGE_ID,
				CODE_CASE_STAGE__ACTION,
				CODE_CASE_STAGE__AVAILABLE_ACTIONS)
				.from(codeCaseStage)
				.orderBy(CODE_CASE_STAGE__CASE_DEF_ID, CODE_CASE_STAGE__SEQ_NR)
				.fetch()) {
			CodeCaseStage caseStage = CodeCaseStage.builder()
					.enumeration(CodeCaseStageEnum.getInstance())
					.id(item.value1())
					.name(item.value2())
					.caseDefId(item.value3())
					.caseStageTypeId(item.value4())
					.description(item.value5())
					.seqNr(item.value6())
					.abstractCaseStageId(item.value7())
					.action(item.value8())
					.availableActions(item.value9() != null ? List.of(item.value9().split(",")) : List.of())
					.build();
			CodeCaseStageEnum.getInstance().addItem(caseStage);
			CodeCaseDefEnum.getCaseDef(item.value3()).addCaseStage(caseStage);
		}
	}

}
