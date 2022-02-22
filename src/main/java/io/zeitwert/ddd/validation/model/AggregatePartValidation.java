package io.zeitwert.ddd.validation.model;

import io.zeitwert.ddd.validation.model.enums.CodeValidationLevel;

public interface AggregatePartValidation {

	Integer getSeqNr();

	CodeValidationLevel getValidationLevel();

	String getValidation();

}
