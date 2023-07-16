
package io.zeitwert.fm.contact.adapter.api.jsonapi.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.contact.model.ObjContactPartAddress;
import io.zeitwert.fm.contact.model.enums.CodeAddressChannelEnum;
import io.zeitwert.fm.obj.adapter.api.jsonapi.dto.ObjPartDtoBase;
import io.dddrive.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.fm.oe.model.enums.CodeCountryEnum;

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

	public void toPart(ObjContactPartAddress part) {
		super.toPart(part);
		part.setAddressChannel(
				addressChannel == null ? null : CodeAddressChannelEnum.getAddressChannel(addressChannel.getId()));
		part.setName(name);
		part.setStreet(street);
		part.setZip(zip);
		part.setCity(city);
		part.setCountry(country == null ? null : CodeCountryEnum.getCountry(country.getId()));
	}

	public static ObjContactPartAddressDto fromPart(ObjContactPartAddress part) {
		if (part == null) {
			return null;
		}
		ObjContactPartAddressDtoBuilder<?, ?> dtoBuilder = ObjContactPartAddressDto.builder();
		ObjPartDtoBase.fromPart(dtoBuilder, part);
		// @formatter:off
		return dtoBuilder
			.addressChannel(EnumeratedDto.fromEnum(part.getAddressChannel()))
			.isMailAddress(part.getIsMailAddress())
			.name(part.getName())
			.street(part.getStreet())
			.zip(part.getZip())
			.city(part.getCity())
			.country(EnumeratedDto.fromEnum(part.getCountry()))
			.build();
		// @formatter:on
	}

}
