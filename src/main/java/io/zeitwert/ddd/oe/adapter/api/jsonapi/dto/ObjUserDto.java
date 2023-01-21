package io.zeitwert.ddd.oe.adapter.api.jsonapi.dto;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiRelationId;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.SerializeType;
import io.zeitwert.ddd.aggregate.adapter.api.jsonapi.dto.AggregateDtoBase;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.obj.adapter.api.jsonapi.dto.ObjDtoBase;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.service.api.ObjUserCache;
import io.zeitwert.fm.dms.adapter.api.jsonapi.dto.ObjDocumentDto;
import io.zeitwert.fm.dms.adapter.api.jsonapi.impl.ObjDocumentDtoAdapter;
import io.zeitwert.fm.dms.model.ObjDocument;
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
@JsonApiResource(type = "user", resourcePath = "oe/users")
public class ObjUserDto extends ObjDtoBase<ObjUser> {

	private static final DateTimeFormatter touchFmt = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

	private String email;
	private String name;
	private String description;

	private EnumeratedDto role;
	List<EnumeratedDto> tenants;

	private Boolean needPasswordChange; // write: change password
	private String password; // write: change password

	public String getLastTouch() {
		OffsetDateTime lastTouch = AggregateDtoBase.getService(ObjUserCache.class).getLastTouch(this.getId());
		return lastTouch != null ? touchFmt.format(lastTouch) : null;
	}

	@JsonApiRelationId
	private Integer avatarId;

	public void setAvatarId(Integer avatarId) {
		// assertThis(false, "avatarId is read-only");
	}

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
		// assertThis(false, "avatar is read-only");
	}

}
