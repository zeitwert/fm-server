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
import dddrive.ddd.property.model.Property
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

	override var salutation by enumProperty<CodeSalutation>("salutation")
	override var name by baseProperty<String>("name")
	override var responsibleUserId by referenceIdProperty<ObjUser>("responsibleUser")
	override var responsibleUser by referenceProperty<ObjUser>("responsibleUser")
	override var mainMemberId by partReferenceIdProperty<ObjHousehold, ObjHouseholdPartMember>("mainMember")
	override var mainMember by partReferenceProperty<ObjHousehold, ObjHouseholdPartMember>("mainMember")
	override val labelSet = enumSetProperty<CodeLabel>("labelSet")
	override val userSet = referenceSetProperty<ObjUser>("userSet")
	override val memberList = partListProperty<ObjHousehold, ObjHouseholdPartMember>("memberList")
	override var literalId by baseProperty<String>("literalId")

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
