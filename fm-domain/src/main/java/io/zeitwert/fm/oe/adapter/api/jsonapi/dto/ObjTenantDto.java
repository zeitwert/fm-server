package io.zeitwert.fm.oe.adapter.api.jsonapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiRelationId;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.SerializeType;
import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;
import io.zeitwert.fm.dms.adapter.api.jsonapi.dto.ObjDocumentDto;
import io.zeitwert.fm.obj.adapter.api.jsonapi.dto.ObjDtoBase;
import io.zeitwert.fm.oe.adapter.api.jsonapi.impl.ObjTenantDtoAdapter;
import io.zeitwert.fm.oe.model.ObjTenant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data()
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ToString(callSuper = true, includeFieldNames = true)
@JsonApiResource(type = "tenant", resourcePath = "oe/tenants")
public class ObjTenantDto extends ObjDtoBase<ObjTenant> {

	private String name;
	private String description;
	private EnumeratedDto tenantType;
	private BigDecimal inflationRate;
	private BigDecimal discountRate;
	@JsonApiRelationId
	private Integer logoId;
	@JsonIgnore
	private ObjDocumentDto logoDto;

	@Override
	public ObjTenantDtoAdapter getAdapter() {
		return (ObjTenantDtoAdapter) super.getAdapter();
	}

	public void setLogoId(Integer logoId) {
		// assertThis(false, "logoId is read-only");
	}

	@JsonApiRelation(serialize = SerializeType.LAZY)
	public ObjDocumentDto getLogo() {
		if (this.logoDto == null) {
			this.logoDto = this.getAdapter().getDocumentDto(this.logoId);
		}
		return this.logoDto;
	}

	public void setLogo(ObjDocumentDto logo) {
		// assertThis(false, "logo is read-only");
	}

}
