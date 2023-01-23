package io.zeitwert.ddd.aggregate.model.enums;

import io.zeitwert.ddd.enums.model.base.EnumeratedBase;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public final class CodeAggregateType extends EnumeratedBase {

	@Override
	public boolean equals(Object other) {
		return super.equals(other);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
