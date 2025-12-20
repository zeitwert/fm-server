package io.zeitwert.fm.contact.adapter.api.jsonapi.dto;

import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;
import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.contact.model.ObjContactPartAddress;
import io.zeitwert.fm.contact.model.enums.CodeAddressChannel;
import io.zeitwert.fm.obj.adapter.api.jsonapi.dto.ObjPartDtoBase;
import io.zeitwert.fm.oe.model.enums.CodeCountry;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data()
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ToString(callSuper = true, includeFieldNames = true)
public class ObjContactPartAddressDto extends ObjPartDtoBase<ObjContact, ObjContactPartAddress> {

	private EnumeratedDto addressChannel;
	private Boolean isMailAddress;
	private String name;
	private String street;
	private String zip;
	private String city;
	private EnumeratedDto country;

	public static ObjContactPartAddressDto fromPart(ObjContactPartAddress part) {
		if (part == null) {
			return null;
		}
		ObjContactPartAddressDtoBuilder<?, ?> dtoBuilder = ObjContactPartAddressDto.builder();
		ObjPartDtoBase.fromPart(dtoBuilder, part);
		// @formatter:off
		return dtoBuilder
			.addressChannel(EnumeratedDto.of(part.getAddressChannel()))
			.isMailAddress(part.isMailAddress())
			.name(part.getName())
			.street(part.getStreet())
			.zip(part.getZip())
			.city(part.getCity())
			.country(EnumeratedDto.of(part.getCountry()))
			.build();
		// @formatter:on
	}

	public void toPart(ObjContactPartAddress part) {
		super.toPart(part);
		part.setAddressChannel(addressChannel == null ? null : CodeAddressChannel.getAddressChannel(addressChannel.getId()));
		part.setName(name);
		part.setStreet(street);
		part.setZip(zip);
		part.setCity(city);
		part.setCountry(country == null ? null : CodeCountry.getCountry(country.getId()));
	}

}
