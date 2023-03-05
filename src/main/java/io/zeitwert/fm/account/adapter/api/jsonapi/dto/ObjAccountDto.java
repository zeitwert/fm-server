
package io.zeitwert.fm.account.adapter.api.jsonapi.dto;

import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiRelationId;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.SerializeType;
import io.dddrive.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.impl.ObjAccountDtoAdapter;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.contact.adapter.api.jsonapi.dto.ObjContactDto;
import io.zeitwert.fm.dms.adapter.api.jsonapi.dto.ObjDocumentDto;
import io.zeitwert.fm.obj.adapter.api.jsonapi.dto.ObjDtoBase;
import io.zeitwert.fm.oe.adapter.api.jsonapi.dto.ObjTenantDto;
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
@JsonApiResource(type = "account", resourcePath = "account/accounts")
public class ObjAccountDto extends ObjDtoBase<ObjAccount> {

	@Override
	public ObjAccountDtoAdapter getAdapter() {
		return (ObjAccountDtoAdapter) super.getAdapter();
	}

	private String name;
	private String description;
	private EnumeratedDto accountType;
	private EnumeratedDto clientSegment;
	private EnumeratedDto referenceCurrency;
	private BigDecimal inflationRate;

	@JsonApiRelationId
	private Integer tenantInfoId;

	@JsonIgnore
	private ObjTenantDto tenantInfoDto;

	@JsonApiRelation(serialize = SerializeType.LAZY)
	public ObjTenantDto getTenantInfo() {
		if (this.tenantInfoDto == null) {
			this.tenantInfoDto = this.getAdapter().getTenantDto(this.tenantInfoId);
		}
		return this.tenantInfoDto;
	}

	public void setTenantInfo(ObjTenantDto tenant) {
		// assertThis(false, "tenantInfo is read-only");
	}

	@JsonApiRelationId
	private Integer mainContactId;

	public void setMainContactId(Integer contactId) {
		this.mainContactId = contactId;
		this.mainContactDto = null;
	}

	@JsonIgnore
	private ObjContactDto mainContactDto;

	@JsonApiRelation(serialize = SerializeType.LAZY)
	public ObjContactDto getMainContact() {
		if (this.mainContactDto == null) {
			this.mainContactDto = this.getAdapter().getContactDto(this.mainContactId);
		}
		return this.mainContactDto;
	}

	public void setMainContact(ObjContactDto contact) {
		this.mainContactDto = contact;
		this.mainContactId = contact != null ? contact.getId() : null;
	}

	@JsonIgnore
	private List<Integer> contactIdList;

	@JsonIgnore
	private List<ObjContactDto> contactsDto;

	@JsonApiRelation(serialize = SerializeType.LAZY)
	public List<? extends ObjContactDto> getContacts() {
		if (this.contactsDto == null) {
			this.contactsDto = this.contactIdList != null
					? this.contactIdList.stream().map(id -> this.getAdapter().getContactDto(id)).toList()
					: List.of();
		}
		return this.contactsDto;
	}

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
