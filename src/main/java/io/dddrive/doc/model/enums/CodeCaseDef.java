
package io.dddrive.doc.model.enums;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.dddrive.enums.model.base.EnumeratedBase;

@Data
@SuperBuilder
public class CodeCaseDef extends EnumeratedBase {

	public final List<CodeCaseStage> caseStages = new ArrayList<>();

	public CodeCaseDef(CodeCaseDefEnum enumeration, String id, String name) {
		super(enumeration, id, name);
	}

	public void addCaseStage(CodeCaseStage stage) {
		this.caseStages.add(stage);
	}

	@JsonIgnore
	public List<CodeCaseStage> getCaseStages() {
		return List.copyOf(this.caseStages);
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
