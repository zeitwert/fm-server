
package io.dddrive.oe.model.base;

import io.dddrive.obj.model.ObjRepository;
import io.dddrive.obj.model.base.ObjExtnBase;
import io.dddrive.oe.model.ObjTenant;
import io.dddrive.oe.model.enums.CodeTenantType;
import io.dddrive.property.model.EnumProperty;
import io.dddrive.property.model.SimpleProperty;

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
