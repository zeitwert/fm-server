
package fm.comunas.ddd.doc.api;

import java.util.List;

import fm.comunas.ddd.doc.model.enums.CodeCaseStage;

public interface DocService {

	List<CodeCaseStage> getCaseStages(String caseDefId);

}
