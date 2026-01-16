package io.zeitwert.app.doc.api.jsonapi.base

import dddrive.app.doc.model.Doc
import dddrive.app.doc.model.enums.CodeCaseStageEnum
import dddrive.ddd.model.RepositoryDirectory
import dddrive.ddd.model.enums.CodeAggregateTypeEnum
import io.zeitwert.app.api.jsonapi.EnumeratedDto
import io.zeitwert.app.api.jsonapi.base.AggregateDtoAdapterBase
import io.zeitwert.app.api.jsonapi.base.AggregateDtoBase
import io.zeitwert.app.session.model.SessionContext
import org.springframework.beans.factory.annotation.Autowired

abstract class DocDtoAdapterBase<E : Doc, D : DocDtoBase>(
	aggregateClass: Class<E>,
	resourceType: String,
	dtoClass: Class<out AggregateDtoBase>,
	directory: RepositoryDirectory,
	resourceFactory: () -> D,
) : AggregateDtoAdapterBase<E, D>(
	aggregateClass,
	resourceType,
	dtoClass,
	directory,
	resourceFactory,
) {

	@Autowired
	lateinit var sessionContext: SessionContext

	init {
		config.exclude("docTypeId")
		config.meta("itemType", {
			val itemType = CodeAggregateTypeEnum.getAggregateType((it as Doc).meta.docTypeId)
			EnumeratedDto.of(itemType)
		})
		config.field("caseStage")
		config.field("assignee")
		config.meta(
			listOf(
				"caseDef",
				"caseStage",
				"assignee",
				"transitionList",
			),
		)
		config.meta("caseStages", {
			(it as Doc).meta.caseStages.map { cs -> EnumeratedDto.of(cs) }
		})
	}

	@Suppress("UNCHECKED_CAST")
	override fun toAggregate(
		dto: D,
		aggregate: E,
	) {
		if (dto.getAttribute("caseStage") != null) {
			val caseStageId = enumId(dto, "caseStage")!!
			val caseStage = CodeCaseStageEnum.getCaseStage(caseStageId)
			aggregate.meta.setCaseStage(caseStage, sessionContext.userId, sessionContext.currentTime)
		}
		super.toAggregate(dto, aggregate)
	}

}
