package io.dddrive.core.validation.model.enums;

import io.dddrive.core.enums.model.Enumerated;

public class CodeValidationLevel implements Enumerated {

	private final String id;
	private final String name;

	public CodeValidationLevel(CodeValidationLevelEnum enumeration, String id, String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public CodeValidationLevelEnum getEnumeration() {
		return CodeValidationLevelEnum.getInstance();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

}
