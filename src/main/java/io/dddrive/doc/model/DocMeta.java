
package io.dddrive.doc.model;

import java.util.List;

import io.dddrive.ddd.model.AggregateMeta;
import io.dddrive.doc.model.enums.CodeCaseDef;
import io.dddrive.doc.model.enums.CodeCaseStage;

public interface DocMeta extends AggregateMeta {

	List<DocPartTransition> getTransitionList();

	CodeCaseDef getCaseDef();

	CodeCaseStage getCaseStage();

	boolean isInWork();

	List<CodeCaseStage> getCaseStages();

	// availableActions: string[];

}
