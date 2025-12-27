package dddrive.domain.household.model.impl

import dddrive.app.obj.model.base.ObjBase
import dddrive.ddd.core.model.Part
import dddrive.ddd.property.delegate.baseProperty
import dddrive.ddd.property.delegate.enumProperty
import dddrive.ddd.property.delegate.enumSetProperty
import dddrive.ddd.property.delegate.partListProperty
import dddrive.ddd.property.delegate.partReferenceIdProperty
import dddrive.ddd.property.delegate.partReferenceProperty
import dddrive.ddd.property.delegate.referenceIdProperty
import dddrive.ddd.property.delegate.referenceProperty
import dddrive.ddd.property.delegate.referenceSetProperty
import dddrive.ddd.property.model.EnumSetProperty
import dddrive.ddd.property.model.PartListProperty
import dddrive.ddd.property.model.Property
import dddrive.ddd.property.model.ReferenceSetProperty
import dddrive.domain.household.model.ObjHousehold
import dddrive.domain.household.model.ObjHouseholdPartMember
import dddrive.domain.household.model.ObjHouseholdRepository
import dddrive.domain.household.model.enums.CodeLabel
import dddrive.domain.household.model.enums.CodeSalutation
import dddrive.domain.oe.model.ObjUser

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
	override val labelSet: EnumSetProperty<CodeLabel> = enumSetProperty(this, "labelSet")
	override val userSet: ReferenceSetProperty<ObjUser> = referenceSetProperty(this, "userSet")
	override val memberList: PartListProperty<ObjHouseholdPartMember> = partListProperty(this, "memberList")

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
