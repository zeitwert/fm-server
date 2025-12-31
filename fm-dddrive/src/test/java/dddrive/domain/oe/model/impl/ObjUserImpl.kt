package dddrive.domain.oe.model.impl

import dddrive.app.obj.model.base.ObjBase
import dddrive.ddd.property.delegate.baseProperty
import dddrive.domain.oe.model.ObjUser
import dddrive.domain.oe.model.ObjUserRepository

class ObjUserImpl(
	override val repository: ObjUserRepository,
	isNew: Boolean,
) : ObjBase(repository, isNew),
	ObjUser {

	override var email by baseProperty<String>("email")
	override var name by baseProperty<String>("name")

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
