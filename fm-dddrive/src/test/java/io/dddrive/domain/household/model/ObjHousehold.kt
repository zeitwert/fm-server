package io.dddrive.domain.household.model

import io.dddrive.domain.household.model.enums.CodeLabel
import io.dddrive.obj.model.Obj
import io.dddrive.oe.model.ObjUser
import io.dddrive.property.model.EnumSetProperty
import io.dddrive.property.model.PartListProperty
import io.dddrive.property.model.ReferenceSetProperty

interface ObjHousehold : Obj {

	var name: String?

	val labelSet: EnumSetProperty<CodeLabel>

	val userSet: ReferenceSetProperty<ObjUser>

	// Single aggregate reference properties
	var responsibleUserId: Any?

	var responsibleUser: ObjUser?

	var mainMemberId: Int?

	var mainMember: ObjHouseholdPartMember?

	val memberList: PartListProperty<ObjHouseholdPartMember>
}
