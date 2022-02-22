package fm.comunas.ddd.validation.model.impl;

import fm.comunas.ddd.validation.model.AggregatePartValidation;
import fm.comunas.ddd.validation.model.enums.CodeValidationLevel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AggregatePartValidationImpl implements AggregatePartValidation {

	private Integer seqNr;
	private CodeValidationLevel validationLevel;
	private String validation;

}
