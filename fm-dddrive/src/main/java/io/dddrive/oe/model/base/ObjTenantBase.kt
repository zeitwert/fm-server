package io.dddrive.oe.model.base

import io.dddrive.obj.model.ObjRepository
import io.dddrive.obj.model.base.ObjBase
import io.dddrive.oe.model.ObjTenant
import io.dddrive.path.setValueByPath
import java.time.OffsetDateTime

abstract class ObjTenantBase(
	override val repository: ObjRepository<ObjTenant>,
	isNew: Boolean,
) : ObjBase(repository, isNew),
	ObjTenant {

	override fun doInit() {
		super.doInit()
		addBaseProperty<String>("key", String::class.java)
		addBaseProperty<String>("name", String::class.java)
		addBaseProperty<String>("description", String::class.java)
	}

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
