
package io.zeitwert.ddd.doc.service.api;

import java.util.List;

import io.zeitwert.ddd.doc.model.enums.CodeCaseStage;

public interface DocService {

	List<CodeCaseStage> getCaseStages(String caseDefId);

}
