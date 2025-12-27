package dddrive.ddd.validation.model

import dddrive.ddd.validation.model.enums.CodeValidationLevel

interface AggregatePartValidation {

	val seqNr: Int

	val validationLevel: CodeValidationLevel

	val message: String

	val path: String?

}
