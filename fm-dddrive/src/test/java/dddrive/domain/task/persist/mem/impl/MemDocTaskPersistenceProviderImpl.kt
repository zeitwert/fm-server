package dddrive.domain.task.persist.mem.impl

import dddrive.ddd.path.getPropertyByPath
import dddrive.ddd.path.setValueByPath
import dddrive.ddd.property.model.PartListProperty
import dddrive.domain.doc.persist.mem.base.MemDocPersistenceProviderBase
import dddrive.domain.task.model.DocTask
import dddrive.domain.task.model.DocTaskPartComment
import dddrive.domain.task.model.enums.CodeTaskPriority
import dddrive.domain.task.persist.DocTaskPersistenceProvider
import dddrive.domain.task.persist.mem.pto.DocTaskPartCommentPto
import dddrive.domain.task.persist.mem.pto.DocTaskPto
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

			// Load ONLY DocTask-specific properties here
			aggregate.subject = pto.subject
			aggregate.content = pto.content
			aggregate.isPrivate = pto.isPrivate
			aggregate.priority = pto.priority?.let {
				CodeTaskPriority
					.getItem(it)
			}
			aggregate.dueAt = pto.dueAt
			aggregate.remindAt = pto.remindAt

			// Load comments
			val commentListProperty =
				aggregate.getPropertyByPath<DocTaskPartComment>("commentList") as? PartListProperty<DocTask, DocTaskPartComment>
			commentListProperty?.clear()
			pto.comments?.forEach { commentPto ->
				val comment = commentListProperty?.add(commentPto.id)
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
			isPrivate = aggregate.isPrivate,
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
