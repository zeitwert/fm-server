package io.zeitwert.fm.contact.adapter.api.jsonapi.impl;

import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.impl.ObjAccountDtoAdapter;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.contact.adapter.api.jsonapi.dto.ObjContactDto;
import io.zeitwert.fm.contact.adapter.api.jsonapi.dto.ObjContactPartAddressDto;
import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.contact.model.enums.CodeContactRole;
import io.zeitwert.fm.contact.model.enums.CodeSalutation;
import io.zeitwert.fm.contact.model.enums.CodeTitle;
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoAdapterBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("objContactDtoAdapter")
public class ObjContactDtoAdapter extends ObjDtoAdapterBase<ObjContact, ObjContactDto> {

	private ObjAccountRepository accountRepository = null;
	private ObjAccountDtoAdapter accountDtoAdapter;

	@Autowired
	void setAccountRepository(ObjAccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@Autowired
	void setAccountDtoAdapter(ObjAccountDtoAdapter accountDtoAdapter) {
		this.accountDtoAdapter = accountDtoAdapter;
	}

	public ObjAccountDto getAccountDto(Integer id) {
		return id != null ? this.accountDtoAdapter.fromAggregate(this.accountRepository.get(id)) : null;
	}

	@Override
	public void toAggregate(ObjContactDto dto, ObjContact obj) {
		try {
			obj.getMeta().disableCalc();
			super.toAggregate(dto, obj);

			obj.accountId = dto.getAccountId();
			obj.contactRole = dto.getContactRole() == null ? null : CodeContactRole.getContactRole(dto.getContactRole().getId());
			obj.salutation = dto.getSalutation() == null ? null : CodeSalutation.getSalutation(dto.getSalutation().getId());
			obj.title = dto.getTitle() == null ? null : CodeTitle.getTitle(dto.getTitle().getId());
			obj.firstName = dto.getFirstName();
			obj.lastName = dto.getLastName();
			obj.description = dto.getDescription();
			obj.birthDate = dto.getBirthDate();
			obj.phone = dto.getPhone();
			obj.mobile = dto.getMobile();
			obj.email = dto.getEmail();
			if (dto.getMailAddresses() != null) {
				for (ObjContactPartAddressDto address : dto.getMailAddresses()) {
					if (address.getPartId() != null) {
						address.toPart(obj.getMailAddress(address.getPartId()).get());
					} else {
						address.toPart(obj.addMailAddress());
					}
				}
			}
			if (dto.getElectronicAddresses() != null) {
				for (ObjContactPartAddressDto address : dto.getElectronicAddresses()) {
					if (address.getPartId() != null) {
						address.toPart(obj.getElectronicAddress(address.getPartId()).get());
					} else {
						address.toPart(obj.addElectronicAddress());
					}
				}
			}

		} finally {
			obj.getMeta().enableCalc();
			obj.calcAll();
		}
	}

	@Override
	public ObjContactDto fromAggregate(ObjContact obj) {
		if (obj == null) {
			return null;
		}
		ObjContactDto.ObjContactDtoBuilder<?, ?> dtoBuilder = ObjContactDto.builder();
		this.fromAggregate(dtoBuilder, obj);
		// @formatter:off
		return dtoBuilder
			.accountId((Integer)obj.accountId)
			.contactRole(EnumeratedDto.of(obj.contactRole))
			.salutation(EnumeratedDto.of(obj.salutation))
			.title(EnumeratedDto.of(obj.title))
			.firstName(obj.firstName)
			.lastName(obj.lastName)
			.description(obj.description)
			.birthDate(obj.birthDate)
			.phone(obj.phone)
			.mobile(obj.mobile)
			.email(obj.email)
			.mailAddresses(obj.mailAddressList.stream().map(a -> ObjContactPartAddressDto.fromPart(a)).toList())
			.electronicAddresses(obj.electronicAddressList.stream().map(a -> ObjContactPartAddressDto.fromPart(a)).toList())
			.build();
		// @formatter:on
	}

//	@Override
//	public ObjContactDto fromRecord(ObjContactVRecord obj) {
//		if (obj == null) {
//			return null;
//		}
//		ObjContactDto.ObjContactDtoBuilder<?, ?> dtoBuilder = ObjContactDto.builder();
//		this.fromRecord(dtoBuilder, obj);
//		// @formatter:off
//		return dtoBuilder
//			.accountId(obj.getAccountId())
//			.contactRole(EnumeratedDto.of(CodeContactRoleEnum.getContactRole(obj.getContactRoleId())))
//			.salutation(EnumeratedDto.of(CodeSalutationEnum.getSalutation(obj.getSalutationId())))
//			.title(EnumeratedDto.of(CodeTitleEnum.getTitle(obj.getTitleId())))
//			.firstName(obj.getFirstName())
//			.lastName(obj.getLastName())
//			.description(obj.getDescription())
//			.birthDate(obj.getBirthDate())
//			.phone(obj.getPhone())
//			.mobile(obj.getMobile())
//			.email(obj.getEmail())
//			//.mailAddresses(obj.getMailAddressList().stream().map(a -> ObjContactPartAddressDto.fromPart(a)).toList())
//			//.electronicAddresses(obj.getElectronicAddressList().stream().map(a -> ObjContactPartAddressDto.fromPart(a)).toList())
//			.build();
//		// @formatter:on
//	}

}
