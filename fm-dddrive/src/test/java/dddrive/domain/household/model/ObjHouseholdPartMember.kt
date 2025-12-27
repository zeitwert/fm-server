package dddrive.domain.household.model

import dddrive.app.obj.model.ObjPart
import dddrive.domain.household.model.enums.CodeSalutation

interface ObjHouseholdPartMember : ObjPart<ObjHousehold> {

	val household: ObjHousehold

	var salutation: CodeSalutation?

	var name: String?

	var spouseId: Int?

	var spouse: ObjHouseholdPartMember?

}
