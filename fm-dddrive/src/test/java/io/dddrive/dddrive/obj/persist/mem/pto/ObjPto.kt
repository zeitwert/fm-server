package io.dddrive.dddrive.obj.persist.mem.pto

import io.dddrive.dddrive.ddd.persist.mem.pto.AggregatePto

abstract class ObjPto(
	// Properties from parent
	id: Int? = null,
	tenantId: Int? = null,
	meta: ObjMetaPto? = null,
	caption: String? = null,
) : AggregatePto(id, tenantId, meta, caption) {

	abstract fun getObjTypeId(): String?

	// Override the property itself with covariant return type
	override val meta: ObjMetaPto?
		get() = super.meta as? ObjMetaPto
}
