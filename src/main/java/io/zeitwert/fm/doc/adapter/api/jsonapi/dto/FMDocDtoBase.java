package io.zeitwert.fm.doc.adapter.api.jsonapi.dto;

import io.zeitwert.fm.doc.model.FMDoc;
import io.zeitwert.ddd.doc.adapter.api.jsonapi.dto.DocDtoBase;
import io.zeitwert.ddd.session.model.SessionInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import org.jooq.Record;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ToString(callSuper = true, includeFieldNames = true)
public abstract class FMDocDtoBase<O extends FMDoc> extends DocDtoBase<O> {

	public void toDoc(O doc) {
		super.toDoc(doc);
	}

	public static void fromDoc(FMDocDtoBaseBuilder<?, ?, ?> dtoBuilder, FMDoc doc, SessionInfo sessionInfo) {
		DocDtoBase.fromDoc(dtoBuilder, doc, sessionInfo);
	}

	public static void fromRecord(FMDocDtoBaseBuilder<?, ?, ?> dtoBuilder, Record doc, SessionInfo sessionInfo) {
		DocDtoBase.fromRecord(dtoBuilder, doc, sessionInfo);
	}

}
