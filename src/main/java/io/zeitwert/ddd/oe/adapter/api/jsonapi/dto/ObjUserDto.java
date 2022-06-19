package io.zeitwert.ddd.oe.adapter.api.jsonapi.dto;

import io.zeitwert.ddd.obj.adapter.api.jsonapi.dto.ObjDtoBase;
import io.zeitwert.ddd.oe.model.ObjUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data()
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ToString(callSuper = true, includeFieldNames = true)
public class ObjUserDto extends ObjDtoBase<ObjUser> {

	private String name;
	private String email;
	private String picture;

}
