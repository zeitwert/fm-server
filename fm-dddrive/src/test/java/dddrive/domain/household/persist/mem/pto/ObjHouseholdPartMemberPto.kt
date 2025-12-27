package dddrive.domain.household.persist.mem.pto

import dddrive.domain.ddd.persist.mem.pto.PartPto

open class ObjHouseholdPartMemberPto(
	var salutation: String? = null,
	var name: String? = null,
	var spouseId: Int? = null,
	// Properties from parent
	id: Int? = null,
) : PartPto(id)
