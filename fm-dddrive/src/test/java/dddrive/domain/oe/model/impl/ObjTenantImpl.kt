package dddrive.domain.oe.model.impl

import dddrive.app.ddd.model.SessionContext
import dddrive.app.obj.model.base.ObjBase
import dddrive.ddd.path.setValueByPath
import dddrive.ddd.property.delegate.baseProperty
import dddrive.domain.oe.model.ObjTenant
import dddrive.domain.oe.model.ObjTenantRepository

class ObjTenantImpl(
	override val repository: ObjTenantRepository,
	isNew: Boolean,
) : ObjBase(repository, isNew),
	ObjTenant {

	override var key: String? by baseProperty(this, "key")
	override var name: String? by baseProperty(this, "name")

	override fun doAfterCreate(sessionContext: SessionContext) {
		super.doAfterCreate(sessionContext)
		setValueByPath("tenantId", id)
	}

	override fun doCalcAll() {
		super.doCalcAll()
		calcCaption()
	}

	protected fun calcCaption() {
		setCaption(name)
	}

	// 	@Override
	// 	public void doCalcSearch() {
	// 		super.doCalcSearch();
	// 		addSearchText(getName());
	// 		addSearchText(getDescription());
	// 	}

}
