package io.zeitwert.fm.dms.model.enums;

import io.zeitwert.ddd.enums.model.base.EnumeratedBase;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
public final class CodeContentKind extends EnumeratedBase {

	@Override
	public boolean equals(Object other) {
		return super.equals(other);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public List<CodeContentType> getContentTypes() {
		return CodeContentTypeEnum.getContentTypes(this);
	}

	public List<String> getExtensions() {
		return this.getContentTypes().stream().map(ct -> "." + ct.getExtension()).toList();
	}

}
