package io.dddrive.obj.model.base

import io.dddrive.ddd.model.PartRepository
import io.dddrive.obj.model.Obj
import io.dddrive.obj.model.ObjPartTransition
import io.dddrive.oe.model.ObjUser
import io.dddrive.property.model.Property
import java.time.OffsetDateTime

abstract class ObjPartTransitionBase(
	obj: Obj,
	repository: PartRepository<Obj, ObjPartTransition>,
	property: Property<*>,
	id: Int,
) : ObjPartBase<Obj>(obj, repository, property, id),
	ObjPartTransition {

	protected val _tenantId = this.addBaseProperty("tenantId", Any::class.java)
	protected val _user = this.addReferenceProperty("user", ObjUser::class.java)
	protected val _timestamp = this.addBaseProperty("timestamp", OffsetDateTime::class.java)

	@Suppress("UNCHECKED_CAST")
	override val repository get() = super.repository as PartRepository<Obj, ObjPartTransition>

	override fun doAfterCreate() {
		this._tenantId.value = this.aggregate.tenantId
	}

	override fun init(
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		_user.id = userId
		_timestamp.value = timestamp
	}

}
