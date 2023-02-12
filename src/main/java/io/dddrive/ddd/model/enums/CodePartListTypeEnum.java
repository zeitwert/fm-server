
package io.dddrive.ddd.model.enums;

import org.springframework.stereotype.Component;

import io.dddrive.app.service.api.impl.Enumerations;
import io.dddrive.enums.model.base.EnumerationBase;

@Component("codePartListTypeEnum")
public class CodePartListTypeEnum extends EnumerationBase<CodePartListType> {

	private static CodePartListTypeEnum INSTANCE;

	protected CodePartListTypeEnum(Enumerations enums) {
		super(CodePartListType.class, enums);
		INSTANCE = this;
	}

	public static CodePartListTypeEnum getInstance() {
		return INSTANCE;
	}

	public void addItem(CodePartListType item) {
		super.addItem(item);
	}

	public static CodePartListType getPartListType(String partListTypeId) {
		return INSTANCE.getItem(partListTypeId);
	}

}
