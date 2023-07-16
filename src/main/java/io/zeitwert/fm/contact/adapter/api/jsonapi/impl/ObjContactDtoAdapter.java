
package io.zeitwert.fm.contact.adapter.api.jsonapi.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.dddrive.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.impl.ObjAccountDtoAdapter;
import io.zeitwert.fm.account.service.api.ObjAccountCache;
import io.zeitwert.fm.contact.adapter.api.jsonapi.dto.ObjContactDto;
import io.zeitwert.fm.contact.adapter.api.jsonapi.dto.ObjContactPartAddressDto;
import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.contact.model.db.tables.records.ObjContactVRecord;
import io.zeitwert.fm.contact.model.enums.CodeContactRoleEnum;
import io.zeitwert.fm.contact.model.enums.CodeSalutationEnum;
import io.zeitwert.fm.contact.model.enums.CodeTitleEnum;
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoAdapterBase;

@Component("objContactDtoAdapter")
public class ObjContactDtoAdapter extends ObjDtoAdapterBase<ObjContact, ObjContactVRecord, ObjContactDto> {

	private ObjAccountCache accountCache = null;
	private ObjAccountDtoAdapter accountDtoAdapter;

	@Autowired
	void setAccountCache(ObjAccountCache accountCache) {
		this.accountCache = accountCache;
	}

	@Autowired
	void setAccountDtoAdapter(ObjAccountDtoAdapter accountDtoAdapter) {
		this.accountDtoAdapter = accountDtoAdapter;
	}

	public ObjAccountDto getAccountDto(Integer id) {
		return id != null ? this.accountDtoAdapter.fromAggregate(this.accountCache.get(id)) : null;
	}

	@Override
	public void toAggregate(ObjContactDto dto, ObjContact obj) {
		try {
			obj.getMeta().disableCalc();
			super.toAggregate(dto, obj);

			obj.setAccountId(dto.getAccountId());
			obj.setContactRole(
					dto.getContactRole() == null ? null : CodeContactRoleEnum.getContactRole(dto.getContactRole().getId()));
			obj.setSalutation(
					dto.getSalutation() == null ? null : CodeSalutationEnum.getSalutation(dto.getSalutation().getId()));
			obj.setTitle(dto.getTitle() == null ? null : CodeTitleEnum.getTitle(dto.getTitle().getId()));
			obj.setFirstName(dto.getFirstName());
			obj.setLastName(dto.getLastName());
			obj.setDescription(dto.getDescription());
			obj.setBirthDate(dto.getBirthDate());
			obj.setPhone(dto.getPhone());
			obj.setMobile(dto.getMobile());
			obj.setEmail(dto.getEmail());
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

	@Override
	public ObjContactDto fromRecord(ObjContactVRecord obj) {
		if (obj == null) {
			return null;
		}
		ObjContactDto.ObjContactDtoBuilder<?, ?> dtoBuilder = ObjContactDto.builder();
		this.fromRecord(dtoBuilder, obj);
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
