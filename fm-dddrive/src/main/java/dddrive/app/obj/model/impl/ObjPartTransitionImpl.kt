package dddrive.app.obj.model.impl

import dddrive.app.obj.model.Obj
import dddrive.app.obj.model.ObjPartTransition
import dddrive.app.obj.model.base.ObjPartBase
import dddrive.ddd.model.PartRepository
import dddrive.property.delegate.baseProperty
import dddrive.property.delegate.referenceIdProperty
import java.time.OffsetDateTime

class ObjPartTransitionImpl(
	obj: Obj,
	override val repository: PartRepository<Obj, ObjPartTransition>,
	property: dddrive.property.model.Property<*>,
	id: Int,
) : ObjPartBase<Obj>(obj, repository, property, id),
	ObjPartTransition {

	private var _tenantId by referenceIdProperty<Obj>("tenant")
	private var _userId by referenceIdProperty<Obj>("user")
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
