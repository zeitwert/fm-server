
package io.zeitwert.fm.contact.model;

import io.zeitwert.fm.contact.model.enums.CodeAddressType;
import io.zeitwert.fm.contact.model.enums.CodeAddressChannel;
import io.dddrive.obj.model.ObjPart;
import io.zeitwert.fm.oe.model.enums.CodeCountry;

public interface ObjContactPartAddress extends ObjPart<ObjContact> {

	CodeAddressType getAddressType();

	Boolean getIsMailAddress();

	CodeAddressChannel getAddressChannel();

	void setAddressChannel(CodeAddressChannel addressChannel);

	String getName();

	void setName(String name);

	String getStreet();

	void setStreet(String street);

	String getZip();

	void setZip(String zip);

	String getCity();

	void setCity(String city);

	CodeCountry getCountry();

	void setCountry(CodeCountry country);

}
