package fm.comunas.fm.contact.model.base;

import fm.comunas.fm.contact.model.ObjContact;
import fm.comunas.fm.contact.model.ObjContactPartAddress;
import fm.comunas.fm.contact.model.enums.CodeAddressType;
import fm.comunas.fm.contact.model.enums.CodeAddressTypeEnum;
import fm.comunas.fm.contact.model.enums.CodeInteractionChannel;
import fm.comunas.fm.contact.model.enums.CodeInteractionChannelEnum;
import fm.comunas.ddd.common.model.enums.CodeCountry;
import fm.comunas.ddd.common.model.enums.CodeCountryEnum;
import fm.comunas.ddd.obj.model.base.ObjPartBase;
import fm.comunas.ddd.property.model.EnumProperty;
import fm.comunas.ddd.property.model.SimpleProperty;

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

	public ObjContactPartAddressBase(ObjContact obj, UpdatableRecord<?> dbRecord) {
		super(obj, dbRecord);
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
