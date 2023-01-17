
package io.zeitwert.ddd.doc.model;

import java.util.List;

import io.zeitwert.ddd.aggregate.model.AggregateMeta;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStage;

public interface DocMeta extends AggregateMeta {

	List<DocPartTransition> getTransitionList();

	List<CodeCaseStage> getCaseStages();

	// availableActions: string[];

}
