package dddrive.domain.household.model

import dddrive.app.obj.model.Obj
import dddrive.property.model.AggregateReferenceSetProperty
import dddrive.property.model.EnumSetProperty
import dddrive.property.model.PartListProperty
import dddrive.property.model.PartMapProperty
import dddrive.domain.household.model.enums.CodeLabel
import dddrive.domain.household.model.enums.CodeSalutation
import dddrive.domain.oe.model.ObjUser

interface ObjHousehold : Obj {

	var salutation: CodeSalutation?

	var name: String?

	var responsibleUserId: Any?

	var responsibleUser: ObjUser?

	val memberList: PartListProperty<ObjHousehold, ObjHouseholdPartMember>

	val membersByRole: PartMapProperty<ObjHousehold, ObjHouseholdPartMember>

	var mainMemberId: Int?

	var mainMember: ObjHouseholdPartMember?

	val labelSet: EnumSetProperty<CodeLabel>

	val userSet: AggregateReferenceSetProperty<ObjUser>

	var literalId: String?

	// Computed properties for testing
	val memberCount: Int?

	val firstMember: ObjHouseholdPartMember?

}
