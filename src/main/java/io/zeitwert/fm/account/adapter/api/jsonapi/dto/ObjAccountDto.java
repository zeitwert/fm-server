
package io.zeitwert.fm.account.adapter.api.jsonapi.dto;

import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiRelationId;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.SerializeType;
import io.zeitwert.fm.contact.adapter.api.jsonapi.dto.ObjContactDto;
import io.zeitwert.fm.contact.adapter.api.jsonapi.impl.ObjContactDtoAdapter;
import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.oe.adapter.api.jsonapi.dto.ObjTenantDto;
import io.zeitwert.fm.oe.adapter.api.jsonapi.impl.ObjTenantDtoAdapter;
import io.zeitwert.fm.oe.model.ObjTenantFM;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data()
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ToString(callSuper = true, includeFieldNames = true)
@JsonApiResource(type = "account", resourcePath = "account/accounts")
public class ObjAccountDto extends ObjAccountLoginDto {

	@JsonApiRelationId
	private Integer tenantInfoId;

	@Override
	public void setLogoId(Integer tenantId) {
		// assertThis(false, "tenantInfoId is read-only");
	}

	@JsonIgnore
	private ObjTenantDto tenantInfoDto;

	@JsonApiRelation(serialize = SerializeType.LAZY)
	public ObjTenantDto getTenantInfo() {
		if (this.tenantInfoDto == null) {
			ObjTenantFM tenant = null;
			if (this.getOriginal() != null) {
				tenant = (ObjTenantFM) this.getOriginal().getTenant();
			} else if (this.tenantInfoId != null) {
				tenant = this.getCache(ObjTenantFM.class).get(this.tenantInfoId);
			}
			this.tenantInfoDto = this.getAdapter(ObjTenantDtoAdapter.class).fromAggregate(tenant);
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
			ObjContact contact = null;
			if (this.getOriginal() != null) {
				contact = this.getOriginal().getMainContact();
			} else if (this.mainContactId != null) {
				contact = getCache(ObjContact.class).get(this.mainContactId);
			}
			this.mainContactDto = this.getAdapter(ObjContactDtoAdapter.class).fromAggregate(contact);
		}
		return this.mainContactDto;
	}

	public void setMainContact(ObjContactDto contact) {
		this.mainContactDto = contact;
		this.mainContactId = contact != null ? contact.getId() : null;
	}

	@JsonIgnore
	private List<ObjContactDto> contactsDto;

	@JsonApiRelation(serialize = SerializeType.LAZY)
	public List<? extends ObjContactDto> getContacts() {
		if (this.contactsDto == null) {
			if (this.getOriginal() != null) {
				ObjContactDtoAdapter contactAdapter = this.getAdapter(ObjContactDtoAdapter.class);
				this.contactsDto = this.getOriginal().getContacts().stream().map(c -> contactAdapter.fromAggregate(c)).toList();
			} else {
			}
		}
		return this.contactsDto;
	}

}
