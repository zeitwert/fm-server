package io.dddrive.domain.household.model.impl

import io.dddrive.ddd.model.Part
import io.dddrive.domain.household.model.ObjHousehold
import io.dddrive.domain.household.model.ObjHouseholdPartMember
import io.dddrive.domain.household.model.ObjHouseholdRepository
import io.dddrive.domain.household.model.enums.CodeLabel
import io.dddrive.domain.household.model.enums.CodeSalutation
import io.dddrive.obj.model.base.ObjBase
import io.dddrive.oe.model.ObjUser
import io.dddrive.property.delegate.baseProperty
import io.dddrive.property.delegate.enumProperty
import io.dddrive.property.delegate.enumSetProperty
import io.dddrive.property.delegate.partListProperty
import io.dddrive.property.delegate.partReferenceIdProperty
import io.dddrive.property.delegate.partReferenceProperty
import io.dddrive.property.delegate.referenceIdProperty
import io.dddrive.property.delegate.referenceProperty
import io.dddrive.property.delegate.referenceSetProperty
import io.dddrive.property.model.EnumSetProperty
import io.dddrive.property.model.PartListProperty
import io.dddrive.property.model.Property
import io.dddrive.property.model.ReferenceSetProperty

class ObjHouseholdImpl(
	repository: ObjHouseholdRepository,
	isNew: Boolean,
) : ObjBase(repository, isNew),
	ObjHousehold {

	override var salutation: CodeSalutation? by enumProperty(this, "salutation")
	override var name: String? by baseProperty(this, "name")
	override var responsibleUserId: Any? by referenceIdProperty<ObjUser>(this, "responsibleUser")
	override var responsibleUser: ObjUser? by referenceProperty(this, "responsibleUser")
	override var mainMemberId: Int? by partReferenceIdProperty<ObjHouseholdPartMember>(this, "mainMember")
	override var mainMember: ObjHouseholdPartMember? by partReferenceProperty(this, "mainMember")
	override val labelSet: EnumSetProperty<CodeLabel> by enumSetProperty(this, "labelSet")
	override val userSet: ReferenceSetProperty<ObjUser> by referenceSetProperty(this, "userSet")
	override val memberList: PartListProperty<ObjHouseholdPartMember> by partListProperty(this, "memberList")

	override fun doAddPart(
		property: Property<*>,
		partId: Int?,
	): Part<*> {
		if (property === memberList) {
			return directory.getPartRepository(ObjHouseholdPartMember::class.java).create(this, property, partId)
		}
		return super.doAddPart(property, partId)
	}

}
