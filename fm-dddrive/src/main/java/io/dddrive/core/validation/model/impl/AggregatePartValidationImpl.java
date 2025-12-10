package io.dddrive.core.validation.model.impl;

import org.springframework.lang.Nullable;

import io.dddrive.core.validation.model.AggregatePartValidation;
import io.dddrive.core.validation.model.enums.CodeValidationLevel;

public class AggregatePartValidationImpl implements AggregatePartValidation {

	private final Integer seqNr;
	private final CodeValidationLevel validationLevel;
	private final String message;
	private final String path;

	public AggregatePartValidationImpl(Integer seqNr, CodeValidationLevel validationLevel, String message) {
		this(seqNr, validationLevel, message, null);
	}

	public AggregatePartValidationImpl(Integer seqNr, CodeValidationLevel validationLevel, String message, @Nullable String path) {
		this.seqNr = seqNr;
		this.validationLevel = validationLevel;
		this.message = message;
		this.path = path;
	}

	@Override
	public Integer getSeqNr() {
		return this.seqNr;
	}

	@Override
	public CodeValidationLevel getValidationLevel() {
		return this.validationLevel;
	}


	@Override
	public String getMessage() {
		return this.message;
	}

	@Override
	@Nullable
	public String getPath() {
		return this.path;
	}

}
