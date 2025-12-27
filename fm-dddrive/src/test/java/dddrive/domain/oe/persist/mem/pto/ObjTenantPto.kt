package dddrive.domain.oe.persist.mem.pto

import dddrive.domain.obj.persist.mem.pto.ObjMetaPto
import dddrive.domain.obj.persist.mem.pto.ObjPto

open class ObjTenantPto(
	var key: String? = null,
	var name: String? = null,
	var description: String? = null,
	// Properties from parent
	id: Int? = null,
	tenantId: Int? = null,
	meta: ObjMetaPto? = null,
	caption: String? = null,
) : ObjPto(id, tenantId, meta, caption) {

	override fun getObjTypeId() = "objTenant"
}
