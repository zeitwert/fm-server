package dddrive.app.validation.model

import dddrive.app.validation.model.enums.CodeValidationLevel

interface AggregatePartValidation {

	val seqNr: Int

	val validationLevel: CodeValidationLevel

	val message: String

	val path: String?

}
