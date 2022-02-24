package io.zeitwert.ddd.doc.adapter.api.jsonapi.dto;

import io.crnk.core.resource.annotations.JsonApiResource;
import io.zeitwert.ddd.doc.model.Doc;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * Artificial crnk.io resource as parent for generic DocParts (f.ex.
 * transitions)
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@JsonApiResource(type = "doc", resourcePath = "doc")
public abstract class DocDto extends DocDtoBase<Doc> {

}
