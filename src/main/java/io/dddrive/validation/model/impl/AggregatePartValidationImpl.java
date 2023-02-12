package io.dddrive.validation.model.impl;

import io.dddrive.validation.model.AggregatePartValidation;
import io.dddrive.validation.model.enums.CodeValidationLevel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AggregatePartValidationImpl implements AggregatePartValidation {

	private Integer seqNr;
	private CodeValidationLevel validationLevel;
	private String validation;

}
