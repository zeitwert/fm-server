
package io.zeitwert.ddd.aggregate.model.enums;

import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;

@Component("codeAggregateTypeEnum")
public class CodeAggregateTypeEnum extends EnumerationBase<CodeAggregateType> {

	private static CodeAggregateTypeEnum INSTANCE;

	protected CodeAggregateTypeEnum(Enumerations enums) {
		super(CodeAggregateType.class, enums);
		INSTANCE = this;
	}

	public static CodeAggregateTypeEnum getInstance() {
		return INSTANCE;
	}

	public void addItem(CodeAggregateType item) {
		super.addItem(item);
	}

	public static CodeAggregateType getAggregateType(String aggregateTypeId) {
		return INSTANCE.getItem(aggregateTypeId);
	}

}
