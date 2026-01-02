package io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base;

import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.AggregateMetaDto;
import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;
import io.zeitwert.fm.obj.adapter.api.jsonapi.dto.ObjMetaDto;
import io.zeitwert.fm.obj.adapter.api.jsonapi.dto.ObjPartTransitionDto;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Helper class to work around Lombok visibility issues when accessing
 * from Kotlin code (Kotlin compiles before Java, so Lombok-generated
 * methods aren't visible during Kotlin compilation).
 */
public class GenericDtoHelper {

	/**
	 * Create an ObjMetaDto with the given values.
	 */
	public static ObjMetaDto createObjMetaDto(
			EnumeratedDto itemType,
			EnumeratedDto owner,
			Integer version,
			EnumeratedDto createdByUser,
			OffsetDateTime createdAt,
			EnumeratedDto modifiedByUser,
			OffsetDateTime modifiedAt,
			EnumeratedDto closedByUser,
			OffsetDateTime closedAt,
			List<ObjPartTransitionDto> transitions) {
		ObjMetaDto dto = new ObjMetaDto();
		dto.setItemType(itemType);
		dto.setOwner(owner);
		dto.setVersion(version);
		dto.setCreatedByUser(createdByUser);
		dto.setCreatedAt(createdAt);
		dto.setModifiedByUser(modifiedByUser);
		dto.setModifiedAt(modifiedAt);
		dto.setClosedByUser(closedByUser);
		dto.setClosedAt(closedAt);
		dto.setTransitions(transitions);
		return dto;
	}

	/**
	 * Get the client version from an AggregateMetaDto.
	 */
	public static Integer getClientVersion(AggregateMetaDto meta) {
		return meta != null ? meta.getClientVersion() : null;
	}

}
