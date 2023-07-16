package io.zeitwert.fm.contact.adapter.api.jsonapi.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiRelationId;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.SerializeType;
import io.dddrive.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto;
import io.zeitwert.fm.contact.adapter.api.jsonapi.impl.ObjContactDtoAdapter;
import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.obj.adapter.api.jsonapi.dto.ObjDtoBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data()
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ToString(callSuper = true, includeFieldNames = true)
@JsonApiResource(type = "contact", resourcePath = "contact/contacts")
public class ObjContactDto extends ObjDtoBase<ObjContact> {

	@Override
	public ObjContactDtoAdapter getAdapter() {
		return (ObjContactDtoAdapter) super.getAdapter();
	}

	@JsonApiRelationId
	private Integer accountId;

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
		this.accountDto = null;
	}

	@JsonIgnore
	private ObjAccountDto accountDto;

	@JsonApiRelation(serialize = SerializeType.LAZY)
	public ObjAccountDto getAccount() {
		if (this.accountDto == null) {
			this.accountDto = this.getAdapter().getAccountDto(this.accountId);
		}
		return this.accountDto;
	}

	public void setAccount(ObjAccountDto account) {
		this.accountDto = account;
		this.accountId = account != null ? account.getId() : null;
	}

	private EnumeratedDto contactRole;
	private EnumeratedDto salutation;
	private EnumeratedDto title;
	private String firstName;
	private String lastName;
	private String description;
	private LocalDate birthDate;
	private String phone;
	private String mobile;
	private String email;

	private List<ObjContactPartAddressDto> mailAddresses;
	private List<ObjContactPartAddressDto> electronicAddresses;

}
