package dddrive.app.obj.model.impl

import dddrive.app.obj.model.Obj
import dddrive.app.obj.model.ObjPartTransition
import dddrive.app.obj.model.base.ObjPartBase
import dddrive.ddd.core.model.PartRepository
import dddrive.ddd.property.delegate.baseProperty
import dddrive.ddd.property.delegate.referenceIdProperty
import dddrive.ddd.property.delegate.referenceProperty
import dddrive.ddd.property.model.Property
import io.dddrive.oe.model.ObjUser
import java.time.OffsetDateTime

class ObjPartTransitionImpl(
	obj: Obj,
	override val repository: dddrive.ddd.core.model.PartRepository<Obj, ObjPartTransition>,
	property: dddrive.ddd.property.model.Property<*>,
	id: Int,
) : ObjPartBase<Obj>(obj, repository, property, id),
	ObjPartTransition {

	// Private mutable backing for read-only interface properties
	private var _tenantId: Any? by _root_ide_package_.dddrive.ddd.property.delegate
		.baseProperty(this, "tenantId")
	private var _user: ObjUser? by _root_ide_package_.dddrive.ddd.property.delegate
		.referenceProperty(this, "user")
	private var _userId: Any? by _root_ide_package_.dddrive.ddd.property.delegate.referenceIdProperty<ObjUser>(
		this,
		"user",
	)
	private var _timestamp: OffsetDateTime? by _root_ide_package_.dddrive.ddd.property.delegate.baseProperty(
		this,
		"timestamp",
	)

	override val user: ObjUser get() = _user!!
	override val timestamp: OffsetDateTime get() = _timestamp!!

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
