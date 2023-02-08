package io.zeitwert.fm.doc.adapter.api.jsonapi.base;

import io.zeitwert.ddd.aggregate.adapter.api.jsonapi.dto.AggregateDtoAdapterBase;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStageEnum;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.fm.doc.adapter.api.jsonapi.dto.DocDtoBase;
import io.zeitwert.fm.doc.adapter.api.jsonapi.dto.DocMetaDto;
import io.zeitwert.jooq.property.DocFields;

import org.jooq.TableRecord;

public abstract class DocDtoAdapterBase<A extends Doc, V extends TableRecord<?>, D extends DocDtoBase<A>>
		extends AggregateDtoAdapterBase<A, V, D> {

	@Override
	public void toAggregate(D dto, A doc) {
		if (dto.getOwner() != null) {
			doc.setOwner(this.getUser(Integer.parseInt(dto.getOwner().getId())));
		}
		if (dto.getAssignee() != null) {
			doc.setAssignee(this.getUser(Integer.parseInt(dto.getAssignee().getId())));
		}
		if (dto.getNextCaseStage() != null) {
			doc.setCaseStage(CodeCaseStageEnum.getCaseStage(dto.getNextCaseStage().getId()));
		}
	}

	protected void fromAggregate(DocDtoBase.DocDtoBaseBuilder<?, ?, ?> dtoBuilder, A doc) {
		dtoBuilder
				.tenant(EnumeratedDto.fromAggregate(doc.getTenant()))
				.meta(DocMetaDto.fromDoc(doc))
				.id(doc.getId())
				.caption(doc.getCaption())
				.owner(EnumeratedDto.fromAggregate(doc.getOwner()))
				.assignee(EnumeratedDto.fromAggregate(doc.getAssignee()));
	}

	protected void fromRecord(DocDtoBase.DocDtoBaseBuilder<?, ?, ?> dtoBuilder, TableRecord<?> doc) {
		EnumeratedDto tenant = this.getTenantEnumerated(doc.get(DocFields.TENANT_ID));
		EnumeratedDto owner = this.getUserEnumerated(doc.get(DocFields.OWNER_ID));
		EnumeratedDto assignee = this.getUserEnumerated(doc.get(DocFields.ASSIGNEE_ID));
		dtoBuilder
				.tenant(tenant)
				.meta(DocMetaDto.fromRecord(doc))
				.id(doc.get(DocFields.ID))
				.caption(doc.get(DocFields.CAPTION))
				.owner(owner)
				.assignee(assignee);
	}

}
