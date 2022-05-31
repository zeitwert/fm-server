package io.zeitwert.fm.dms.model.enums;

import io.zeitwert.ddd.enums.model.Enumeration;
import io.zeitwert.ddd.enums.model.base.EnumeratedBase;

import java.util.List;

public final class CodeContentKind extends EnumeratedBase {

	public CodeContentKind(Enumeration<CodeContentKind> enumeration, String id, String name) {
		super(enumeration, id, name);
	}

	public List<CodeContentType> getContentTypeList() {
		return CodeContentTypeEnum.getContentTypeList(this);
	}

	public List<String> getExtensionList() {
		return this.getContentTypeList().stream().map(ct -> "." + ct.getExtension()).toList();
	}

}
