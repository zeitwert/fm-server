
package io.zeitwert.ddd.validation.model.enums;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;

@Component("codeValidationLevelEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeValidationLevelEnum extends EnumerationBase<CodeValidationLevel> {

	public static CodeValidationLevel INFO;
	public static CodeValidationLevel WARNING;
	public static CodeValidationLevel ERROR;

	private static CodeValidationLevelEnum INSTANCE;

	protected CodeValidationLevelEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext, CodeValidationLevel.class);
		INSTANCE = this;
		INFO = CodeValidationLevel.builder().enumeration(this).id("info").name("Info").build();
		WARNING = CodeValidationLevel.builder().enumeration(this).id("warning").name("Warning").build();
		ERROR = CodeValidationLevel.builder().enumeration(this).id("error").name("Error").build();
		this.addItem(INFO);
		this.addItem(WARNING);
		this.addItem(ERROR);
	}

	public static CodeValidationLevel getValidationLevel(String validationLevelId) {
		return INSTANCE.getItem(validationLevelId);
	}

}
