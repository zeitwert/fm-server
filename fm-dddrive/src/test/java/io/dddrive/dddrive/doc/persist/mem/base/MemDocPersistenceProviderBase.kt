package io.dddrive.dddrive.doc.persist.mem.base

import io.dddrive.core.doc.model.Doc
import io.dddrive.core.doc.model.DocPartTransition
import io.dddrive.core.doc.model.enums.CodeCaseDefEnum
import io.dddrive.core.doc.model.enums.CodeCaseStageEnum
import io.dddrive.core.property.model.PartListProperty
import io.dddrive.dddrive.ddd.persist.mem.base.MemAggregatePersistenceProviderBase
import io.dddrive.dddrive.doc.persist.mem.pto.DocPartTransitionPto
import io.dddrive.dddrive.doc.persist.mem.pto.DocPto
import io.dddrive.domain.doc.persist.mem.pto.DocMetaPto
import io.dddrive.path.getValueByPath
import io.dddrive.path.setValueByPath

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

		// Load Doc-specific properties from pto.meta into the doc aggregate
		aggregate.setValueByPath("docTypeId", docMetaPto?.docTypeId)

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
		transitionListProperty?.clearParts()
		docMetaPto?.transitions?.forEach { transitionPto ->
			// When adding a part from PTO, if transitionPto.id is null, a new ID will be generated.
			// If transitionPto.id is not null, it implies we are loading an existing part.
			val transition = transitionListProperty?.addPart(transitionPto.id)
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
			ownerId = aggregate.owner.id as? Int,
			version = aggregate.meta.version,
			createdAt = aggregate.meta.createdAt,
			createdByUserId = aggregate.meta.createdByUser.id as? Int,
			modifiedAt = aggregate.meta.modifiedAt,
			modifiedByUserId = aggregate.meta.modifiedByUser?.id as? Int,
		)
		return meta
	}

}
