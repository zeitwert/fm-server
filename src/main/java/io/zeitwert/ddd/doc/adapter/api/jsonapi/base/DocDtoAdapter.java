package io.zeitwert.ddd.doc.adapter.api.jsonapi.base;

import io.zeitwert.ddd.aggregate.adapter.api.jsonapi.dto.AggregateDtoAdapter;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.doc.adapter.api.jsonapi.dto.DocDtoBase;
import io.zeitwert.ddd.doc.adapter.api.jsonapi.dto.DocMetaDto;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.base.DocFields;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStageEnum;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.oe.service.api.ObjUserCache;
import io.zeitwert.fm.oe.adapter.api.jsonapi.impl.ObjTenantDtoAdapter;
import io.zeitwert.fm.oe.adapter.api.jsonapi.impl.ObjUserDtoAdapter;

import org.jooq.TableRecord;

public abstract class DocDtoAdapter<A extends Doc, V extends TableRecord<?>, D extends DocDtoBase<A>>
		extends AggregateDtoAdapter<A, V, D> {

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
		ObjTenantDtoAdapter tenantDtoAdapter = ObjTenantDtoAdapter.getInstance();
		ObjUserDtoAdapter userDtoAdapter = ObjUserDtoAdapter.getInstance();
		// @formatter:off
		dtoBuilder
			.tenant(tenantDtoAdapter.asEnumerated(doc.getTenant()))
			.meta(DocMetaDto.fromDoc(doc))
			.id(doc.getId())
			.caption(doc.getCaption())
			.owner(userDtoAdapter.asEnumerated(doc.getOwner()))
			.assignee(ObjUserDtoAdapter.getInstance().asEnumerated(doc.getAssignee()));
		// @formatter:on
	}

	protected void fromRecord(DocDtoBase.DocDtoBaseBuilder<?, ?, ?> dtoBuilder, TableRecord<?> doc) {
		ObjUserCache userCache = (ObjUserCache) AppContext.getInstance().getBean(ObjUserCache.class);
		EnumeratedDto tenant = this.getTenantEnumerated(doc.get(DocFields.TENANT_ID));
		EnumeratedDto owner = this.getUserEnumerated(doc.get(DocFields.OWNER_ID));
		// @formatter:off
		dtoBuilder
			.tenant(tenant)
			.meta(DocMetaDto.fromRecord(doc))
			.id(doc.get(DocFields.ID))
			.caption(doc.get(DocFields.CAPTION))
			.owner(owner)
			.assignee(userCache.getAsEnumerated(doc.getValue(DocFields.ASSIGNEE_ID)));
		// @formatter:on
	}

}
