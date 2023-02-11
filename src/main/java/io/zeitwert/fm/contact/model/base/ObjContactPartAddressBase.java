package io.zeitwert.fm.contact.model.base;

import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.contact.model.ObjContactPartAddress;
import io.zeitwert.fm.contact.model.enums.CodeAddressChannel;
import io.zeitwert.ddd.obj.model.base.ObjPartBase;
import io.zeitwert.ddd.oe.model.enums.CodeCountry;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;

public abstract class ObjContactPartAddressBase extends ObjPartBase<ObjContact> implements ObjContactPartAddress {

	protected final EnumProperty<CodeAddressChannel> addressChannel = this.addEnumProperty("addressChannel",
			CodeAddressChannel.class);
	protected final SimpleProperty<String> name = this.addSimpleProperty("name", String.class);
	protected final SimpleProperty<String> street = this.addSimpleProperty("street", String.class);
	protected final SimpleProperty<String> zip = this.addSimpleProperty("zip", String.class);
	protected final SimpleProperty<String> city = this.addSimpleProperty("city", String.class);
	protected final EnumProperty<CodeCountry> country = this.addEnumProperty("country", CodeCountry.class);

	public ObjContactPartAddressBase(PartRepository<ObjContact, ?> repository, ObjContact obj, Object state) {
		super(repository, obj, state);
	}

}
