package fm.comunas.ddd.doc.model.base;

import fm.comunas.ddd.part.model.base.PartFields;

import org.jooq.Field;
import org.jooq.impl.DSL;

public interface DocPartFields extends PartFields {

	static final Field<Integer> DOC_ID = DSL.field("doc_id", Integer.class);

}
