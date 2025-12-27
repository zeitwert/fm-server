package dddrive.domain.doc.persist.mem.base

import dddrive.app.doc.model.Doc
import dddrive.app.doc.model.DocPartTransition
import dddrive.app.doc.model.enums.CodeCaseDefEnum
import dddrive.app.doc.model.enums.CodeCaseStageEnum
import dddrive.ddd.property.model.PartListProperty
import dddrive.domain.ddd.persist.mem.base.MemAggregatePersistenceProviderBase
import dddrive.domain.doc.persist.mem.pto.DocPartTransitionPto
import dddrive.domain.doc.persist.mem.pto.DocPto
import dddrive.path.getValueByPath
import dddrive.path.setValueByPath
import io.dddrive.domain.doc.persist.mem.pto.DocMetaPto

abstract class MemDocPersistenceProviderBase<D : Doc, Pto : DocPto>(
	intfClass: Class<D>,
) : MemAggregatePersistenceProviderBase<D, Pto>(intfClass) {

	@Suppress("UNCHECKED_CAST")
	override fun toAggregate(
		pto: Pto,
		aggregate: D,
	) {
		// First, let the AggregatePersistenceProviderBase handle its properties
		super.toAggregate(pto, aggregate)

		// Now, handle Doc-specific and DocMeta-specific properties
		val docMetaPto = pto.meta // This is DocMetaPto
		aggregate.meta // This is DocMeta from the domain object

		docMetaPto?.caseDefId?.let { caseDefId ->
			val caseDef = CodeCaseDefEnum.getCaseDef(caseDefId)
			aggregate.setValueByPath("caseDef", caseDef)
		}

		docMetaPto?.caseStageId?.let { caseStageId ->
			val caseStage = CodeCaseStageEnum.getCaseStage(caseStageId)
			aggregate.setValueByPath("caseStage", caseStage)
		}

		aggregate.setValueByPath("assigneeId", docMetaPto?.assigneeId)

		// Load transitions
		val transitionListProperty =
			aggregate.getProperty("transitionList", DocPartTransition::class) as? PartListProperty<DocPartTransition>
		transitionListProperty?.clear()
		docMetaPto?.transitions?.forEach { transitionPto ->
			// When adding a part from PTO, if transitionPto.id is null, a new ID will be generated.
			// If transitionPto.id is not null, it implies we are loading an existing part.
			val transition = transitionListProperty?.add(transitionPto.id)
			transition?.let { domainTransition ->
				// Populate properties of the domainTransition from transitionPto
				domainTransition.setValueByPath("tenantId", aggregate.tenantId)
				domainTransition.setValueByPath("userId", transitionPto.userId)
				domainTransition.setValueByPath("timestamp", transitionPto.timestamp)
				transitionPto.oldCaseStageId?.let { stageId ->
					domainTransition.setValueByPath("oldCaseStageId", stageId)
				}
				transitionPto.newCaseStageId?.let { stageId ->
					domainTransition.setValueByPath("newCaseStageId", stageId)
				}
			}
		}
	}

	@Suppress("UNCHECKED_CAST")
	protected fun getMeta(aggregate: D): DocMetaPto {
		val maxPartId = aggregate.getValueByPath("maxPartId") as? Int?
		val transitions =
			aggregate.meta.transitionList
				.map { domainTransition ->
					DocPartTransitionPto(
						id = domainTransition.id,
						userId = domainTransition.user.id,
						timestamp = domainTransition.timestamp,
						oldCaseStageId = domainTransition.oldCaseStage?.id,
						newCaseStageId = domainTransition.newCaseStage.id,
					)
				}.toList()

		val meta = DocMetaPto(
			// Doc specific meta fields
			docTypeId = aggregate.meta.docTypeId,
			caseDefId = aggregate.meta.caseDef?.id,
			caseStageId = aggregate.meta.caseStage?.id,
			isInWork = aggregate.meta.isInWork,
			assigneeId = aggregate.assignee?.id,
			transitions = transitions,
			// Properties inherited from AggregateMetaPto
			maxPartId = maxPartId,
			ownerId = aggregate.owner?.id as? Int,
			version = aggregate.meta.version,
			createdAt = aggregate.meta.createdAt,
			createdByUserId = aggregate.meta.createdByUser?.id as? Int,
			modifiedAt = aggregate.meta.modifiedAt,
			modifiedByUserId = aggregate.meta.modifiedByUser?.id as? Int,
		)
		return meta
	}

}
