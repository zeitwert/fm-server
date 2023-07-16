package io.zeitwert.fm.oe.adapter.api.jsonapi.dto;

import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiRelationId;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.SerializeType;
import io.dddrive.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.fm.dms.adapter.api.jsonapi.dto.ObjDocumentDto;
import io.zeitwert.fm.obj.adapter.api.jsonapi.dto.ObjDtoBase;
import io.zeitwert.fm.oe.adapter.api.jsonapi.impl.ObjTenantDtoAdapter;
import io.zeitwert.fm.oe.model.ObjTenantFM;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data()
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ToString(callSuper = true, includeFieldNames = true)
@JsonApiResource(type = "tenant", resourcePath = "oe/tenants")
public class ObjTenantDto extends ObjDtoBase<ObjTenantFM> {

	@Override
	public ObjTenantDtoAdapter getAdapter() {
		return (ObjTenantDtoAdapter) super.getAdapter();
	}

	private String name;
	private String description;
	private EnumeratedDto tenantType;
	private BigDecimal inflationRate;

	@JsonApiRelationId
	private Integer logoId;

	public void setLogoId(Integer logoId) {
		// assertThis(false, "logoId is read-only");
	}

	@JsonIgnore
	private ObjDocumentDto logoDto;

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
