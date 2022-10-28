package io.zeitwert.ddd.doc.adapter.api.jsonapi.base;

import io.zeitwert.ddd.aggregate.adapter.api.jsonapi.dto.AggregateDtoAdapter;
import io.zeitwert.ddd.doc.adapter.api.jsonapi.dto.DocDtoBase;
import io.zeitwert.ddd.doc.adapter.api.jsonapi.dto.DocMetaDto;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.base.DocFields;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.impl.ObjTenantDtoAdapter;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.impl.ObjUserDtoAdapter;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.session.model.SessionInfo;

import org.jooq.TableRecord;

public abstract class DocDtoAdapter<A extends Doc, V extends TableRecord<?>, D extends DocDtoBase<A>>
		extends AggregateDtoAdapter<A, V, D> {

	@Override
	public void toAggregate(D dto, A doc, SessionInfo sessionInfo) {
		if (dto.getOwner() != null) {
			doc.setOwner(getUserRepository().get(sessionInfo, Integer.parseInt(dto.getOwner().getId())));
		}
	}

	protected void fromAggregate(DocDtoBase.DocDtoBaseBuilder<?, ?, ?> dtoBuilder, A doc, SessionInfo sessionInfo) {
		ObjTenantDtoAdapter tenantDtoAdapter = ObjTenantDtoAdapter.getInstance();
		ObjUserDtoAdapter userDtoAdapter = ObjUserDtoAdapter.getInstance();
		// @formatter:off
		dtoBuilder
			.sessionInfo(sessionInfo)
			.tenant(tenantDtoAdapter.asEnumerated(doc.getTenant(), sessionInfo))
			.meta(DocMetaDto.fromDoc(doc, sessionInfo))
			.id(doc.getId())
			.caption(doc.getCaption())
			.owner(userDtoAdapter.asEnumerated(doc.getOwner(), sessionInfo));
		// @formatter:on
	}

	protected void fromRecord(DocDtoBase.DocDtoBaseBuilder<?, ?, ?> dtoBuilder, TableRecord<?> doc,
			SessionInfo sessionInfo) {
		ObjTenantDtoAdapter tenantDtoAdapter = ObjTenantDtoAdapter.getInstance();
		ObjTenant tenant = getTenantRepository().get(sessionInfo, doc.get(DocFields.TENANT_ID));
		ObjUserDtoAdapter userDtoAdapter = ObjUserDtoAdapter.getInstance();
		ObjUser owner = getUserRepository().get(sessionInfo, doc.get(DocFields.OWNER_ID));
		// @formatter:off
		dtoBuilder
			.sessionInfo(sessionInfo)
			.tenant(tenantDtoAdapter.asEnumerated(tenant, sessionInfo))
			.meta(DocMetaDto.fromRecord(doc, sessionInfo))
			.id(doc.get(DocFields.ID))
			.caption(doc.get(DocFields.CAPTION))
			.owner(userDtoAdapter.asEnumerated(owner, sessionInfo));
		// @formatter:on
	}

}
