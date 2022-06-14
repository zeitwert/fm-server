package io.zeitwert.fm.obj.adapter.api.jsonapi.dto;

import io.zeitwert.fm.obj.model.FMObj;
import io.zeitwert.ddd.obj.adapter.api.jsonapi.dto.ObjDtoBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ToString(callSuper = true, includeFieldNames = true)
public abstract class FMObjDtoBase<O extends FMObj> extends ObjDtoBase<O> {
}
