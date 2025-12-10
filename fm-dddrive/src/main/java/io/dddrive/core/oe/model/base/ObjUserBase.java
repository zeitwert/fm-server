package io.dddrive.core.oe.model.base;

import io.dddrive.core.obj.model.ObjRepository;
import io.dddrive.core.obj.model.base.ObjBase;
import io.dddrive.core.oe.model.ObjUser;
import io.dddrive.core.property.model.BaseProperty;

public abstract class ObjUserBase extends ObjBase implements ObjUser {

	//@formatter:off
	protected final BaseProperty<String> email = this.addBaseProperty("email", String.class);
	protected final BaseProperty<String> password = this.addBaseProperty("password", String.class);
	protected final BaseProperty<String> name = this.addBaseProperty("name", String.class);
	protected final BaseProperty<String> description = this.addBaseProperty("description", String.class);
	//@formatter:on

	public ObjUserBase(ObjRepository<ObjUser> repository) {
		super(repository);
	}

//	@Override
//	public void doCalcSearch() {
//		super.doCalcSearch();
//		this.addSearchToken(this.getEmail());
//		this.addSearchText(this.getEmail().replace("@", " ").replace(".", " ").replace("_", " ").replace("-", " "));
//		this.addSearchText(this.getName());
//		this.addSearchText(this.getDescription());
//	}

	@Override
	protected void doCalcAll() {
		super.doCalcAll();
		this.calcCaption();
	}

	protected void calcCaption() {
		this.setCaption(this.getName());
	}

}
