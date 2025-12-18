package io.dddrive.core.oe.model.base

import io.dddrive.core.obj.model.ObjRepository
import io.dddrive.core.obj.model.base.ObjBase
import io.dddrive.core.oe.model.ObjUser

abstract class ObjUserBase(
	repository: ObjRepository<ObjUser>,
	isNew: Boolean,
) : ObjBase(repository, isNew),
	ObjUser {

	protected val _email = this.addBaseProperty("email", String::class.java)
	protected val _password = this.addBaseProperty("password", String::class.java)
	protected val _name = this.addBaseProperty("name", String::class.java)
	protected val _description = this.addBaseProperty("description", String::class.java)

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
