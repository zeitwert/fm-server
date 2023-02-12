
package io.dddrive.doc.model.enums;

import org.springframework.stereotype.Component;

import io.dddrive.app.service.api.impl.Enumerations;
import io.dddrive.enums.model.base.EnumerationBase;

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
