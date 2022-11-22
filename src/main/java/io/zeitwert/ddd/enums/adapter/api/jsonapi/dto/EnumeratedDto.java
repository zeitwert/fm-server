package io.zeitwert.ddd.enums.adapter.api.jsonapi.dto;

import io.crnk.core.resource.annotations.JsonApiId;
import io.zeitwert.ddd.enums.model.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data()
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnumeratedDto {

	@JsonApiId
	private String id;

	private String name;

	private EnumeratedDto itemType;

	public static EnumeratedDto fromEnum(Enumerated e) {
		if (e == null) {
			return null;
		}
		// @formatter:off
		return EnumeratedDto.builder()
			.id(e.getId())
			.name(e.getName())
			.build();
		// @formatter:on
	}

}
