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
import io.zeitwert.ddd.session.model.RequestContext;

import org.jooq.TableRecord;

public abstract class DocDtoAdapter<A extends Doc, V extends TableRecord<?>, D extends DocDtoBase<A>>
		extends AggregateDtoAdapter<A, V, D> {

	@Override
	public void toAggregate(D dto, A doc, RequestContext requestCtx) {
		if (dto.getOwner() != null) {
			doc.setOwner(getUserRepository().get(requestCtx, Integer.parseInt(dto.getOwner().getId())));
		}
	}

	protected void fromAggregate(DocDtoBase.DocDtoBaseBuilder<?, ?, ?> dtoBuilder, A doc, RequestContext requestCtx) {
		ObjTenantDtoAdapter tenantDtoAdapter = ObjTenantDtoAdapter.getInstance();
		ObjUserDtoAdapter userDtoAdapter = ObjUserDtoAdapter.getInstance();
		// @formatter:off
		dtoBuilder
			.requestCtx(requestCtx)
			.tenant(tenantDtoAdapter.asEnumerated(doc.getTenant(), requestCtx))
			.meta(DocMetaDto.fromDoc(doc, requestCtx))
			.id(doc.getId())
			.caption(doc.getCaption())
			.owner(userDtoAdapter.asEnumerated(doc.getOwner(), requestCtx));
		// @formatter:on
	}

	protected void fromRecord(DocDtoBase.DocDtoBaseBuilder<?, ?, ?> dtoBuilder, TableRecord<?> doc,
			RequestContext requestCtx) {
		ObjTenantDtoAdapter tenantDtoAdapter = ObjTenantDtoAdapter.getInstance();
		ObjTenant tenant = getTenantRepository().get(requestCtx, doc.get(DocFields.TENANT_ID));
		ObjUserDtoAdapter userDtoAdapter = ObjUserDtoAdapter.getInstance();
		ObjUser owner = getUserRepository().get(requestCtx, doc.get(DocFields.OWNER_ID));
		// @formatter:off
		dtoBuilder
			.requestCtx(requestCtx)
			.tenant(tenantDtoAdapter.asEnumerated(tenant, requestCtx))
			.meta(DocMetaDto.fromRecord(doc, requestCtx))
			.id(doc.get(DocFields.ID))
			.caption(doc.get(DocFields.CAPTION))
			.owner(userDtoAdapter.asEnumerated(owner, requestCtx));
		// @formatter:on
	}

}
