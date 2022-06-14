package io.zeitwert.fm.doc.adapter.api.jsonapi.dto;

import io.zeitwert.fm.doc.model.FMDoc;
import io.zeitwert.ddd.doc.adapter.api.jsonapi.dto.DocDtoBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ToString(callSuper = true, includeFieldNames = true)
public abstract class FMDocDtoBase<O extends FMDoc> extends DocDtoBase<O> {
}
