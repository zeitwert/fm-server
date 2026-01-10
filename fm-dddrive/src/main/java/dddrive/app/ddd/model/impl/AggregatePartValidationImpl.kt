package dddrive.app.ddd.model.impl

import dddrive.app.ddd.model.AggregatePartValidation
import dddrive.app.ddd.model.enums.CodeValidationLevel

class AggregatePartValidationImpl(
	override val seqNr: Int,
	override val validationLevel: CodeValidationLevel,
	override val message: String,
	override val path: String? = null,
) : AggregatePartValidation
