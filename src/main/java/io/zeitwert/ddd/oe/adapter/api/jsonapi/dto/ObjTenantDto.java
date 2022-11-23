package io.zeitwert.ddd.oe.adapter.api.jsonapi.dto;

import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiRelationId;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.SerializeType;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.obj.adapter.api.jsonapi.dto.ObjDtoBase;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.impl.ObjUserDtoAdapter;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.impl.ObjAccountDtoAdapter;
import io.zeitwert.fm.dms.adapter.api.jsonapi.dto.ObjDocumentDto;
import io.zeitwert.fm.dms.adapter.api.jsonapi.impl.ObjDocumentDtoAdapter;
import io.zeitwert.fm.dms.model.ObjDocument;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data()
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ToString(callSuper = true, includeFieldNames = true)
@JsonApiResource(type = "tenant", resourcePath = "oe/tenants")
public class ObjTenantDto extends ObjDtoBase<ObjTenant> {

	private String name;
	private String description;
	private String extlKey;
	private EnumeratedDto tenantType;
	private BigDecimal inflationRate;

	@JsonIgnore
	private List<ObjUserDto> usersDto;

	@JsonApiRelation(serialize = SerializeType.LAZY)
	public List<? extends ObjUserDto> getUsers() {
		if (this.usersDto == null) {
			if (this.getOriginal() != null) {
				this.usersDto = this.getOriginal().getUsers().stream()
						.map(c -> ObjUserDtoAdapter.getInstance().fromAggregate(c)).toList();
			} else {
			}
		}
		return this.usersDto;
	}

	@JsonIgnore
	private List<ObjAccountDto> accountsDto;

	@JsonApiRelation(serialize = SerializeType.LAZY)
	public List<? extends ObjAccountDto> getAccounts() {
		if (this.accountsDto == null) {
			if (this.getOriginal() != null) {
				this.accountsDto = this.getOriginal().getAccounts().stream()
						.map(c -> ObjAccountDtoAdapter.getInstance().fromAggregate(c)).toList();
			} else {
			}
		}
		return this.accountsDto;
	}

	@JsonApiRelationId
	private Integer logoId;

	@JsonIgnore
	private ObjDocumentDto logoDto;

	@JsonApiRelation(serialize = SerializeType.LAZY)
	public ObjDocumentDto getLogo() {
		if (this.logoDto == null) {
			ObjDocument logo = null;
			if (this.getOriginal() != null) {
				logo = this.getOriginal().getLogoImage();
			} else if (this.logoId != null) {
				logo = getRepository(ObjDocument.class).get(this.logoId);
			}
			this.logoDto = ObjDocumentDtoAdapter.getInstance().fromAggregate(logo);
		}
		return this.logoDto;
	}

	public void setLogo(ObjDocumentDto logo) {
	}

	@JsonApiRelationId
	private Integer bannerId;

	@JsonIgnore
	private ObjDocumentDto bannerDto;

	@JsonApiRelation(serialize = SerializeType.LAZY)
	public ObjDocumentDto getBanner() {
		if (this.bannerDto == null) {
			ObjDocument banner = null;
			if (this.getOriginal() != null) {
				banner = this.getOriginal().getBannerImage();
			} else if (this.bannerId != null) {
				banner = getRepository(ObjDocument.class).get(this.bannerId);
			}
			this.bannerDto = ObjDocumentDtoAdapter.getInstance().fromAggregate(banner);
		}
		return this.bannerDto;
	}

	public void setBanner(ObjDocumentDto banner) {
	}

}
