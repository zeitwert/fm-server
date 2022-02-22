
package fm.comunas.ddd.obj.adapter.api.jsonapi.dto;

import io.crnk.core.resource.annotations.JsonApiResource;
import fm.comunas.ddd.obj.model.Obj;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * Artificial crnk.io resource as parent for generic ObjParts (f.ex.
 * transitions)
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@JsonApiResource(type = "obj", resourcePath = "obj")
public abstract class ObjDto extends ObjDtoBase<Obj> {

}
