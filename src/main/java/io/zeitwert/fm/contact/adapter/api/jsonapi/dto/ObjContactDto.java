package io.zeitwert.fm.contact.adapter.api.jsonapi.dto;

import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiRelationId;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.SerializeType;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto;
import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.contact.model.db.tables.records.ObjContactVRecord;
import io.zeitwert.fm.contact.model.enums.CodeContactRoleEnum;
import io.zeitwert.fm.contact.model.enums.CodeSalutationEnum;
import io.zeitwert.fm.contact.model.enums.CodeTitleEnum;
import io.zeitwert.fm.obj.adapter.api.jsonapi.dto.FMObjDtoBase;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.session.model.SessionInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data()
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ToString(callSuper = true, includeFieldNames = true)
@JsonApiResource(type = "contact", resourcePath = "contact/contacts", deletable = false)
public class ObjContactDto extends FMObjDtoBase<ObjContact> {

	@JsonApiRelationId
	private Integer accountId;

	@JsonIgnore
	private ObjAccountDto accountDto;

	@JsonApiRelation(serialize = SerializeType.LAZY)
	public ObjAccountDto getAccount() {
		if (this.accountDto == null) {
			if (this.getOriginal() != null) {
				this.accountDto = ObjAccountDto.fromObj(this.getOriginal().getAccount(), this.sessionInfo);
			} else if (this.accountId != null) {
			}
		}
		return this.accountDto;
	}

	public void setAccount(ObjAccountDto account) {
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

	public void toObj(ObjContact obj) {
		super.toObj(obj);
		obj.setAccountId(accountId);
		obj.setContactRole(contactRole == null ? null : CodeContactRoleEnum.getContactRole(contactRole.getId()));
		obj.setSalutation(salutation == null ? null : CodeSalutationEnum.getSalutation(salutation.getId()));
		obj.setTitle(title == null ? null : CodeTitleEnum.getTitle(title.getId()));
		obj.setFirstName(firstName);
		obj.setLastName(lastName);
		obj.setDescription(description);
		obj.setBirthDate(birthDate);
		obj.setPhone(phone);
		obj.setMobile(mobile);
		obj.setEmail(email);
		if (mailAddresses != null) {
			for (ObjContactPartAddressDto address : mailAddresses) {
				if (address.getId() != null) {
					address.toPart(obj.getMailAddress(address.getId()).get());
				} else {
					address.toPart(obj.addMailAddress());
				}
			}
		}
		if (electronicAddresses != null) {
			for (ObjContactPartAddressDto address : electronicAddresses) {
				if (address.getId() != null) {
					address.toPart(obj.getElectronicAddress(address.getId()).get());
				} else {
					address.toPart(obj.addElectronicAddress());
				}
			}
		}
	}

	public static ObjContactDto fromObj(ObjContact obj, SessionInfo sessionInfo) {
		if (obj == null) {
			return null;
		}
		ObjContactDtoBuilder<?, ?> dtoBuilder = ObjContactDto.builder().original(obj);
		FMObjDtoBase.fromObj(dtoBuilder, obj, sessionInfo);
		// @formatter:off
		return dtoBuilder
			.accountId(obj.getAccountId())
			.contactRole(EnumeratedDto.fromEnum(obj.getContactRole()))
			.salutation(EnumeratedDto.fromEnum(obj.getSalutation()))
			.title(EnumeratedDto.fromEnum(obj.getTitle()))
			.firstName(obj.getFirstName())
			.lastName(obj.getLastName())
			.description(obj.getDescription())
			.birthDate(obj.getBirthDate())
			.phone(obj.getPhone())
			.mobile(obj.getMobile())
			.email(obj.getEmail())
			.mailAddresses(obj.getMailAddressList().stream().map(a -> ObjContactPartAddressDto.fromPart(a)).toList())
			.electronicAddresses(obj.getElectronicAddressList().stream().map(a -> ObjContactPartAddressDto.fromPart(a)).toList())
			.build();
		// @formatter:on
	}

	public static ObjContactDto fromRecord(ObjContactVRecord obj, SessionInfo sessionInfo) {
		if (obj == null) {
			return null;
		}
		ObjContactDtoBuilder<?, ?> dtoBuilder = ObjContactDto.builder().original(null);
		FMObjDtoBase.fromRecord(dtoBuilder, obj, sessionInfo);
		// @formatter:off
		return dtoBuilder
			.accountId(obj.getAccountId())
			.contactRole(EnumeratedDto.fromEnum(CodeContactRoleEnum.getContactRole(obj.getContactRoleId())))
			.salutation(EnumeratedDto.fromEnum(CodeSalutationEnum.getSalutation(obj.getSalutationId())))
			.title(EnumeratedDto.fromEnum(CodeTitleEnum.getTitle(obj.getTitleId())))
			.firstName(obj.getFirstName())
			.lastName(obj.getLastName())
			.description(obj.getDescription())
			.birthDate(obj.getBirthDate())
			.phone(obj.getPhone())
			.mobile(obj.getMobile())
			.email(obj.getEmail())
			//.mailAddresses(obj.getMailAddressList().stream().map(a -> ObjContactPartAddressDto.fromPart(a)).toList())
			//.electronicAddresses(obj.getElectronicAddressList().stream().map(a -> ObjContactPartAddressDto.fromPart(a)).toList())
			.build();
		// @formatter:on
	}

}
