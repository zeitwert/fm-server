package io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto

import dddrive.app.validation.model.AggregatePartValidation

data class AggregatePartValidationDto(
	val seqNr: Int?,
	val validationLevel: EnumeratedDto?,
	val validation: String,
) {

	companion object {

		@JvmStatic
		fun fromValidation(validation: AggregatePartValidation): AggregatePartValidationDto =
			AggregatePartValidationDto(
				validation.seqNr,
				EnumeratedDto.of(validation.validationLevel),
				validation.message,
			)
	}

}
