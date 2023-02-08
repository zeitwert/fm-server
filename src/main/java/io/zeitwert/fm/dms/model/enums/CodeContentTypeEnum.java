
package io.zeitwert.fm.dms.model.enums;

import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;
import io.zeitwert.fm.dms.model.db.Tables;
import io.zeitwert.fm.dms.model.db.tables.records.CodeContentTypeRecord;

@Component("codeContentTypeEnum")
@DependsOn({ "flyway", "flywayInitializer", "codeContentKindEnum" })
public class CodeContentTypeEnum extends EnumerationBase<CodeContentType> {

	private static CodeContentTypeEnum INSTANCE;

	private final CodeContentKindEnum codeContentKindEnum;

	protected CodeContentTypeEnum(AppContext appContext,
			final CodeContentKindEnum codeContentKindEnum) {
		super(appContext, CodeContentType.class);
		this.codeContentKindEnum = codeContentKindEnum;
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeContentTypeRecord item : this.getDslContext().selectFrom(Tables.CODE_CONTENT_TYPE).fetch()) {
			CodeContentKind contentKind = this.codeContentKindEnum.getItem(item.getContentKindId());
			CodeContentType contentType = CodeContentType.builder()
					.enumeration(this)
					.id(item.getId())
					.name(item.getName())
					.contentKind(contentKind)
					.extension(item.getExtension())
					.mimeType(item.getMimeType())
					.build();
			this.addItem(contentType);
		}
	}

	public static CodeContentType getContentType(String itemId) {
		return INSTANCE.getItem(itemId);
	}

	public static CodeContentType getContentType(String mimeType, String fileName) {
		Optional<CodeContentType> maybeContentType = null;
		if (mimeType != null && !mimeType.isEmpty()) {
			maybeContentType = getItemByMimeType(mimeType);
			if (maybeContentType.isPresent()) {
				return maybeContentType.get();
			}
		}
		if (fileName != null && !fileName.isEmpty() && fileName.indexOf(".") >= 0) {
			String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
			maybeContentType = getItemByExtension(extension);
			if (maybeContentType.isPresent()) {
				return maybeContentType.get();
			}
		}
		return null;
	}

	public static Optional<CodeContentType> getItemByMimeType(String mimeType) {
		return INSTANCE.getItems()
				.stream()
				.filter((CodeContentType cct) -> cct.getMimeType().equalsIgnoreCase(mimeType))
				.findAny();
	}

	public static Optional<CodeContentType> getItemByExtension(String extension) {
		return INSTANCE.getItems()
				.stream()
				.filter((CodeContentType cct) -> cct.getExtension().equalsIgnoreCase(extension))
				.findAny();
	}

	public static List<CodeContentType> getContentTypes(CodeContentKind contentKind) {
		return INSTANCE.getItems()
				.stream()
				.filter((CodeContentType cct) -> cct.getContentKind() == contentKind)
				.toList();
	}

}
