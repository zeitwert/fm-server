package io.dddrive.core.oe.model.base

import io.dddrive.core.obj.model.ObjRepository
import io.dddrive.core.obj.model.base.ObjBase
import io.dddrive.core.oe.model.ObjTenant
import java.time.OffsetDateTime

abstract class ObjTenantBase(
	repository: ObjRepository<ObjTenant>,
	isNew: Boolean,
) : ObjBase(repository, isNew),
	ObjTenant {

	protected val _key = this.addBaseProperty<String>("key", String::class.java)
	protected val _name = this.addBaseProperty<String>("name", String::class.java)
	protected val _description = this.addBaseProperty<String>("description", String::class.java)

	// 	@Override
	// 	public void doCalcSearch() {
	// 		super.doCalcSearch();
	// 		this.addSearchText(this.getName());
	// 		this.addSearchText(this.getDescription());
	// 	}

	override fun doAfterCreate(
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		super.doAfterCreate(userId, timestamp)
		this._tenant.id = this.id
	}

	override fun doCalcAll() {
		super.doCalcAll()
		this.calcCaption()
	}

	protected fun calcCaption() {
		this.setCaption(this.name)
	}

}
