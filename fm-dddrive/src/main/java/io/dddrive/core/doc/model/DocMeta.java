package io.dddrive.core.doc.model;

import io.dddrive.core.ddd.model.AggregateMeta;
import io.dddrive.core.doc.model.enums.CodeCaseDef;
import io.dddrive.core.doc.model.enums.CodeCaseStage;

import java.util.List;

public interface DocMeta extends AggregateMeta {

	String getDocTypeId();

	List<DocPartTransition> getTransitionList();

	CodeCaseDef getCaseDef();

	CodeCaseStage getCaseStage();

	boolean isInWork();

	List<CodeCaseStage> getCaseStages();

	// availableActions: string[];

}
