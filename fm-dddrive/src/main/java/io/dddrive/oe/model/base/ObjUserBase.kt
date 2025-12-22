package io.dddrive.oe.model.base

import io.dddrive.obj.model.ObjRepository
import io.dddrive.obj.model.base.ObjBase
import io.dddrive.oe.model.ObjUser

abstract class ObjUserBase(
	override val repository: ObjRepository<ObjUser>,
	isNew: Boolean,
) : ObjBase(repository, isNew),
	ObjUser {

	override fun doInit() {
		super.doInit()
		this.addBaseProperty("email", String::class.java)
		this.addBaseProperty("password", String::class.java)
		this.addBaseProperty("name", String::class.java)
		this.addBaseProperty("description", String::class.java)
	}

	override fun doCalcAll() {
		super.doCalcAll()
		this.calcCaption()
	}

	protected fun calcCaption() {
		this.setCaption(this.name)
	}

	// 	public void doCalcSearch() {
	// 		super.doCalcSearch();
	// 		this.addSearchToken(this.getEmail());
	// 		this.addSearchText(this.getEmail().replace("@", " ").replace(".", " ").replace("_", " ").replace("-", " "));
	// 		this.addSearchText(this.getName());
	// 		this.addSearchText(this.getDescription());
	// 	}

}
