package io.zeitwert.fm.dms.model.base;

import io.zeitwert.ddd.obj.model.base.ObjExtnFields;

import org.jooq.Field;
import org.jooq.impl.DSL;

public interface ObjDocumentFields extends ObjExtnFields {

	static final Field<String> NAME = DSL.field("name", String.class);
	static final Field<String> DOCUMENT_KIND_ID = DSL.field("document_kind_id", String.class);
	static final Field<String> DOCUMENT_CATEGORY_ID = DSL.field("document_category_id", String.class);
	static final Field<Integer> TEMPLATE_DOCUMENT_ID = DSL.field("template_document_id", Integer.class);
	static final Field<String> CONTENT_KIND_ID = DSL.field("content_kind_id", String.class);

}
