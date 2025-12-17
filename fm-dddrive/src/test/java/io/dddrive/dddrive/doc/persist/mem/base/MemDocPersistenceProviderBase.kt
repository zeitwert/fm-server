package io.dddrive.dddrive.doc.persist.mem.base

import io.dddrive.core.doc.model.Doc
import io.dddrive.core.doc.model.DocPartTransition
import io.dddrive.core.doc.model.enums.CodeCaseDef
import io.dddrive.core.doc.model.enums.CodeCaseDefEnum
import io.dddrive.core.doc.model.enums.CodeCaseStage
import io.dddrive.core.doc.model.enums.CodeCaseStageEnum
import io.dddrive.core.oe.model.ObjUser
import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.EnumProperty
import io.dddrive.core.property.model.PartListProperty
import io.dddrive.core.property.model.ReferenceProperty
import io.dddrive.dddrive.ddd.persist.mem.base.MemAggregatePersistenceProviderBase
import io.dddrive.dddrive.doc.persist.mem.pto.DocPartTransitionPto
import io.dddrive.dddrive.doc.persist.mem.pto.DocPto
import io.dddrive.domain.doc.persist.mem.pto.DocMetaPto // Ensure correct import for DocMetaPto

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
		(aggregate.getProperty("docTypeId") as? BaseProperty<String?>)?.value = docMetaPto?.docTypeId

		docMetaPto?.caseDefId?.let { caseDefId ->
			val caseDef = CodeCaseDefEnum.getCaseDef(caseDefId)
			(aggregate.getProperty("caseDef") as? EnumProperty<CodeCaseDef>)?.value = caseDef
		}

		docMetaPto?.caseStageId?.let { caseStageId ->
			val caseStage = CodeCaseStageEnum.getCaseStage(caseStageId)
			(aggregate.getProperty("caseStage") as? EnumProperty<CodeCaseStage>)?.value = caseStage
		}

		(aggregate.getProperty("isInWork") as? BaseProperty<Boolean?>)?.value = docMetaPto?.isInWork
		(aggregate.getProperty("assignee") as? ReferenceProperty<ObjUser>)?.id = docMetaPto?.assigneeId

		// Load transitions
		val transitionListProperty = aggregate.getProperty("transitionList") as? PartListProperty<DocPartTransition>
		transitionListProperty?.clearParts()
		docMetaPto?.transitions?.forEach { transitionPto ->
			// When adding a part from PTO, if transitionPto.id is null, a new ID will be generated.
			// If transitionPto.id is not null, it implies we are loading an existing part.
			val transition = transitionListProperty?.addPart(transitionPto.id)
			transition?.let { domainTransition ->
				// Populate properties of the domainTransition from transitionPto
				(domainTransition.getProperty("tenantId") as? BaseProperty<Any?>)?.value =
					aggregate.tenantId // tenantId is from Aggregate
				(domainTransition.getProperty("user") as? ReferenceProperty<ObjUser>)?.id = transitionPto.userId
				(domainTransition.getProperty("timestamp") as? BaseProperty<java.time.OffsetDateTime?>)?.value =
					transitionPto.timestamp
				transitionPto.oldCaseStageId?.let { stageId ->
					(domainTransition.getProperty("oldCaseStage") as? EnumProperty<CodeCaseStage>)?.value =
						CodeCaseStageEnum.getCaseStage(stageId)
				}
				transitionPto.newCaseStageId?.let { stageId ->
					(domainTransition.getProperty("newCaseStage") as? EnumProperty<CodeCaseStage>)?.value =
						CodeCaseStageEnum.getCaseStage(stageId)
				}
			}
		}
	}

	@Suppress("UNCHECKED_CAST")
	protected fun getMeta(aggregate: D): DocMetaPto {
		val maxPartId = (aggregate.getProperty("maxPartId") as? BaseProperty<Int?>)?.value

		val transitions =
			aggregate.meta.transitionList
				.map { domainTransition ->
					DocPartTransitionPto(
						id = domainTransition.id,
						userId = domainTransition.user?.id,
						timestamp = domainTransition.timestamp,
						oldCaseStageId = domainTransition.oldCaseStage?.id,
						newCaseStageId = domainTransition.newCaseStage?.id,
					)
				}.toList()

		return DocMetaPto(
			// Doc specific meta fields
			docTypeId = aggregate.meta.repository
				?.aggregateType
				?.id,
			caseDefId = aggregate.meta.caseDef?.id,
			caseStageId = aggregate.meta.caseStage?.id,
			isInWork = aggregate.meta.isInWork,
			assigneeId = (aggregate.getProperty("assignee") as? ReferenceProperty<ObjUser>)?.id,
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
	}
}
