
package io.zeitwert.ddd.doc.model.enums;

import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;

@Component("codeCaseDefEnum")
public class CodeCaseDefEnum extends EnumerationBase<CodeCaseDef> {

	static private CodeCaseDefEnum INSTANCE;

	protected CodeCaseDefEnum(Enumerations enums) {
		super(CodeCaseDef.class, enums);
		INSTANCE = this;
	}

	public static CodeCaseDefEnum getInstance() {
		return INSTANCE;
	}

	public void addItem(CodeCaseDef item) {
		super.addItem(item);
	}

	public static CodeCaseDef getCaseDef(String caseDefId) {
		return INSTANCE.getItem(caseDefId);
	}

}
