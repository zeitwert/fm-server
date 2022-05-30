package io.zeitwert.fm.dms.model.enums;

import io.zeitwert.ddd.enums.model.Enumeration;
import io.zeitwert.ddd.enums.model.base.EnumeratedBase;

import org.springframework.http.MediaType;

public final class CodeContentType extends EnumeratedBase {

	private final CodeContentKind contentKind;
	private final String extension;
	private final String mimeType;

	public CodeContentType(Enumeration<CodeContentType> enumeration, String id, String name,
			CodeContentKind contentKind, String extension, String mimeType) {
		super(enumeration, id, name);
		this.contentKind = contentKind;
		this.extension = extension;
		this.mimeType = mimeType;
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
