package io.dddrive.core.validation.model;

import org.springframework.lang.Nullable;

import io.dddrive.core.validation.model.enums.CodeValidationLevel;

public interface AggregatePartValidation {

	Integer getSeqNr();

	CodeValidationLevel getValidationLevel();


	String getMessage();

	@Nullable
	String getPath();

}
