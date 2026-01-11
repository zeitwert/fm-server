package dddrive.domain.oe.model.impl

import dddrive.app.ddd.model.SessionContext
import dddrive.app.obj.model.base.ObjBase
import dddrive.property.path.setValueByPath
import dddrive.property.delegate.baseProperty
import dddrive.domain.oe.model.ObjTenant
import dddrive.domain.oe.model.ObjTenantRepository

class ObjTenantImpl(
	override val repository: ObjTenantRepository,
	isNew: Boolean,
) : ObjBase(repository, isNew),
	ObjTenant {

	override var key by baseProperty<String>("key")
	override var name by baseProperty<String>("name")

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
