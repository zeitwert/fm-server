package io.zeitwert.fm.contact.model.base;

import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.contact.model.ObjContactPartAddress;
import io.zeitwert.fm.contact.model.enums.CodeAddressType;
import io.zeitwert.fm.contact.model.enums.CodeAddressTypeEnum;
import io.zeitwert.fm.contact.model.enums.CodeInteractionChannel;
import io.zeitwert.fm.contact.model.enums.CodeInteractionChannelEnum;
import io.zeitwert.ddd.common.model.enums.CodeCountry;
import io.zeitwert.ddd.common.model.enums.CodeCountryEnum;
import io.zeitwert.ddd.obj.model.base.ObjPartBase;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;

import org.jooq.UpdatableRecord;

public abstract class ObjContactPartAddressBase extends ObjPartBase<ObjContact> implements ObjContactPartAddress {

	protected final EnumProperty<CodeAddressType> addressType;
	protected final SimpleProperty<String> name;
	protected final SimpleProperty<String> street;
	protected final SimpleProperty<String> zip;
	protected final SimpleProperty<String> city;
	protected final SimpleProperty<String> state;
	protected final EnumProperty<CodeCountry> country;
	protected final EnumProperty<CodeInteractionChannel> channel;
	protected final SimpleProperty<Boolean> isFavorite;
	protected final SimpleProperty<Boolean> isMailAddress;

	public ObjContactPartAddressBase(PartRepository<ObjContact, ?> repository, ObjContact obj,
			UpdatableRecord<?> dbRecord) {
		super(repository, obj, dbRecord);
		this.addressType = this.addEnumProperty(dbRecord, ObjContactPartAddressFields.ADDRESS_TYPE_ID,
				CodeAddressTypeEnum.class);
		this.name = this.addSimpleProperty(dbRecord, ObjContactPartAddressFields.NAME);
		this.street = this.addSimpleProperty(dbRecord, ObjContactPartAddressFields.STREET);
		this.zip = this.addSimpleProperty(dbRecord, ObjContactPartAddressFields.ZIP);
		this.city = this.addSimpleProperty(dbRecord, ObjContactPartAddressFields.CITY);
		this.state = this.addSimpleProperty(dbRecord, ObjContactPartAddressFields.STATE);
		this.country = this.addEnumProperty(dbRecord, ObjContactPartAddressFields.COUNTRY_ID, CodeCountryEnum.class);
		this.channel = this.addEnumProperty(dbRecord, ObjContactPartAddressFields.CHANNEL_ID,
				CodeInteractionChannelEnum.class);
		this.isFavorite = this.addSimpleProperty(dbRecord, ObjContactPartAddressFields.IS_FAVORITE);
		this.isMailAddress = this.addSimpleProperty(dbRecord, ObjContactPartAddressFields.IS_MAIL_ADDRESS);
	}

}
