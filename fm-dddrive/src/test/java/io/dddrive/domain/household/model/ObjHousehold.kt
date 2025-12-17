package io.dddrive.domain.household.model

import io.dddrive.core.obj.model.Obj
import io.dddrive.domain.household.model.enums.CodeLabel

interface ObjHousehold : Obj {

	var name: String?

	val labelSet: Set<CodeLabel>

	fun hasLabel(label: CodeLabel): Boolean

	fun clearLabelSet()

	fun addLabel(label: CodeLabel)

	fun removeLabel(label: CodeLabel)

	val userSet: Set<Any>

	fun hasUser(userId: Any): Boolean

	fun clearUserSet()

	fun addUser(userId: Any)

	fun removeUser(userId: Any)

	var mainMemberId: Int?

	var mainMember: ObjHouseholdPartMember?

	val memberList: List<ObjHouseholdPartMember>

	fun getMember(seqNr: Int?): ObjHouseholdPartMember?

	fun getMemberById(memberId: Int?): ObjHouseholdPartMember?

	fun addMember(): ObjHouseholdPartMember

	fun removeMember(memberId: Int?)
}
