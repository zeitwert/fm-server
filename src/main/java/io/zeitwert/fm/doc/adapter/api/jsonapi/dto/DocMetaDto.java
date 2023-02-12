package io.zeitwert.fm.doc.adapter.api.jsonapi.dto;

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
public class DocMetaDto extends AggregateMetaDto {

	private EnumeratedDto caseDef;
	private EnumeratedDto caseStage;
	private boolean isInWork;
	private EnumeratedDto assignee;

	private List<EnumeratedDto> caseStages;
	private List<String> availableActions; // TODO implement
	private List<DocPartTransitionDto> transitions;

}
