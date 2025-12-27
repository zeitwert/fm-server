package io.zeitwert.fm.oe.adapter.api.jsonapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiRelationId;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.SerializeType;
import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;
import io.zeitwert.fm.dms.adapter.api.jsonapi.dto.ObjDocumentDto;
import io.zeitwert.fm.obj.adapter.api.jsonapi.dto.ObjDtoBase;
import io.zeitwert.fm.oe.adapter.api.jsonapi.impl.ObjUserDtoAdapter;
import io.zeitwert.fm.oe.model.ObjUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data()
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ToString(callSuper = true, includeFieldNames = true)
@JsonApiResource(type = "user", resourcePath = "oe/users")
public class ObjUserDto extends ObjDtoBase<ObjUser> {

	private static final DateTimeFormatter touchFmt = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
	List<EnumeratedDto> tenants;
	private String email;
	private String name;
	private String description;

	private EnumeratedDto role;
	private Boolean needPasswordChange; // write: change password
	private String password; // write: change password
	@JsonApiRelationId
	private Integer avatarId;
	@JsonIgnore
	private ObjDocumentDto avatarDto;

	@Override
	public ObjUserDtoAdapter getAdapter() {
		return (ObjUserDtoAdapter) super.getAdapter();
	}

	public String getLastTouch() {
		OffsetDateTime lastTouch = this.getAdapter().getLastTouch(this.getId());
		return lastTouch != null ? touchFmt.format(lastTouch) : null;
	}

	public void setAvatarId(Integer avatarId) {
		// assertThis(false, "avatarId is read-only");
	}

	@JsonApiRelation(serialize = SerializeType.LAZY)
	public ObjDocumentDto getAvatar() {
		if (this.avatarDto == null) {
			this.avatarDto = this.getAdapter().getDocumentDto(this.avatarId);
		}
		return this.avatarDto;
	}

	public void setAvatar(ObjDocumentDto avatar) {
		// assertThis(false, "avatar is read-only");
	}

}
