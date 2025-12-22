package io.dddrive.obj.model.base

import io.dddrive.ddd.model.PartRepository
import io.dddrive.obj.model.Obj
import io.dddrive.obj.model.ObjPartTransition
import io.dddrive.oe.model.ObjUser
import io.dddrive.path.setValueByPath
import io.dddrive.property.model.Property
import java.time.OffsetDateTime

abstract class ObjPartTransitionBase(
	obj: Obj,
	override val repository: PartRepository<Obj, ObjPartTransition>,
	property: Property<*>,
	id: Int,
) : ObjPartBase<Obj>(obj, repository, property, id),
	ObjPartTransition {

	override fun doInit() {
		super.doInit()
		addBaseProperty("tenantId", Any::class.java)
		addReferenceProperty("user", ObjUser::class.java)
		addBaseProperty("timestamp", OffsetDateTime::class.java)
	}

	override fun doAfterCreate() {
		super.doAfterCreate()
		setValueByPath("tenantId", aggregate.tenantId)
	}

	override fun init(
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		setValueByPath("userId", userId)
		setValueByPath("timestamp", timestamp)
	}

}
