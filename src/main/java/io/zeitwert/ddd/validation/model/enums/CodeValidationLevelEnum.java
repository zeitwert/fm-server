
package io.zeitwert.ddd.validation.model.enums;

import io.zeitwert.ddd.app.service.api.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component("codeValidationLevelEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeValidationLevelEnum extends EnumerationBase<CodeValidationLevel> {

	public static CodeValidationLevel INFO;
	public static CodeValidationLevel WARNING;
	public static CodeValidationLevel ERROR;

	private static CodeValidationLevelEnum INSTANCE;

	@Autowired
	protected CodeValidationLevelEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INFO = new CodeValidationLevel(this, "info", "Info");
		this.addItem(INFO);
		WARNING = new CodeValidationLevel(this, "warning", "Warning");
		this.addItem(WARNING);
		ERROR = new CodeValidationLevel(this, "error", "Error");
		this.addItem(ERROR);
		INSTANCE = this;
	}

	public static CodeValidationLevel getValidationLevel(String validationLevelId) {
		return INSTANCE.getItem(validationLevelId);
	}

}
