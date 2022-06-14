
package io.zeitwert.ddd.doc.adapter.api.jsonapi.dto;

import io.crnk.core.resource.annotations.JsonApiMetaInformation;
import io.zeitwert.ddd.aggregate.adapter.api.jsonapi.dto.AggregateDtoBase;
import io.zeitwert.ddd.doc.model.Doc;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public abstract class DocDtoBase<D extends Doc> extends AggregateDtoBase<D> {

	@JsonApiMetaInformation
	private DocMetaDto meta;

}
