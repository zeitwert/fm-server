package dddrive.domain.household.model.impl

import dddrive.app.obj.model.base.ObjBase
import dddrive.ddd.model.Part
import dddrive.property.delegate.baseProperty
import dddrive.property.delegate.enumProperty
import dddrive.property.delegate.enumSetProperty
import dddrive.property.delegate.partListProperty
import dddrive.property.delegate.partMapProperty
import dddrive.property.delegate.partReferenceIdProperty
import dddrive.property.delegate.partReferenceProperty
import dddrive.property.delegate.referenceIdProperty
import dddrive.property.delegate.referenceProperty
import dddrive.property.delegate.referenceSetProperty
import dddrive.property.model.Property
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
	override val membersByRole = partMapProperty<ObjHousehold, ObjHouseholdPartMember>("membersByRole")
	override var literalId by baseProperty<String>("literalId")

	// Computed properties - calculator returns the value directly for baseProperty
	override var memberCount by baseProperty<Int>("memberCount") { _ ->
		memberList.size
	}

	// Computed properties - calculator returns the ID for partReferenceProperty
	override var firstMember by partReferenceProperty<ObjHousehold, ObjHouseholdPartMember>("firstMember") { _ ->
		if (memberList.isEmpty()) null else memberList[0].id
	}

	override fun doAddPart(
		property: Property<*>,
		partId: Int?,
	): Part<*> {
		if (property === memberList || property === membersByRole) {
			return directory.getPartRepository(ObjHouseholdPartMember::class.java).create(this, property, partId)
		}
		return super.doAddPart(property, partId)
	}

}
