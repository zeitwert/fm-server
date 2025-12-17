package io.dddrive.core.oe.model.base

import io.dddrive.core.obj.model.ObjRepository
import io.dddrive.core.obj.model.base.ObjBase
import io.dddrive.core.oe.model.ObjTenant
import io.dddrive.core.property.model.BaseProperty
import java.time.OffsetDateTime

abstract class ObjTenantBase(
	repository: ObjRepository<ObjTenant>,
	isNew: Boolean,
) : ObjBase(repository, isNew),
	ObjTenant {

	// @formatter:off
	protected val _key: BaseProperty<String> = this.addBaseProperty<String>("key", String::class.java)
	protected val _name: BaseProperty<String> = this.addBaseProperty<String>("name", String::class.java)
	protected val _description: BaseProperty<String> = this.addBaseProperty<String>("description", String::class.java)
	// @formatter:on

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
