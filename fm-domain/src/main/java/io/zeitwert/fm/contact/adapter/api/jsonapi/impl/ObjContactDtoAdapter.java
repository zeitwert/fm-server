package io.zeitwert.fm.contact.adapter.api.jsonapi.impl;

import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.impl.ObjAccountDtoAdapter;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.contact.adapter.api.jsonapi.dto.ObjContactDto;
import io.zeitwert.fm.contact.adapter.api.jsonapi.dto.ObjContactPartAddressDto;
import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.contact.model.ObjContactPartAddress;
import io.zeitwert.fm.contact.model.enums.CodeContactRole;
import io.zeitwert.fm.contact.model.enums.CodeSalutation;
import io.zeitwert.fm.contact.model.enums.CodeTitle;
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoAdapterBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.StreamSupport;

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

			obj.setAccountId(dto.getAccountId());
			obj.setContactRole(dto.getContactRole() == null ? null : CodeContactRole.getContactRole(dto.getContactRole().getId()));
			obj.setSalutation(dto.getSalutation() == null ? null : CodeSalutation.getSalutation(dto.getSalutation().getId()));
			obj.setTitle(dto.getTitle() == null ? null : CodeTitle.getTitle(dto.getTitle().getId()));
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
						ObjContactPartAddress part = obj.getMailAddressList().getById(address.getPartId());
						if (part != null) {
							address.toPart(part);
						}
					} else {
						address.toPart(obj.getMailAddressList().add((Integer) null));
					}
				}
			}
			if (dto.getElectronicAddresses() != null) {
				for (ObjContactPartAddressDto address : dto.getElectronicAddresses()) {
					if (address.getPartId() != null) {
						ObjContactPartAddress part = obj.getElectronicAddressList().getById(address.getPartId());
						if (part != null) {
							address.toPart(part);
						}
					} else {
						address.toPart(obj.getElectronicAddressList().add((Integer) null));
					}
				}
			}

		} finally {
			obj.getMeta().enableCalc();
			obj.getMeta().calcAll();
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
			.accountId((Integer)obj.getAccountId())
			.contactRole(EnumeratedDto.of(obj.getContactRole()))
			.salutation(EnumeratedDto.of(obj.getSalutation()))
			.title(EnumeratedDto.of(obj.getTitle()))
			.firstName(obj.getFirstName())
			.lastName(obj.getLastName())
			.description(obj.getDescription())
			.birthDate(obj.getBirthDate())
			.phone(obj.getPhone())
			.mobile(obj.getMobile())
			.email(obj.getEmail())
			.mailAddresses(StreamSupport.stream(obj.getMailAddressList().spliterator(), false)
				.map(ObjContactPartAddressDto::fromPart)
				.toList())
			.electronicAddresses(StreamSupport.stream(obj.getElectronicAddressList().spliterator(), false)
				.map(ObjContactPartAddressDto::fromPart)
				.toList())
			.build();
		// @formatter:on
	}

}
