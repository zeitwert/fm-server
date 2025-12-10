package io.dddrive.core.doc.model.enums;

import java.util.ArrayList;
import java.util.List;

import io.dddrive.core.enums.model.base.EnumeratedBase;

public class CodeCaseDef extends EnumeratedBase {

	public final List<CodeCaseStage> caseStages = new ArrayList<>();

	public CodeCaseDef(CodeCaseDefEnum enumeration, String id, String name) {
		super(enumeration, id, name);
	}

	public void addCaseStage(CodeCaseStage stage) {
		this.caseStages.add(stage);
	}

	public List<CodeCaseStage> getCaseStages() {
		return List.copyOf(this.caseStages);
	}

}
