package io.zeitwert.fm.doc.model;

import io.zeitwert.ddd.doc.model.Doc;

import org.jooq.Record;

public interface FMDocRepository<D extends Doc, V extends Record>
		extends io.zeitwert.ddd.doc.model.DocRepository<D, V> {
}
