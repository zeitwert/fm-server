package io.dddrive.validation.model.impl

import io.dddrive.validation.model.AggregatePartValidation
import io.dddrive.validation.model.enums.CodeValidationLevel

class AggregatePartValidationImpl(
	override val seqNr: Int,
	override val validationLevel: CodeValidationLevel,
	override val message: String,
	override val path: String? = null,
) : AggregatePartValidation
