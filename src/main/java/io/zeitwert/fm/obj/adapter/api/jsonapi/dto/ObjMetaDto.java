
package io.zeitwert.fm.obj.adapter.api.jsonapi.dto;

import java.time.OffsetDateTime;
import java.util.List;

import io.dddrive.ddd.adapter.api.jsonapi.dto.AggregateMetaDto;
import io.dddrive.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ObjMetaDto extends AggregateMetaDto {

	private EnumeratedDto closedByUser;
	private OffsetDateTime closedAt;
	private List<ObjPartTransitionDto> transitions;

}
