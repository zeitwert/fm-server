package io.dddrive.core.doc.model.enums;

import java.util.ArrayList;
import java.util.List;

import io.dddrive.core.ddd.model.enums.CodeAggregateType;
import io.dddrive.core.ddd.model.enums.CodeAggregateTypeEnum;
import io.dddrive.core.enums.model.base.EnumeratedBase;

public class CodeCaseDef extends EnumeratedBase {

	public final List<CodeCaseStage> caseStages = new ArrayList<>();
	private final CodeAggregateType docType;

	public CodeCaseDef(CodeCaseDefEnum enumeration, String id, String name, String docTypeId) {
		super(enumeration, id, name);
		docType = CodeAggregateTypeEnum.getAggregateType(docTypeId);
	}

	public CodeAggregateType getDocType() {
		return this.docType;
	}

	public void addCaseStage(CodeCaseStage stage) {
		this.caseStages.add(stage);
	}

	public List<CodeCaseStage> getCaseStages() {
		return List.copyOf(this.caseStages);
	}

}
