package dddrive.domain.task.persist.mem.pto

import dddrive.domain.doc.persist.mem.pto.DocPto
import io.dddrive.domain.doc.persist.mem.pto.DocMetaPto
import java.time.OffsetDateTime

open class DocTaskPto(
	var subject: String? = null,
	var content: String? = null,
	var isPrivate: Boolean? = null,
	var priority: String? = null,
	var dueAt: OffsetDateTime? = null,
	var remindAt: OffsetDateTime? = null,
	var comments: List<DocTaskPartCommentPto>? = null,
	// Properties from AggregatePto (passed to DocPto constructor)
	id: Int? = null,
	tenantId: Int? = null,
	meta: DocMetaPto? = null,
	caption: String? = null,
) : DocPto(id, tenantId, meta, caption) {

	override fun getDocTypeId() = AGGREGATE_TYPE

	companion object {

		const val AGGREGATE_TYPE = "docTask"
	}
}
