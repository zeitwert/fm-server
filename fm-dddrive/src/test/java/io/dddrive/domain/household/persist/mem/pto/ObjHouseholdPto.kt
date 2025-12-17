package io.dddrive.domain.household.persist.mem.pto

import io.dddrive.dddrive.obj.persist.mem.pto.ObjMetaPto
import io.dddrive.dddrive.obj.persist.mem.pto.ObjPto

open class ObjHouseholdPto(
	var name: String? = null,
	var labels: Set<String>? = null,
	var users: Set<Int>? = null,
	var members: List<ObjHouseholdPartMemberPto>? = null,
	var mainMemberId: Int? = null,
	// Properties from parent
	id: Int? = null,
	tenantId: Int? = null,
	meta: ObjMetaPto? = null,
	caption: String? = null,
) : ObjPto(id, tenantId, meta, caption) {

	override fun getObjTypeId() = "objHousehold"
}
