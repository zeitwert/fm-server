package dddrive.ddd.validation.model.impl

import dddrive.ddd.validation.model.AggregatePartValidation
import dddrive.ddd.validation.model.enums.CodeValidationLevel

class AggregatePartValidationImpl(
	override val seqNr: Int,
	override val validationLevel: CodeValidationLevel,
	override val message: String,
	override val path: String? = null,
) : AggregatePartValidation
