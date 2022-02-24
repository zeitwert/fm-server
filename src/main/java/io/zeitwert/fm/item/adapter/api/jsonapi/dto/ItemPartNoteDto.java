package io.zeitwert.fm.item.adapter.api.jsonapi.dto;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.item.adapter.api.jsonapi.dto.ItemPartDtoBase;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjUserDto;
import io.zeitwert.fm.item.model.ItemPartNote;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@Data()
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ToString(callSuper = true, includeFieldNames = true)
public class ItemPartNoteDto extends ItemPartDtoBase<Aggregate, ItemPartNote<Aggregate>> {

	private String subject;
	private String content;
	private Boolean isPrivate;

	private ObjUserDto createdByUser;
	private OffsetDateTime createdAt;
	private ObjUserDto modifiedByUser;
	private OffsetDateTime modifiedAt;

	public void toPart(ItemPartNote<?> part) {
		// super.toPart(part);
		part.setSubject(this.subject);
		part.setContent(this.content);
		part.setIsPrivate(this.isPrivate);
	}

	public static ItemPartNoteDto fromPart(ItemPartNote<?> part) {
		if (part == null) {
			return null;
		}
		ItemPartNoteDtoBuilder<?, ?> dtoBuilder = ItemPartNoteDto.builder();
		ItemPartDtoBase.fromPart(dtoBuilder, part);
		// @formatter:off
		return dtoBuilder
			.subject(part.getSubject())
			.content(part.getContent())
			.isPrivate(part.getIsPrivate())
			.createdByUser(ObjUserDto.fromObj(part.getCreatedByUser()))
			.createdAt(part.getCreatedAt())
			.modifiedByUser(ObjUserDto.fromObj(part.getModifiedByUser()))
			.modifiedAt(part.getModifiedAt())
			.build();
		// @formatter:on
	}

}
