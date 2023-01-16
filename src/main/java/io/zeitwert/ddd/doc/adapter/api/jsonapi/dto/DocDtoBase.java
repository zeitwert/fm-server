
package io.zeitwert.ddd.doc.adapter.api.jsonapi.dto;

import io.crnk.core.resource.annotations.JsonApiField;
import io.crnk.core.resource.annotations.JsonApiMetaInformation;
import io.zeitwert.ddd.aggregate.adapter.api.jsonapi.dto.AggregateDtoBase;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
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
