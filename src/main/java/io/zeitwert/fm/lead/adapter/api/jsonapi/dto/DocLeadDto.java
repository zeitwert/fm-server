
package io.zeitwert.fm.lead.adapter.api.jsonapi.dto;

import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiRelationId;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.SerializeType;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.impl.ObjAccountDtoAdapter;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.contact.adapter.api.jsonapi.dto.ObjContactDto;
import io.zeitwert.fm.contact.adapter.api.jsonapi.impl.ObjContactDtoAdapter;
import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.doc.adapter.api.jsonapi.dto.FMDocDtoBase;
import io.zeitwert.fm.lead.model.DocLead;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data()
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ToString(callSuper = true, includeFieldNames = true)
@JsonApiResource(type = "lead", resourcePath = "lead/leads", deletable = false)
public class DocLeadDto extends FMDocDtoBase<DocLead> {

	@JsonApiRelationId
	private Integer accountId;

	@JsonIgnore
	private ObjAccountDto accountDto;

	@JsonApiRelation(serialize = SerializeType.LAZY)
	public ObjAccountDto getAccount() {
		if (this.accountDto == null) {
			ObjAccount account = null;
			if (this.getOriginal() != null) {
				account = this.getOriginal().getAccount();
			} else if (this.accountId != null) {
				account = getRepository(ObjAccount.class).get(this.accountId);
			}
			this.accountDto = ObjAccountDtoAdapter.getInstance().fromAggregate(account);
		}
		return this.accountDto;
	}

	// Crnk needs to see this to set accountId
	public void setAccount(ObjAccountDto account) {
	}

	@JsonApiRelationId
	private Integer contactId;

	@JsonIgnore
	private ObjContactDto contactDto;

	@JsonApiRelation(serialize = SerializeType.LAZY)
	public ObjContactDto getContact() {
		if (this.contactDto == null) {
			ObjContact contact = null;
			if (this.getOriginal() != null) {
				contact = this.getOriginal().getContact();
			} else if (this.contactId != null) {
				contact = getRepository(ObjContact.class).get(this.contactId);
			}
			this.contactDto = ObjContactDtoAdapter.getInstance().fromAggregate(contact);
		}
		return this.contactDto;
	}

	// Crnk needs to see this to set contactId
	public void setContact(ObjContactDto contact) {
	}

	private String subject;
	private String description;
	private Set<EnumeratedDto> areas;
	private EnumeratedDto leadSource;
	private EnumeratedDto leadRating;
	private EnumeratedDto salutation;
	private EnumeratedDto title;
	private String firstName;
	private String lastName;
	private String phone;
	private String mobile;
	private String email;
	private String street;
	private String zip;
	private String city;
	private String state;
	private EnumeratedDto country;

}
