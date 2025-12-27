package dddrive.app.validation.model.impl

import dddrive.app.validation.model.AggregatePartValidation
import dddrive.app.validation.model.enums.CodeValidationLevel

class AggregatePartValidationImpl(
	override val seqNr: Int,
	override val validationLevel: CodeValidationLevel,
	override val message: String,
	override val path: String? = null,
) : AggregatePartValidation
