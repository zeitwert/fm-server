
package io.zeitwert.fm.contact.model;

import io.zeitwert.fm.contact.model.enums.CodeAddressType;
import io.zeitwert.fm.account.model.enums.CodeCountry;
import io.zeitwert.fm.contact.model.enums.CodeAddressChannel;
import io.zeitwert.ddd.obj.model.ObjPart;

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
