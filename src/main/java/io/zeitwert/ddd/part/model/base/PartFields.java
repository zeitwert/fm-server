package io.zeitwert.ddd.part.model.base;

import org.jooq.Field;
import org.jooq.impl.DSL;

public interface PartFields {

	static final Field<Integer> ID = DSL.field("id", Integer.class);
	static final Field<Integer> PARENT_PART_ID = DSL.field("parent_part_id", Integer.class);
	static final Field<String> PART_LIST_TYPE_ID = DSL.field("part_list_type_id", String.class);
	static final Field<Integer> SEQ_NR = DSL.field("seq_nr", Integer.class);

}
