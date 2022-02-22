package fm.comunas.ddd.obj.model.base;

import fm.comunas.ddd.part.model.base.PartFields;

import org.jooq.Field;
import org.jooq.impl.DSL;

public interface ObjPartFields extends PartFields {

	static final Field<Integer> OBJ_ID = DSL.field("obj_id", Integer.class);

}
