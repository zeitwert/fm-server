package fm.comunas.fm.obj.adapter.api.jsonapi.dto;

import fm.comunas.ddd.obj.adapter.api.jsonapi.dto.ObjDtoBase;
import fm.comunas.ddd.obj.model.Obj;
import fm.comunas.ddd.session.model.SessionInfo;
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
public abstract class ObjVDtoBase<O extends Obj> extends ObjDtoBase<O> {

	private Integer refObjId;

	// TODO provide generic Dto
	private Obj refObj;

	public void toObj(O obj) {
		super.toObj(obj);
	}

	public static void fromObj(ObjVDtoBaseBuilder<?, ?, ?> dtoBuilder, Obj obj, SessionInfo sessionInfo) {
		ObjDtoBase.fromObj(dtoBuilder, obj, sessionInfo);
	}

	public static void fromRecord(ObjVDtoBaseBuilder<?, ?, ?> dtoBuilder, Record obj, SessionInfo sessionInfo) {
		ObjDtoBase.fromRecord(dtoBuilder, obj, sessionInfo);
	}

}
