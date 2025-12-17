package io.dddrive.core.validation.model

import io.dddrive.core.validation.model.enums.CodeValidationLevel

interface AggregatePartValidation {

	val seqNr: Int

	val validationLevel: CodeValidationLevel

	val message: String

	val path: String?

}
