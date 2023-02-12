
package io.dddrive.doc.model.enums;

import org.springframework.stereotype.Component;

import io.dddrive.app.service.api.impl.Enumerations;
import io.dddrive.enums.model.base.EnumerationBase;

@Component("codeCaseStageEnum")
public class CodeCaseStageEnum extends EnumerationBase<CodeCaseStage> {

	static private CodeCaseStageEnum INSTANCE;

	protected CodeCaseStageEnum(Enumerations enums) {
		super(CodeCaseStage.class, enums);
		INSTANCE = this;
	}

	public static CodeCaseStageEnum getInstance() {
		return INSTANCE;
	}

	public void addItem(CodeCaseStage item) {
		super.addItem(item);
	}

	public static CodeCaseStage getCaseStage(String caseStageId) {
		return INSTANCE.getItem(caseStageId);
	}

}
