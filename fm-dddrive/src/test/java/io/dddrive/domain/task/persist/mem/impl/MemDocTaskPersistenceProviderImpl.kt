package io.dddrive.domain.task.persist.mem.impl

import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.PartListProperty
import io.dddrive.dddrive.doc.persist.mem.base.MemDocPersistenceProviderBase
import io.dddrive.domain.task.model.DocTask
import io.dddrive.domain.task.model.DocTaskPartComment
import io.dddrive.domain.task.model.DocTaskRepository
import io.dddrive.domain.task.model.enums.CodeTaskPriority
import io.dddrive.domain.task.persist.DocTaskPersistenceProvider
import io.dddrive.domain.task.persist.mem.pto.DocTaskPartCommentPto
import io.dddrive.domain.task.persist.mem.pto.DocTaskPto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component("docTaskPersistenceProvider")
class MemDocTaskPersistenceProviderImpl :
	MemDocPersistenceProviderBase<DocTask, DocTaskPto>(DocTask::class.java),
	DocTaskPersistenceProvider {

	private lateinit var _repository: DocTaskRepository

	@Autowired
	@Lazy
	protected fun setRepository(repository: DocTaskRepository) {
		this._repository = repository
	}

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
			aggregate.private = pto.private
			aggregate.priority = pto.priority?.let { CodeTaskPriority.Enumeration.getItem(it) }
			aggregate.dueAt = pto.dueAt
			aggregate.remindAt = pto.remindAt

			// Load comments
			val commentListProperty = aggregate.getProperty("commentList") as? PartListProperty<DocTaskPartComment>
			commentListProperty?.clearParts()
			pto.comments?.forEach { commentPto ->
				val comment = commentListProperty?.addPart(commentPto.id)
				comment?.let { domainComment ->
					domainComment.text = commentPto.text
					(domainComment.getProperty("createdAt") as? BaseProperty<OffsetDateTime?>)?.value = commentPto.createdAt
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
						createdAt =
							domainComment.getProperty("createdAt")?.let { property ->
								if (property is BaseProperty<*> && property.type == OffsetDateTime::class.java) {
									@Suppress("UNCHECKED_CAST")
									(property as BaseProperty<OffsetDateTime?>).value
								} else {
									null
								}
							},
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
