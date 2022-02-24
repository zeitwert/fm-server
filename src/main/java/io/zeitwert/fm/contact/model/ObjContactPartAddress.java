
package io.zeitwert.fm.contact.model;

import io.zeitwert.fm.contact.model.enums.CodeAddressType;
import io.zeitwert.fm.contact.model.enums.CodeInteractionChannel;
import io.zeitwert.ddd.common.model.enums.CodeCountry;
import io.zeitwert.ddd.obj.model.ObjPart;

public interface ObjContactPartAddress extends ObjPart<ObjContact> {

	CodeAddressType getAddressType();

	void setAddressType(CodeAddressType addressType);

	String getName();

	void setName(String name);

	String getStreet();

	void setStreet(String street);

	String getZip();

	void setZip(String zip);

	String getCity();

	void setCity(String city);

	String getState();

	void setState(String state);

	CodeCountry getCountry();

	void setCountry(CodeCountry country);

	CodeInteractionChannel getChannel();

	void setChannel(CodeInteractionChannel channel);

	Boolean getIsFavorite();

	void setIsFavorite(Boolean favorite);

	Boolean getIsMailAddress();

	void setIsMailAddress(Boolean isMailAddress);

}
