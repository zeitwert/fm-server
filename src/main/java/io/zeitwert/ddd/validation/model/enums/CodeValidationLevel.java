package io.zeitwert.ddd.validation.model.enums;

import io.zeitwert.ddd.enums.model.base.EnumeratedBase;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class CodeValidationLevel extends EnumeratedBase {

	@Override
	public boolean equals(Object other) {
		return super.equals(other);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
