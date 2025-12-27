package dddrive.domain.oe.model.impl

import dddrive.app.obj.model.base.ObjBase
import dddrive.ddd.path.setValueByPath
import dddrive.ddd.property.delegate.baseProperty
import dddrive.domain.oe.model.ObjTenant
import dddrive.domain.oe.model.ObjTenantRepository
import java.time.OffsetDateTime

class ObjTenantImpl(
	override val repository: ObjTenantRepository,
	isNew: Boolean,
) : ObjBase(repository, isNew),
	ObjTenant {

	override var key: String? by baseProperty(this, "key")
	override var name: String? by baseProperty(this, "name")

	override fun doAfterCreate(
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		super.doAfterCreate(userId, timestamp)
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
