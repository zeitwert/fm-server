package io.dddrive.core.doc.model.enums;

import io.dddrive.core.enums.model.base.EnumerationBase;

public class CodeCaseDefEnum extends EnumerationBase<CodeCaseDef> {

	static private CodeCaseDefEnum INSTANCE;

	public CodeCaseDefEnum() {
		super(CodeCaseDef.class);
		INSTANCE = this;
	}

	public static CodeCaseDefEnum getInstance() {
		return INSTANCE;
	}

	@Override
	public void addItem(CodeCaseDef item) {
		super.addItem(item);
	}

	public static CodeCaseDef getCaseDef(String caseDefId) {
		return INSTANCE.getItem(caseDefId);
	}

}
