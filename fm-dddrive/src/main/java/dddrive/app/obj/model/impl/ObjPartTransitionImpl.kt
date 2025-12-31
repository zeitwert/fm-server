package dddrive.app.obj.model.impl

import dddrive.app.obj.model.Obj
import dddrive.app.obj.model.ObjPartTransition
import dddrive.app.obj.model.base.ObjPartBase
import dddrive.ddd.core.model.PartRepository
import dddrive.ddd.property.delegate.baseProperty
import dddrive.ddd.property.model.Property
import java.time.OffsetDateTime

class ObjPartTransitionImpl(
	obj: Obj,
	override val repository: PartRepository<Obj, ObjPartTransition>,
	property: Property<*>,
	id: Int,
) : ObjPartBase<Obj>(obj, repository, property, id),
	ObjPartTransition {

	private var _tenantId by baseProperty<Any>("tenantId")
	private var _userId by baseProperty<Any>("userId")
	override val userId get() = _userId!!

	private var _timestamp by baseProperty<OffsetDateTime>("timestamp")
	override val timestamp get() = _timestamp!!

	override fun doAfterCreate() {
		super.doAfterCreate()
		_tenantId = aggregate.tenantId
	}

	override fun init(
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		_userId = userId
		_timestamp = timestamp
	}

}
