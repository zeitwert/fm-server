
package io.zeitwert.fm.account.adapter.api.jsonapi.dto;

import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiRelationId;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.SerializeType;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.contact.adapter.api.jsonapi.dto.ObjContactDto;
import io.zeitwert.fm.contact.adapter.api.jsonapi.impl.ObjContactDtoAdapter;
import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.dms.adapter.api.jsonapi.dto.ObjDocumentDto;
import io.zeitwert.fm.dms.adapter.api.jsonapi.impl.ObjDocumentDtoAdapter;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.obj.adapter.api.jsonapi.dto.FMObjDtoBase;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data()
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ToString(callSuper = true, includeFieldNames = true)
@JsonApiResource(type = "account", resourcePath = "account/accounts")
public class ObjAccountDto extends FMObjDtoBase<ObjAccount> {

	private String key;
	private String name;
	private String description;
	private EnumeratedDto accountType;
	private EnumeratedDto clientSegment;
	private EnumeratedDto referenceCurrency;
	private Set<EnumeratedDto> areas;

	@JsonApiRelationId
	private Integer mainContactId;

	@JsonIgnore
	private ObjContactDto mainContactDto;

	@JsonApiRelation(serialize = SerializeType.LAZY)
	public ObjContactDto getMainContact() {
		if (this.mainContactDto == null) {
			ObjContact contact = null;
			if (this.getOriginal() != null) {
				contact = this.getOriginal().getMainContact();
			} else if (this.mainContactId != null) {
				contact = getRepository(ObjContact.class).get(this.mainContactId);
			}
			this.mainContactDto = ObjContactDtoAdapter.getInstance().fromAggregate(contact);
		}
		return this.mainContactDto;
	}

	// Crnk needs to see this to set mainContractId
	public void setMainContact(ObjContactDto mainContact) {
	}

	@JsonIgnore
	private List<ObjContactDto> contactsDto;

	@JsonApiRelation(serialize = SerializeType.LAZY)
	public List<? extends ObjContactDto> getContacts() {
		if (this.contactsDto == null) {
			if (this.getOriginal() != null) {
				this.contactsDto = this.getOriginal().getContacts().stream()
						.map(c -> ObjContactDtoAdapter.getInstance().fromAggregate(c)).toList();
			} else {
			}
		}
		return this.contactsDto;
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
