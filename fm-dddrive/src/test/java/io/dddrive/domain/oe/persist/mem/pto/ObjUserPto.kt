package io.dddrive.domain.oe.persist.mem.pto

import io.dddrive.dddrive.obj.persist.mem.pto.ObjMetaPto
import io.dddrive.dddrive.obj.persist.mem.pto.ObjPto

open class ObjUserPto(
	var email: String? = null,
	var name: String? = null,
	var description: String? = null,
	// Properties from parent
	id: Int? = null,
	tenantId: Int? = null,
	meta: ObjMetaPto? = null,
	caption: String? = null,
) : ObjPto(id, tenantId, meta, caption) {

	override fun getObjTypeId() = "objUser"
}
