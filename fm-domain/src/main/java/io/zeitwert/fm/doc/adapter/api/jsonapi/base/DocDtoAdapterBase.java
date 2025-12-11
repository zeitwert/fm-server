package io.zeitwert.fm.doc.adapter.api.jsonapi.base;

import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.AggregateDtoAdapterBase;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.AggregateMetaDto;
import io.dddrive.ddd.model.enums.CodeAggregateTypeEnum;
import io.dddrive.doc.model.Doc;
import io.dddrive.doc.model.DocMeta;
import io.dddrive.doc.model.enums.CodeCaseDefEnum;
import io.dddrive.doc.model.enums.CodeCaseStageEnum;
import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;
import io.zeitwert.fm.doc.adapter.api.jsonapi.dto.DocDtoBase;
import io.zeitwert.fm.doc.adapter.api.jsonapi.dto.DocMetaDto;
import io.zeitwert.fm.doc.adapter.api.jsonapi.dto.DocPartTransitionDto;
import io.zeitwert.fm.doc.adapter.api.jsonapi.dto.DocMetaDto.DocMetaDtoBuilder;
import io.zeitwert.fm.doc.model.base.DocFields;

import org.jooq.TableRecord;

public abstract class DocDtoAdapterBase<A extends Doc, V extends TableRecord<?>, D extends DocDtoBase<A>>
		extends AggregateDtoAdapterBase<A, V, D> {

	@Override
	public void toAggregate(D dto, A doc) {
		if (dto.getCaseDef() != null) {
			doc.setCaseDef(CodeCaseDefEnum.getCaseDef(dto.getCaseDef().getId()));
		}
		if (dto.getCaseStage() != null) {
			doc.setCaseStage(CodeCaseStageEnum.getCaseStage(dto.getCaseStage().getId()));
		}
		if (dto.getOwner() != null) {
			doc.setOwner(this.getUser(Integer.parseInt(dto.getOwner().getId())));
		}
		if (dto.getAssignee() != null) {
			doc.setAssignee(this.getUser(Integer.parseInt(dto.getAssignee().getId())));
		}
	}

	protected void fromAggregate(DocDtoBase.DocDtoBaseBuilder<?, ?, ?> dtoBuilder, A doc) {
		dtoBuilder
				.adapter(this)
				.tenant(EnumeratedDto.fromAggregate(doc.getTenant()))
				.meta(this.metaFromDoc(doc))
				.id(doc.getId())
				.caption(doc.getCaption())
				.owner(EnumeratedDto.fromAggregate(doc.getOwner()))
				.assignee(EnumeratedDto.fromAggregate(doc.getAssignee()));
	}

	private DocMetaDto metaFromDoc(Doc doc) {
		DocMeta meta = doc.getMeta();
		DocMetaDtoBuilder<?, ?> builder = DocMetaDto.builder();
		AggregateMetaDto.fromAggregate(builder, doc);
		return builder
				.caseDef(EnumeratedDto.fromEnum(doc.getMeta().getCaseDef()))
				.caseStage(EnumeratedDto.fromEnum(doc.getMeta().getCaseStage()))
				.isInWork(doc.getMeta().getCaseStage().isInWork())
				.assignee(EnumeratedDto.fromAggregate(doc.getAssignee()))
				.caseStages(doc.getMeta().getCaseStages().stream().map(cs -> EnumeratedDto.fromEnum(cs)).toList())
				.transitions(meta.getTransitionList().stream().map(v -> DocPartTransitionDto.fromPart(v)).toList())
				.build();
	}

	protected void fromRecord(DocDtoBase.DocDtoBaseBuilder<?, ?, ?> dtoBuilder, TableRecord<?> doc) {
		EnumeratedDto tenant = this.getTenantEnumerated(doc.get(DocFields.TENANT_ID));
		EnumeratedDto owner = this.getUserEnumerated(doc.get(DocFields.OWNER_ID));
		EnumeratedDto assignee = this.getUserEnumerated(doc.get(DocFields.ASSIGNEE_ID));
		dtoBuilder
				.adapter(this)
				.tenant(tenant)
				.meta(this.metaFromRecord(doc))
				.id(doc.get(DocFields.ID))
				.caption(doc.get(DocFields.CAPTION))
				.owner(owner)
				.assignee(assignee);
	}

	private DocMetaDto metaFromRecord(TableRecord<?> doc) {
		DocMetaDtoBuilder<?, ?> builder = DocMetaDto.builder();
		return builder
				.itemType(EnumeratedDto.fromEnum(CodeAggregateTypeEnum.getAggregateType(doc.get(DocFields.DOC_TYPE_ID))))
				.owner(this.getUserEnumerated(doc.getValue(DocFields.OWNER_ID)))
				.version(doc.get(DocFields.VERSION))
				.createdByUser(this.getUserEnumerated(doc.getValue(DocFields.CREATED_BY_USER_ID)))
				.createdAt(doc.get(DocFields.CREATED_AT))
				.modifiedByUser(this.getUserEnumerated(doc.getValue(DocFields.MODIFIED_BY_USER_ID)))
				.modifiedAt(doc.get(DocFields.MODIFIED_AT))
				.caseDef(EnumeratedDto.fromEnum(CodeCaseDefEnum.getCaseDef(doc.get(DocFields.CASE_DEF_ID))))
				.caseStage(EnumeratedDto.fromEnum(CodeCaseStageEnum.getCaseStage(doc.get(DocFields.CASE_STAGE_ID))))
				.isInWork(doc.get(DocFields.IS_IN_WORK))
				.assignee(this.getUserEnumerated(doc.getValue(DocFields.ASSIGNEE_ID)))
				.build();
	}

}
