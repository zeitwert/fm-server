package io.zeitwert.fm.obj.adapter.api.jsonapi.dto;

import io.zeitwert.fm.obj.model.FMObj;
import io.zeitwert.ddd.obj.adapter.api.jsonapi.dto.ObjDtoBase;
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
public abstract class FMObjDtoBase<O extends FMObj> extends ObjDtoBase<O> {

	public void toObj(O obj) {
		super.toObj(obj);
	}

	public static void fromObj(FMObjDtoBaseBuilder<?, ?, ?> dtoBuilder, FMObj obj, SessionInfo sessionInfo) {
		ObjDtoBase.fromObj(dtoBuilder, obj, sessionInfo);
	}

	public static void fromRecord(FMObjDtoBaseBuilder<?, ?, ?> dtoBuilder, Record obj, SessionInfo sessionInfo) {
		ObjDtoBase.fromRecord(dtoBuilder, obj, sessionInfo);
	}

}
