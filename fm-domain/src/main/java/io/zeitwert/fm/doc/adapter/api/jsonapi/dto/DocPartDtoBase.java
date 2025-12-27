package io.zeitwert.fm.doc.adapter.api.jsonapi.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import com.fasterxml.jackson.annotation.JsonFormat;
import dddrive.app.doc.model.Doc;
import dddrive.app.doc.model.DocPart;

@Data
@NoArgsConstructor
@SuperBuilder
public abstract class DocPartDtoBase<D extends Doc, P extends DocPart<D>> {

	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private String id;

	public static void fromPart(DocPartDtoBaseBuilder<?, ?, ?, ?> dtoBuilder, DocPart<?> part) {
		dtoBuilder.id(String.valueOf(part.getId()));
	}

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	public Integer getId() {
		try {
			return Integer.valueOf(this.id);
		} catch (NumberFormatException nfe) {
			return null;
		}
	}

	public void setId(String id) {
		this.id = id;
	}

	public void toPart(P part) {
	}

}
