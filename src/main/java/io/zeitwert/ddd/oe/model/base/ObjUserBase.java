
package io.zeitwert.ddd.oe.model.base;

import io.zeitwert.ddd.obj.model.ObjRepository;
import io.zeitwert.ddd.obj.model.base.ObjExtnBase;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.property.model.SimpleProperty;

public abstract class ObjUserBase extends ObjExtnBase implements ObjUser {

	//@formatter:off
	protected final SimpleProperty<String> email = this.addSimpleProperty("email", String.class);
	protected final SimpleProperty<String> name = this.addSimpleProperty("name", String.class);
	protected final SimpleProperty<String> description = this.addSimpleProperty("description", String.class);
	//@formatter:on

	public ObjUserBase(ObjRepository<?, ?> repository, Object state) {
		super(repository, state);
	}

	@Override
	public void doCalcSearch() {
		this.addSearchToken(this.getEmail());
		this.addSearchText(this.getEmail().replace("@", " ").replace(".", " ").replace("_", " ").replace("-", " "));
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
