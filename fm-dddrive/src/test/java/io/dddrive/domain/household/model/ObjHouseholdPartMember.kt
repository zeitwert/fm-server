package io.dddrive.domain.household.model

import io.dddrive.domain.household.model.enums.CodeSalutation
import io.dddrive.obj.model.ObjPart

interface ObjHouseholdPartMember : ObjPart<ObjHousehold> {

	val household: ObjHousehold

	var salutation: CodeSalutation?

	var name: String?

	var spouseId: Int?

	var spouse: ObjHouseholdPartMember?

}
