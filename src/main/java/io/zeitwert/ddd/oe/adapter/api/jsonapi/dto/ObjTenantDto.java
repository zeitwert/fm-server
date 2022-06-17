package io.zeitwert.ddd.oe.adapter.api.jsonapi.dto;

import io.crnk.core.resource.annotations.JsonApiId;
import io.zeitwert.ddd.oe.model.ObjTenant;
import lombok.Builder;
import lombok.Data;

@Data()
@Builder
public class ObjTenantDto {

	@JsonApiId
	private Integer id;
	private String caption;
	private String name;
	private String extlKey;

	public static ObjTenantDto fromObj(ObjTenant obj) {
		if (obj == null) {
			return null;
		}
		// @formatter:off
		return ObjTenantDto.builder()
			.id(obj.getId())
			.caption(obj.getCaption())
			.name(obj.getName())
			.extlKey(obj.getExtlKey())
			.build();
		// @formatter:on
	}

}
