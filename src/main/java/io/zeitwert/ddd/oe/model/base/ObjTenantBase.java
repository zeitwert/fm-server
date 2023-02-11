
package io.zeitwert.ddd.oe.model.base;

import io.zeitwert.ddd.obj.model.ObjRepository;
import io.zeitwert.ddd.obj.model.base.ObjExtnBase;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.enums.CodeTenantType;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;

public abstract class ObjTenantBase extends ObjExtnBase implements ObjTenant {

	//@formatter:off
	protected final EnumProperty<CodeTenantType> tenantType = this.addEnumProperty("tenantType", CodeTenantType.class);
	protected final SimpleProperty<String> name = this.addSimpleProperty("name", String.class);
	protected final SimpleProperty<String> description = this.addSimpleProperty("description", String.class);
	//@formatter:on

	public ObjTenantBase(ObjRepository<?, ?> repository, Object state) {
		super(repository, state);
	}

	@Override
	public void doCalcSearch() {
		this.addSearchText(this.getName());
		this.addSearchText(this.getDescription());
	}

	@Override
	protected void doCalcAll() {
		super.doCalcAll();
		this.calcCaption();
	}

	protected void calcCaption() {
		this.setCaption(this.getName());
	}

}
