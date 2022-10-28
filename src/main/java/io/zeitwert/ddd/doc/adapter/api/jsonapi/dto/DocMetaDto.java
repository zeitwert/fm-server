package io.zeitwert.ddd.doc.adapter.api.jsonapi.dto;

import java.util.List;

import org.jooq.Record;

import io.zeitwert.ddd.aggregate.adapter.api.jsonapi.dto.AggregateMetaDto;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateTypeEnum;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocMeta;
import io.zeitwert.ddd.doc.model.base.DocFields;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStageEnum;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.impl.ObjUserDtoAdapter;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.session.model.RequestContext;
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
	private List<EnumeratedDto> caseStages; // TODO implement
	private List<String> availableActions; // TODO implement
	private List<DocPartTransitionDto> transitionList;

	public static DocMetaDto fromDoc(Doc doc, RequestContext requestCtx) {
		DocMeta meta = doc.getMeta();
		DocMetaDtoBuilder<?, ?> builder = DocMetaDto.builder();
		AggregateMetaDto.fromAggregate(builder, doc, requestCtx);
		// @formatter:off
		return builder
			.caseStage(EnumeratedDto.fromEnum(doc.getCaseStage()))
			.transitionList(meta.getTransitionList().stream().map(v -> DocPartTransitionDto.fromPart(v, requestCtx)).toList())
			.build();
		// @formatter:on
	}

	public static DocMetaDto fromRecord(Record doc, RequestContext requestCtx) {
		DocMetaDtoBuilder<?, ?> builder = DocMetaDto.builder();
		AggregateMetaDto.fromRecord(builder, doc, requestCtx);
		ObjUserRepository userRepo = (ObjUserRepository) AppContext.getInstance().getRepository(ObjUser.class);
		ObjUserDtoAdapter userDtoAdapter = ObjUserDtoAdapter.getInstance();
		Integer modifiedByUserId = doc.getValue(DocFields.MODIFIED_BY_USER_ID);
		EnumeratedDto modifiedByUser = modifiedByUserId == null ? null
				: userDtoAdapter.asEnumerated(userRepo.get(requestCtx, modifiedByUserId), requestCtx);
		// @formatter:off
		return builder
			.itemType(EnumeratedDto.fromEnum(CodeAggregateTypeEnum.getAggregateType(doc.get(DocFields.DOC_TYPE_ID))))
			.owner(userDtoAdapter.asEnumerated(userRepo.get(requestCtx, doc.getValue(DocFields.OWNER_ID)), requestCtx))
			.createdByUser(userDtoAdapter.asEnumerated(userRepo.get(requestCtx, doc.getValue(DocFields.CREATED_BY_USER_ID)), requestCtx))
			.createdAt(doc.get(DocFields.CREATED_AT))
			.modifiedByUser(modifiedByUser)
			.modifiedAt(doc.get(DocFields.MODIFIED_AT))
			.caseStage(EnumeratedDto.fromEnum(CodeCaseStageEnum.getCaseStage(doc.get(DocFields.CASE_STAGE_ID))))
			.build();
		// @formatter:on
	}

}
