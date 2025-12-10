package io.zeitwert.fm.obj.adapter.api.jsonapi.dto;

import io.dddrive.ddd.model.PartPersistenceStatus;
import io.dddrive.ddd.model.base.PartSPI;
import io.dddrive.obj.model.Obj;
import io.dddrive.obj.model.ObjPart;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@SuperBuilder
public abstract class ObjPartDtoBase<O extends Obj, P extends ObjPart<O>> {

	public static final String ServerNewIdPrefix = "New:";

	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private String id;

	@JsonProperty("id")
	public String getDtoId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@JsonIgnore
	public Integer getPartId() {
		try {
			return Integer.valueOf(this.id);
		} catch (NumberFormatException nfe) {
			return null;
		}
	}

	public void toPart(P part) {
	}

	public static void fromPart(ObjPartDtoBaseBuilder<?, ?, ?, ?> dtoBuilder, ObjPart<?> part) {
		boolean isNew = ((PartSPI<?>) part).getPersistenceStatus() == PartPersistenceStatus.CREATED;
		dtoBuilder.id(isNew ? ServerNewIdPrefix + part.getId() : String.valueOf(part.getId()));
	}

}
