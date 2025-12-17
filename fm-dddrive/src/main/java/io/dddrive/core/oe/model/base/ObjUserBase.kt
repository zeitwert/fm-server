package io.dddrive.core.oe.model.base

import io.dddrive.core.obj.model.ObjRepository
import io.dddrive.core.obj.model.base.ObjBase
import io.dddrive.core.oe.model.ObjUser
import io.dddrive.core.property.model.BaseProperty

abstract class ObjUserBase(
	repository: ObjRepository<ObjUser>,
	isNew: Boolean,
) : ObjBase(repository, isNew),
	ObjUser {

	// @formatter:off
	protected val _email: BaseProperty<String> = this.addBaseProperty<String>("email", String::class.java)
	protected val _password: BaseProperty<String> = this.addBaseProperty<String>("password", String::class.java)
	protected val _name: BaseProperty<String> = this.addBaseProperty<String>("name", String::class.java)
	protected val _description: BaseProperty<String> = this.addBaseProperty<String>("description", String::class.java)
	// 	@Override

	// 	public void doCalcSearch() {
	// 		super.doCalcSearch();
	// 		this.addSearchToken(this.getEmail());
	// 		this.addSearchText(this.getEmail().replace("@", " ").replace(".", " ").replace("_", " ").replace("-", " "));
	// 		this.addSearchText(this.getName());
	// 		this.addSearchText(this.getDescription());
	// 	}

	override fun doCalcAll() {
		super.doCalcAll()
		this.calcCaption()
	}

	protected fun calcCaption() {
		this.setCaption(this.name)
	}

}
