package io.dddrive.core.validation.model.enums;

import io.dddrive.core.enums.model.base.EnumerationBase;

public class CodeValidationLevelEnum extends EnumerationBase<CodeValidationLevel> {

	public static CodeValidationLevel INFO;
	public static CodeValidationLevel WARNING;
	public static CodeValidationLevel ERROR;

	private static CodeValidationLevelEnum INSTANCE;

	public CodeValidationLevelEnum() {
		super(CodeValidationLevel.class);
		INSTANCE = this;
		this.addItem(new CodeValidationLevel(this, "info", "Info"));
		this.addItem(new CodeValidationLevel(this, "warning", "Warning"));
		this.addItem(new CodeValidationLevel(this, "error", "Error"));
	}

	public static CodeValidationLevelEnum getInstance() {
		return INSTANCE;
	}

	public static CodeValidationLevel getValidationLevel(String validationLevelId) {
		return INSTANCE.getItem(validationLevelId);
	}

	@Override
	public void assignItems() {
		INFO = getValidationLevel("info");
		WARNING = getValidationLevel("warning");
		ERROR = getValidationLevel("error");
	}

}
