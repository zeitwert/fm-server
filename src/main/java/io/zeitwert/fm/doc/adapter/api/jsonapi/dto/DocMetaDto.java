package io.zeitwert.fm.doc.adapter.api.jsonapi.dto;

import java.util.List;

import org.jooq.TableRecord;

import io.zeitwert.ddd.aggregate.adapter.api.jsonapi.dto.AggregateMetaDto;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateTypeEnum;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocMeta;
import io.zeitwert.ddd.doc.model.base.DocFields;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStageEnum;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.oe.service.api.ObjUserCache;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class DocMetaDto extends AggregateMetaDto {

	private EnumeratedDto caseStage;
	private boolean isInWork;
	private EnumeratedDto assignee;

	private List<EnumeratedDto> caseStages;
	private List<String> availableActions; // TODO implement
	private List<DocPartTransitionDto> transitions;

	public static DocMetaDto fromDoc(Doc doc) {
		DocMeta meta = doc.getMeta();
		DocMetaDtoBuilder<?, ?> builder = DocMetaDto.builder();
		AggregateMetaDto.fromAggregate(builder, doc);
		// @formatter:off
		return builder
			.caseStage(EnumeratedDto.fromEnum(doc.getCaseStage()))
			.isInWork(doc.getCaseStage().isInWork())
			.assignee(EnumeratedDto.fromAggregate(doc.getAssignee()))
			.caseStages(doc.getMeta().getCaseStages().stream().map(cs -> EnumeratedDto.fromEnum(cs)).toList())
			.transitions(meta.getTransitionList().stream().map(v -> DocPartTransitionDto.fromPart(v)).toList())
			.build();
		// @formatter:on
	}

	public static DocMetaDto fromRecord(TableRecord<?> doc) {
		DocMetaDtoBuilder<?, ?> builder = DocMetaDto.builder();
		AggregateMetaDto.fromRecord(builder, doc);
		ObjUserCache userCache = (ObjUserCache) AppContext.getInstance().getBean(ObjUserCache.class);
		Integer modifiedByUserId = doc.getValue(DocFields.MODIFIED_BY_USER_ID);
		EnumeratedDto modifiedByUser = modifiedByUserId == null ? null : userCache.getAsEnumerated(modifiedByUserId);
		// @formatter:off
		return builder
			.itemType(EnumeratedDto.fromEnum(CodeAggregateTypeEnum.getAggregateType(doc.get(DocFields.DOC_TYPE_ID))))
			.owner(userCache.getAsEnumerated(doc.getValue(DocFields.OWNER_ID)))
			.version(doc.get(DocFields.VERSION))
			.createdByUser(userCache.getAsEnumerated(doc.getValue(DocFields.CREATED_BY_USER_ID)))
			.createdAt(doc.get(DocFields.CREATED_AT))
			.modifiedByUser(modifiedByUser)
			.modifiedAt(doc.get(DocFields.MODIFIED_AT))
			.caseStage(EnumeratedDto.fromEnum(CodeCaseStageEnum.getCaseStage(doc.get(DocFields.CASE_STAGE_ID))))
			.isInWork(doc.get(DocFields.IS_IN_WORK))
			.assignee(userCache.getAsEnumerated(doc.getValue(DocFields.ASSIGNEE_ID)))
			.build();
		// @formatter:on
	}

}
