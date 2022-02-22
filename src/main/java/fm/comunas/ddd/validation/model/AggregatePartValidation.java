package fm.comunas.ddd.validation.model;

import fm.comunas.ddd.validation.model.enums.CodeValidationLevel;

public interface AggregatePartValidation {

	Integer getSeqNr();

	CodeValidationLevel getValidationLevel();

	String getValidation();

}
