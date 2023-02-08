
package io.zeitwert.ddd.part.model.enums;

import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;

@Component("codePartListTypeEnum")
public class CodePartListTypeEnum extends EnumerationBase<CodePartListType> {

	static public final String TABLE_NAME = "code_part_list_type";

	private static CodePartListTypeEnum INSTANCE;

	protected CodePartListTypeEnum(Enumerations enums) {
		super(null, CodePartListType.class);
		enums.addEnumeration(CodePartListType.class, this);
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
