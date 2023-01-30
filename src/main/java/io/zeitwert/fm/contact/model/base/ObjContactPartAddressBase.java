package io.zeitwert.fm.contact.model.base;

import io.zeitwert.fm.account.model.enums.CodeCountry;
import io.zeitwert.fm.account.model.enums.CodeCountryEnum;
import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.contact.model.ObjContactPartAddress;
import io.zeitwert.fm.contact.model.enums.CodeAddressChannel;
import io.zeitwert.fm.contact.model.enums.CodeAddressChannelEnum;
import io.zeitwert.ddd.obj.model.base.ObjPartBase;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;

import org.jooq.UpdatableRecord;

public abstract class ObjContactPartAddressBase extends ObjPartBase<ObjContact> implements ObjContactPartAddress {

	protected final EnumProperty<CodeAddressChannel> addressChannel;
	protected final SimpleProperty<String> name;
	protected final SimpleProperty<String> street;
	protected final SimpleProperty<String> zip;
	protected final SimpleProperty<String> city;
	protected final EnumProperty<CodeCountry> country;

	public ObjContactPartAddressBase(PartRepository<ObjContact, ?> repository, ObjContact obj,
			UpdatableRecord<?> dbRecord) {
		super(repository, obj, dbRecord);
		this.addressChannel = this.addEnumProperty(dbRecord, ObjContactPartAddressFields.ADDRESS_CHANNEL_ID,
				CodeAddressChannelEnum.class);
		this.name = this.addSimpleProperty(dbRecord, ObjContactPartAddressFields.NAME);
		this.street = this.addSimpleProperty(dbRecord, ObjContactPartAddressFields.STREET);
		this.zip = this.addSimpleProperty(dbRecord, ObjContactPartAddressFields.ZIP);
		this.city = this.addSimpleProperty(dbRecord, ObjContactPartAddressFields.CITY);
		this.country = this.addEnumProperty(dbRecord, ObjContactPartAddressFields.COUNTRY_ID, CodeCountryEnum.class);
	}

}
