package io.zeitwert.fm.dms.model.base;

import io.zeitwert.fm.obj.model.base.FMObjFields;

import org.jooq.Field;
import org.jooq.impl.DSL;

public interface ObjDocumentFields extends FMObjFields {

	static final Field<Integer> OBJ_ID = DSL.field("obj_id", Integer.class);

	static final Field<String> NAME = DSL.field("name", String.class);
	static final Field<String> DOCUMENT_KIND_ID = DSL.field("document_kind_id", String.class);
	static final Field<String> DOCUMENT_CATEGORY_ID = DSL.field("document_category_id", String.class);
	static final Field<Integer> TEMPLATE_DOCUMENT_ID = DSL.field("template_document_id", Integer.class);
	static final Field<String> CONTENT_KIND_ID = DSL.field("content_kind_id", String.class);

}
