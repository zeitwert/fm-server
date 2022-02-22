package fm.comunas.ddd.obj.adapter.api.jsonapi.dto;

import fm.comunas.ddd.obj.model.Obj;
import fm.comunas.ddd.obj.model.ObjPart;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@NoArgsConstructor
@SuperBuilder
public abstract class ObjPartDtoBase<O extends Obj, P extends ObjPart<O>> {

	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private String id;

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

	public static void fromPart(ObjPartDtoBaseBuilder<?, ?, ?, ?> dtoBuilder, ObjPart<?> part) {
		dtoBuilder.id(String.valueOf(part.getId()));
	}

}
