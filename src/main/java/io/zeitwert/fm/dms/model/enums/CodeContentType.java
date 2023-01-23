package io.zeitwert.fm.dms.model.enums;

import io.zeitwert.ddd.enums.model.base.EnumeratedBase;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import org.springframework.http.MediaType;

@Data
@SuperBuilder
public final class CodeContentType extends EnumeratedBase {

	private final CodeContentKind contentKind;
	private final String extension;
	private final String mimeType;

	@Override
	public boolean equals(Object other) {
		return super.equals(other);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public CodeContentKind getContentKind() {
		return this.contentKind;
	}

	public String getExtension() {
		return this.extension;
	}

	public String getMimeType() {
		return this.mimeType;
	}

	public MediaType getMediaType() {
		return MediaType.parseMediaType(this.mimeType);
	}

}
