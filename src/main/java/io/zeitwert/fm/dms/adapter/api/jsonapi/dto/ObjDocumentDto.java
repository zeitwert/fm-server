package io.zeitwert.fm.dms.adapter.api.jsonapi.dto;

import io.crnk.core.resource.annotations.JsonApiResource;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.obj.adapter.api.jsonapi.dto.FMObjDtoBase;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data()
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ToString(callSuper = true, includeFieldNames = true)
@JsonApiResource(type = "document", resourcePath = "document/documents")
public class ObjDocumentDto extends FMObjDtoBase<ObjDocument> {

	private String name;
	private EnumeratedDto contentKind;
	private String supportedContentTypes;
	private EnumeratedDto documentKind;
	private EnumeratedDto documentCategory;
	private EnumeratedDto contentType;

}
