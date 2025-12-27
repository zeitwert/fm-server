package io.dddrive.oe.model.base

import dddrive.app.obj.model.ObjRepository
import dddrive.app.obj.model.base.ObjBase
import dddrive.ddd.property.delegate.baseProperty
import io.dddrive.oe.model.ObjUser

abstract class ObjUserBase(
	override val repository: ObjRepository<ObjUser>,
	isNew: Boolean,
) : ObjBase(repository, isNew),
	ObjUser {

	override var email: String? by baseProperty(this, "email")
	override var password: String? by baseProperty(this, "password")
	override var name: String? by baseProperty(this, "name")
	override var description: String? by baseProperty(this, "description")

	override fun doCalcAll() {
		super.doCalcAll()
		calcCaption()
	}

	protected fun calcCaption() {
		setCaption(name)
	}

	// 	public void doCalcSearch() {
	// 		super.doCalcSearch();
	// 		this.addSearchToken(this.getEmail());
	// 		this.addSearchText(this.getEmail().replace("@", " ").replace(".", " ").replace("_", "
	// ").replace("-", " "));
	// 		this.addSearchText(this.getName());
	// 		this.addSearchText(this.getDescription());
	// 	}

}
