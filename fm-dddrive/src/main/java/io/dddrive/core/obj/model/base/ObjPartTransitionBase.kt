package io.dddrive.core.obj.model.base

import io.dddrive.core.ddd.model.PartRepository
import io.dddrive.core.obj.model.Obj
import io.dddrive.core.obj.model.ObjPartTransition
import io.dddrive.core.oe.model.ObjUser
import io.dddrive.core.property.model.Property
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
	override val repository: PartRepository<Obj, ObjPartTransition>
		get() = super.repository as PartRepository<Obj, ObjPartTransition>

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
