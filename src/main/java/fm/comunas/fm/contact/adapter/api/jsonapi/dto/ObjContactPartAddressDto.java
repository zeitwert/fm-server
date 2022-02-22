
package fm.comunas.fm.contact.adapter.api.jsonapi.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import fm.comunas.fm.contact.model.ObjContact;
import fm.comunas.fm.contact.model.ObjContactPartAddress;
import fm.comunas.fm.contact.model.enums.CodeInteractionChannelEnum;
import fm.comunas.ddd.common.model.enums.CodeCountryEnum;
import fm.comunas.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import fm.comunas.ddd.obj.adapter.api.jsonapi.dto.ObjPartDtoBase;

@Data()
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ToString(callSuper = true, includeFieldNames = true)
public class ObjContactPartAddressDto extends ObjPartDtoBase<ObjContact, ObjContactPartAddress> {

	private String name;
	private String street;
	private String zip;
	private String city;
	private String state;
	private EnumeratedDto country;
	private EnumeratedDto channel;
	private Boolean isFavorite;
	private Boolean isMailAddress;

	public void toPart(ObjContactPartAddress part) {
		super.toPart(part);
		part.setName(name);
		part.setStreet(street);
		part.setZip(zip);
		part.setCity(city);
		part.setState(state);
		part.setCountry(country == null ? null : CodeCountryEnum.getCountry(country.getId()));
		part.setChannel(channel == null ? null : CodeInteractionChannelEnum.getInteractionChannel(channel.getId()));
		part.setIsFavorite(isFavorite);
		part.setIsMailAddress(isMailAddress);
	}

	public static ObjContactPartAddressDto fromPart(ObjContactPartAddress part) {
		if (part == null) {
			return null;
		}
		ObjContactPartAddressDtoBuilder<?, ?> dtoBuilder = ObjContactPartAddressDto.builder();
		ObjPartDtoBase.fromPart(dtoBuilder, part);
		// @formatter:off
		return dtoBuilder
			.name(part.getName())
			.street(part.getStreet())
			.zip(part.getZip())
			.city(part.getCity())
			.state(part.getState())
			.country(EnumeratedDto.fromEnum(part.getCountry()))
			.channel(EnumeratedDto.fromEnum(part.getChannel()))
			.isFavorite(part.getIsFavorite())
			.isMailAddress(part.getIsMailAddress())
			.build();
		// @formatter:on
	}

}
