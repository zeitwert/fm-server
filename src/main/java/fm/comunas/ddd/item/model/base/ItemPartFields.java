package fm.comunas.ddd.item.model.base;

import fm.comunas.ddd.part.model.base.PartFields;

import org.jooq.Field;
import org.jooq.impl.DSL;

public interface ItemPartFields extends PartFields {

	static final Field<Integer> ITEM_ID = DSL.field("item_id", Integer.class);

}
