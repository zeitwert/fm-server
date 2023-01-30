
package io.zeitwert.fm.doc.model;

import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocRepository;
import io.zeitwert.ddd.doc.model.db.tables.records.DocRecord;

public interface DocVRepository extends DocRepository<Doc, DocRecord> {

}
