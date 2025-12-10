
package io.zeitwert.fm.obj.adapter.api.jsonapi.dto;

import io.crnk.core.resource.annotations.JsonApiField;
import io.crnk.core.resource.annotations.JsonApiMetaInformation;
import io.dddrive.ddd.adapter.api.jsonapi.dto.AggregateDtoBase;
import io.dddrive.obj.model.Obj;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class ObjDtoBase<O extends Obj> extends AggregateDtoBase<O> {

	@JsonApiMetaInformation
	private ObjMetaDto meta;

	@JsonApiField(readable = false, filterable = true)
	public Boolean getIsClosed() {
		return null;
	}

}
