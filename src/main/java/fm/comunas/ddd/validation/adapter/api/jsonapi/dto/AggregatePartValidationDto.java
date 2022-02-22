package fm.comunas.ddd.validation.adapter.api.jsonapi.dto;

import fm.comunas.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import fm.comunas.ddd.validation.model.AggregatePartValidation;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AggregatePartValidationDto {

	private Integer seqNr;
	private EnumeratedDto validationLevel;
	private String validation;

	public static AggregatePartValidationDto fromValidation(AggregatePartValidation validation) {
		// @formatter:off
		return AggregatePartValidationDto.builder()
			.seqNr(validation.getSeqNr())
			.validationLevel(EnumeratedDto.fromEnum(validation.getValidationLevel()))
			.validation(validation.getValidation())
			.build();
		// @formatter:on
	}

}
