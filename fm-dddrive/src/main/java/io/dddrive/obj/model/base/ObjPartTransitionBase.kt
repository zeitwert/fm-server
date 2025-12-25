package io.dddrive.obj.model.base

import io.dddrive.ddd.model.PartRepository
import io.dddrive.obj.model.Obj
import io.dddrive.obj.model.ObjPartTransition
import io.dddrive.oe.model.ObjUser
import io.dddrive.property.delegate.baseProperty
import io.dddrive.property.delegate.referenceIdProperty
import io.dddrive.property.delegate.referenceProperty
import io.dddrive.property.model.Property
import java.time.OffsetDateTime

abstract class ObjPartTransitionBase(
	obj: Obj,
	override val repository: PartRepository<Obj, ObjPartTransition>,
	property: Property<*>,
	id: Int,
) : ObjPartBase<Obj>(obj, repository, property, id),
	ObjPartTransition {

	// Private mutable backing for read-only interface properties
	private var _tenantId: Any? by baseProperty()

	private var _user: ObjUser? by referenceProperty()
	override val user: ObjUser get() = _user!!

	private var _userId: Any? by referenceIdProperty<ObjUser>()

	private var _timestamp: OffsetDateTime? by baseProperty()
	override val timestamp: OffsetDateTime get() = _timestamp!!

	// Register properties for setValueByPath access (interface has val properties)
	@Suppress("UNUSED_EXPRESSION")
	override fun doInit() {
		super.doInit()
		_tenantId
		_user
		_userId
		_timestamp
	}

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
