package io.dddrive.domain.task.persist.mem.impl

import io.dddrive.core.property.model.PartListProperty
import io.dddrive.dddrive.doc.persist.mem.base.MemDocPersistenceProviderBase
import io.dddrive.domain.task.model.DocTask
import io.dddrive.domain.task.model.DocTaskPartComment
import io.dddrive.domain.task.model.enums.CodeTaskPriority
import io.dddrive.domain.task.persist.DocTaskPersistenceProvider
import io.dddrive.domain.task.persist.mem.pto.DocTaskPartCommentPto
import io.dddrive.domain.task.persist.mem.pto.DocTaskPto
import io.dddrive.path.getPropertyByPath
import io.dddrive.path.setValueByPath
import org.springframework.stereotype.Component

@Component("docTaskPersistenceProvider")
class MemDocTaskPersistenceProviderImpl :
	MemDocPersistenceProviderBase<DocTask, DocTaskPto>(DocTask::class.java),
	DocTaskPersistenceProvider {

	@Suppress("UNCHECKED_CAST")
	override fun toAggregate(
		pto: DocTaskPto,
		aggregate: DocTask,
	) {
		val aggregateMeta = aggregate.meta // Domain object's meta

		try {
			aggregateMeta.disableCalc()
			super.toAggregate(pto, aggregate)
			println("docTask.load.metaPto: ${pto.meta?.assigneeId}")
			println("docTask.load.meta.assignee: ${aggregate.assignee}")

			// Load ONLY DocTask-specific properties here
			aggregate.subject = pto.subject
			aggregate.content = pto.content
			aggregate.private = pto.private
			aggregate.priority = pto.priority?.let { CodeTaskPriority.Enumeration.getItem(it) }
			aggregate.dueAt = pto.dueAt
			aggregate.remindAt = pto.remindAt

			// Load comments
			val commentListProperty =
				aggregate.getPropertyByPath<DocTaskPartComment>("commentList") as? PartListProperty<DocTaskPartComment>
			commentListProperty?.clearParts()
			pto.comments?.forEach { commentPto ->
				val comment = commentListProperty?.addPart(commentPto.id)
				comment?.let { domainComment ->
					domainComment.text = commentPto.text
					domainComment.setValueByPath("createdAt", commentPto.createdAt)
				}
			}
		} finally {
			aggregateMeta.enableCalc()
			aggregate.meta.calcAll()
		}
	}

	override fun fromAggregate(aggregate: DocTask): DocTaskPto {
		val metaPto = this.getMeta(aggregate)
		println("docTask.metaPto: ${metaPto.assigneeId}")
		val commentPtos =
			aggregate.commentList
				.map { domainComment ->
					DocTaskPartCommentPto(
						// Renamed
						id = domainComment.id,
						text = domainComment.text,
						createdAt = domainComment.createdAt,
					)
				}.toList()

		return DocTaskPto(
			// DocTask-specific properties
			subject = aggregate.subject,
			content = aggregate.content,
			private = aggregate.private,
			priority = aggregate.priority?.id,
			dueAt = aggregate.dueAt,
			remindAt = aggregate.remindAt,
			comments = commentPtos,
			// AggregatePto properties
			id = aggregate.id as? Int,
			tenantId = aggregate.tenantId as? Int,
			meta = metaPto,
			caption = aggregate.caption,
		)
	}

}
