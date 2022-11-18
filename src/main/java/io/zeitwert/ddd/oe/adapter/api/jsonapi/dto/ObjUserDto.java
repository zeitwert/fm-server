package io.zeitwert.ddd.oe.adapter.api.jsonapi.dto;

import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiRelationId;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.SerializeType;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.obj.adapter.api.jsonapi.dto.ObjDtoBase;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.fm.dms.adapter.api.jsonapi.dto.ObjDocumentDto;
import io.zeitwert.fm.dms.adapter.api.jsonapi.impl.ObjDocumentDtoAdapter;
import io.zeitwert.fm.dms.model.ObjDocument;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data()
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ToString(callSuper = true, includeFieldNames = true)
@JsonApiResource(type = "user", resourcePath = "oe/users")
public class ObjUserDto extends ObjDtoBase<ObjUser> {

	private EnumeratedDto tenant;
	private String email;
	private String password;
	private String role;
	private String name;
	private String description;

	@JsonApiRelationId
	private Integer avatarId;

	@JsonIgnore
	private ObjDocumentDto avatarDto;

	@JsonApiRelation(serialize = SerializeType.LAZY)
	public ObjDocumentDto getAvatar() {
		if (this.avatarDto == null) {
			ObjDocument avatar = null;
			if (this.getOriginal() != null) {
				avatar = this.getOriginal().getAvatarImage();
			} else if (this.avatarId != null) {
				avatar = getRepository(ObjDocument.class).get(this.avatarId);
			}
			this.avatarDto = ObjDocumentDtoAdapter.getInstance().fromAggregate(avatar);
		}
		return this.avatarDto;
	}

	public void setAvatar(ObjDocumentDto avatar) {
	}

}
