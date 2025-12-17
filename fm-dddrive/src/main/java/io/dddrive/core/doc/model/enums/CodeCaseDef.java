package io.dddrive.core.doc.model.enums;

import io.dddrive.core.ddd.model.enums.CodeAggregateType;
import io.dddrive.core.ddd.model.enums.CodeAggregateTypeEnum;
import io.dddrive.core.enums.model.Enumerated;

import java.util.ArrayList;
import java.util.List;

public class CodeCaseDef implements Enumerated {

	public final List<CodeCaseStage> caseStages = new ArrayList<>();
	private final String id;
	private final String name;
	private final CodeAggregateType docType;

	public CodeCaseDef(String id, String name, String docTypeId) {
		this.id = id;
		this.name = name;
		docType = CodeAggregateTypeEnum.getAggregateType(docTypeId);
	}

	@Override
	public CodeCaseDefEnum getEnumeration() {
		return CodeCaseDefEnum.getInstance();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
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
