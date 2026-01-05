package io.zeitwert.fm.doc.adapter.api.jsonapi.base

import dddrive.app.doc.model.Doc
import dddrive.app.doc.model.enums.CodeCaseStageEnum
import dddrive.ddd.core.model.RepositoryDirectory
import dddrive.ddd.core.model.enums.CodeAggregateTypeEnum
import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.AggregateDtoAdapterBase
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.EnumeratedDto
import org.springframework.beans.factory.annotation.Autowired

abstract class DocDtoAdapterBase<E : Doc, D : DocDtoBase<E>>(
	directory: RepositoryDirectory,
	resourceFactory: () -> D,
) : AggregateDtoAdapterBase<E, D>(
		directory,
		resourceFactory,
		{
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
		},
	) {

	@Autowired
	lateinit var sessionContext: SessionContext

	@Suppress("UNCHECKED_CAST")
	override fun toAggregate(
		dto: D,
		aggregate: E,
	) {
		if (dto["caseStage"] != null) {
			val caseStageId = dto.enumId("caseStage")!!
			val caseStage = CodeCaseStageEnum.getCaseStage(caseStageId)
			aggregate.meta.setCaseStage(caseStage, sessionContext.userId, sessionContext.currentTime)
		}
		if (dto.hasAttribute("owner")) {
			aggregate.ownerId = userRepository.idFromString(dto.enumId("owner"))
		}
		if (dto["assignee"] != null) {
			aggregate.assigneeId = userRepository.idFromString(dto.enumId("assignee"))
		}
		super.toAggregate(dto, aggregate)
	}

}
