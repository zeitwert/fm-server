package io.zeitwert.fm.doc.adapter.api.jsonapi.base

import dddrive.app.doc.model.Doc
import dddrive.app.doc.model.enums.CodeCaseStageEnum
import dddrive.ddd.core.model.RepositoryDirectory
import dddrive.ddd.core.model.enums.CodeAggregateTypeEnum
import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.GenericAggregateDtoAdapterBase
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.EnumeratedDto
import org.springframework.beans.factory.annotation.Autowired

abstract class GenericDocDtoAdapterBase<E : Doc, D : GenericDocDtoBase<E>>(
	directory: RepositoryDirectory,
	resourceFactory: () -> D,
) : GenericAggregateDtoAdapterBase<E, D>(directory, resourceFactory) {

	@Autowired
	lateinit var sessionContext: SessionContext

	init {
		exclude("docTypeId")
		meta("itemType", {
			val itemType = CodeAggregateTypeEnum.getAggregateType((it as Doc).meta.docTypeId)
			EnumeratedDto.of(itemType)
		})
		field("caseStage", "caseStage")
		field("assignee", "assignee")
		meta(
			listOf(
				"caseDef",
				"caseStage",
				"assignee",
				"transitionList",
			),
		)
		meta("caseStages", {
			(it as Doc).meta.caseStages.map { cs -> EnumeratedDto.of(cs) }
		})
		relationship("tenantInfoId", "tenant", "tenant")
		relationship("accountId", "account", "account")
	}

	@Suppress("UNCHECKED_CAST")
	override fun toAggregate(
		dto: D,
		aggregate: E,
	) {
		if (dto["caseStage"] != null) {
			val caseStageId = (dto["caseStage"] as Map<String, Any?>)["id"] as String
			val caseStage = CodeCaseStageEnum.getCaseStage(caseStageId)
			aggregate.meta.setCaseStage(caseStage, sessionContext.userId, sessionContext.currentTime)
		}
		if (dto.hasAttribute("owner")) {
			aggregate.ownerId = userRepository.idFromString((dto["owner"] as Map<String, Any?>)["id"] as String)
		}
		if (dto["assignee"] != null) {
			aggregate.assigneeId = userRepository.idFromString((dto["assignee"] as Map<String, Any?>)["id"] as String)
		}
		super.toAggregate(dto, aggregate)
	}

}
