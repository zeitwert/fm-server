package io.dddrive.core.doc.model.enums;

import io.dddrive.core.enums.model.Enumerated;

import java.util.List;

public class CodeCaseStage implements Enumerated {

	private final String id;
	private final String name;
	private final String caseDefId;
	private final Integer seqNr;
	private final String caseStageTypeId;
	private final String description;
	private final String abstractCaseStageId;
	private final String action;
	private final List<String> availableActions;

	public CodeCaseStage(
			String id,
			String caseDefId,
			String caseStageTypeId,
			String name,
			String description,
			Integer seqNr,
			String abstractCaseStageId,
			String action,
			List<String> availableActions) {
		this.id = id;
		this.name = name;
		this.caseDefId = caseDefId;
		this.seqNr = seqNr;
		this.description = description;
		this.caseStageTypeId = caseStageTypeId;
		this.abstractCaseStageId = abstractCaseStageId;
		this.action = action;
		this.availableActions = availableActions;
	}

	@Override
	public CodeCaseStageEnum getEnumeration() {
		return CodeCaseStageEnum.getInstance();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	public CodeCaseDef getCaseDef() {
		return CodeCaseDefEnum.getCaseDef(this.caseDefId);
	}

	public boolean isInWork() {
		return !"terminal".equals(this.caseStageTypeId);
	}

	public Integer getSeqNr() {
		return this.seqNr;
	}

	public String getDescription() {
		return this.description;
	}

	public String getCaseStageTypeId() {
		return this.caseStageTypeId;
	}

	public Boolean getIsAbstract() {
		return "abstract".equals(this.getCaseStageTypeId());
	}

	public CodeCaseStage getAbstractCaseStage() {
		return CodeCaseStageEnum.getCaseStage(this.abstractCaseStageId);
	}

	public String getAction() {
		return this.action;
	}

	public List<String> getAvailableActions() {
		return this.availableActions;
	}

}
