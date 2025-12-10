package io.dddrive.core.ddd.model.enums;

import io.dddrive.core.enums.model.base.EnumerationBase;

public class CodeAggregateTypeEnum extends EnumerationBase<CodeAggregateType> {

	private static CodeAggregateTypeEnum INSTANCE;

	public CodeAggregateTypeEnum() {
		super(CodeAggregateType.class);
		INSTANCE = this;
	}

	public static CodeAggregateTypeEnum getInstance() {
		return INSTANCE;
	}

	@Override
	public void addItem(CodeAggregateType item) {
		super.addItem(item);
	}

	public static CodeAggregateType getAggregateType(String aggregateTypeId) {
		return INSTANCE.getItem(aggregateTypeId);
	}

}
