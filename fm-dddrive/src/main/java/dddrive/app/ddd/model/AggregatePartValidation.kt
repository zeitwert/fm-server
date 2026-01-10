package dddrive.app.ddd.model

import dddrive.app.ddd.model.enums.CodeValidationLevel

interface AggregatePartValidation {

	val seqNr: Int

	val validationLevel: CodeValidationLevel

	val message: String

	val path: String?

}
