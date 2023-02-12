package io.dddrive.validation.model;

import io.dddrive.validation.model.enums.CodeValidationLevel;

public interface AggregatePartValidation {

	Integer getSeqNr();

	CodeValidationLevel getValidationLevel();

	String getValidation();

}
