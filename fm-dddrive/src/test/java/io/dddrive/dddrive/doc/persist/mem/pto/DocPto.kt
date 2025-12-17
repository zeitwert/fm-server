package io.dddrive.dddrive.doc.persist.mem.pto

import io.dddrive.dddrive.ddd.persist.mem.pto.AggregatePto
import io.dddrive.domain.doc.persist.mem.pto.DocMetaPto

abstract class DocPto(
	// Properties from AggregatePto
	id: Int? = null,
	tenantId: Int? = null,
	meta: DocMetaPto? = null,
	caption: String? = null,
) : AggregatePto(id, tenantId, meta, caption) {

	// The actual type ID of the document (e.g., "docTask", "docInvoice")
	abstract fun getDocTypeId(): String?

	// Override the meta property with the covariant return type DocMetaPto
	override val meta: DocMetaPto?
		get() = super.meta as? DocMetaPto
}
