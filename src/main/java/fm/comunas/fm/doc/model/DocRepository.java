
package fm.comunas.fm.doc.model;

import java.util.List;

import fm.comunas.ddd.aggregate.model.AggregateRepository;
import fm.comunas.ddd.doc.model.Doc;
import fm.comunas.ddd.doc.model.db.tables.records.DocRecord;
import fm.comunas.ddd.oe.model.ObjUser;

public interface DocRepository extends AggregateRepository<Doc, DocRecord> {

	/**
	 * Return the number of docs that are related to this item.
	 *
	 * @param item
	 * @return
	 */
	// Integer getCount(Item item);

	/**
	 * Change the owner to a list of docs.
	 *
	 * @param docs
	 */
	void changeOwner(List<Doc> docs, ObjUser user);

	/**
	 * Return the number of document parts from a doc.
	 *
	 * @param item
	 * @return
	 */
	// Integer getDocumentCount(Item item);

}
