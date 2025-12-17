package io.dddrive.domain.household.model

import io.dddrive.core.obj.model.ObjPart
import io.dddrive.domain.household.model.enums.CodeSalutation

interface ObjHouseholdPartMember : ObjPart<ObjHousehold> {
	val household: ObjHousehold

	var salutation: CodeSalutation?
	var name: String?

	var spouseId: Int?
	var spouse: ObjHouseholdPartMember?
}
