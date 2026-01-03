package io.zeitwert.fm.doc.adapter.api.jsonapi.dto;

import dddrive.app.doc.model.Doc;
import io.crnk.core.resource.annotations.JsonApiField;
import io.crnk.core.resource.annotations.JsonApiMetaInformation;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.AggregateDtoBase;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.EnumeratedDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public abstract class DocDtoBase<D extends Doc> extends AggregateDtoBase<D> {

	private EnumeratedDto assignee;
	private EnumeratedDto caseDef;
	private EnumeratedDto caseStage;

	@JsonApiMetaInformation
	private DocMetaDto meta;

	@JsonApiField(readable = false, filterable = true)
	public Boolean getIsInWork() {
		return null;
	}

	@JsonApiField(readable = false, filterable = true)
	public String getCaseStageId() {
		return null;
	}

}
