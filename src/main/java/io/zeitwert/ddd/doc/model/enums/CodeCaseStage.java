
package io.zeitwert.ddd.doc.model.enums;

import io.zeitwert.ddd.enums.model.base.EnumeratedBase;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@SuperBuilder
public class CodeCaseStage extends EnumeratedBase {

	private final String caseDefId;
	private final Integer seqNr;
	private final String caseStageTypeId;
	private final String description;
	private final String abstractCaseStageId;
	private final String action;
	private final List<String> availableActions;

	public CodeCaseStage(CodeCaseStageEnum enumeration, String id, String caseDefId, String caseStageTypeId, String name,
			String description, Integer seqNr, String abstractCaseStageId, String action, String availableActions) {
		super(enumeration, id, name);
		this.caseDefId = caseDefId;
		this.seqNr = seqNr;
		this.description = description;
		this.caseStageTypeId = caseStageTypeId;
		this.abstractCaseStageId = abstractCaseStageId;
		this.action = action;
		this.availableActions = availableActions == null ? new ArrayList<>() : Arrays.asList(availableActions.split(","));
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
		return this.getCaseStageTypeId().equals("abstract");
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

	@Override
	public boolean equals(Object other) {
		return super.equals(other);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
