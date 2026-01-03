package io.zeitwert.fm.obj.adapter.api.jsonapi.dto;

import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.AggregateMetaDto;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.EnumeratedDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ObjMetaDto extends AggregateMetaDto {

	private EnumeratedDto closedByUser;
	private OffsetDateTime closedAt;
	private List<ObjPartTransitionDto> transitions;

}
