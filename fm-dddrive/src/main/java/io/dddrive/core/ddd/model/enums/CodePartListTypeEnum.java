package io.dddrive.core.ddd.model.enums;

import io.dddrive.core.enums.model.base.EnumerationBase;

public class CodePartListTypeEnum extends EnumerationBase<CodePartListType> {

	private static CodePartListTypeEnum INSTANCE;

	public CodePartListTypeEnum() {
		super(CodePartListType.class);
		INSTANCE = this;
	}

	public static CodePartListTypeEnum getInstance() {
		return INSTANCE;
	}

	@Override
	public void addItem(CodePartListType item) {
		super.addItem(item);
	}

	public static CodePartListType getPartListType(String partListTypeId) {
		return INSTANCE.getItem(partListTypeId);
	}

}
