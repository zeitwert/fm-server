package io.dddrive.core.doc.model.enums;

import io.dddrive.core.enums.model.base.EnumerationBase;

public class CodeCaseStageEnum extends EnumerationBase<CodeCaseStage> {

	static private CodeCaseStageEnum INSTANCE;

	public CodeCaseStageEnum() {
		super(CodeCaseStage.class);
		INSTANCE = this;
	}

	public static CodeCaseStageEnum getInstance() {
		return INSTANCE;
	}

	@Override
	public void addItem(CodeCaseStage item) {
		super.addItem(item);
	}

	public static CodeCaseStage getCaseStage(String caseStageId) {
		return INSTANCE.getItem(caseStageId);
	}

}
