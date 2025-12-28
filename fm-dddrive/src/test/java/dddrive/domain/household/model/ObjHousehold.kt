package dddrive.domain.household.model

import dddrive.app.obj.model.Obj
import dddrive.ddd.property.model.EnumSetProperty
import dddrive.ddd.property.model.PartListProperty
import dddrive.ddd.property.model.ReferenceSetProperty
import dddrive.domain.household.model.enums.CodeLabel
import dddrive.domain.household.model.enums.CodeSalutation
import dddrive.domain.oe.model.ObjUser

interface ObjHousehold : Obj {

	var salutation: CodeSalutation?

	var name: String?

	var responsibleUserId: Any?

	var responsibleUser: ObjUser?

	val memberList: PartListProperty<ObjHousehold, ObjHouseholdPartMember>

	var mainMemberId: Int?

	var mainMember: ObjHouseholdPartMember?

	val labelSet: EnumSetProperty<CodeLabel>

	val userSet: ReferenceSetProperty<ObjUser>

}
