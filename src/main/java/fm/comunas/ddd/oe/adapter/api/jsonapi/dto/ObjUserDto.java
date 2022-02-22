package fm.comunas.ddd.oe.adapter.api.jsonapi.dto;

import io.crnk.core.resource.annotations.JsonApiId;
import fm.comunas.ddd.oe.model.ObjUser;
import lombok.Builder;
import lombok.Data;

@Data()
@Builder
public class ObjUserDto {

	@JsonApiId
	private Integer id;
	private String caption;
	private String name;
	private String email;
	private String picture;

	public static ObjUserDto fromObj(ObjUser obj) {
		if (obj == null) {
			return null;
		}
		// @formatter:off
		return ObjUserDto.builder()
			.id(obj.getId())
			.caption(obj.getCaption())
			.name(obj.getName())
			.email(obj.getEmail())
			.picture(obj.getPicture())
			.build();
		// @formatter:on
	}

}
