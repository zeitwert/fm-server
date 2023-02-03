
package io.zeitwert.ddd.doc.model.base;

import io.zeitwert.ddd.aggregate.model.base.AggregateFields;

import org.jooq.Field;
import org.jooq.impl.DSL;

public interface DocFields extends AggregateFields {

	static final Field<String> DOC_TYPE_ID = DSL.field("doc_type_id", String.class);
	static final Field<String> CASE_DEF_ID = DSL.field("case_def_id", String.class);
	static final Field<String> CASE_STAGE_ID = DSL.field("case_stage_id", String.class);
	static final Field<Boolean> IS_IN_WORK = DSL.field("is_in_work", Boolean.class);
	static final Field<Integer> ASSIGNEE_ID = DSL.field("assignee_id", Integer.class);

	static final String TRANSITION_LIST = "doc.transitionList";

}
