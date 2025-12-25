package io.dddrive.domain.household.model.impl

import io.dddrive.ddd.model.Part
import io.dddrive.domain.household.model.ObjHousehold
import io.dddrive.domain.household.model.ObjHouseholdPartMember
import io.dddrive.domain.household.model.ObjHouseholdRepository
import io.dddrive.domain.household.model.enums.CodeLabel
import io.dddrive.obj.model.base.ObjBase
import io.dddrive.oe.model.ObjUser
import io.dddrive.property.delegate.baseProperty
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

open class ObjHouseholdImpl(
	repository: ObjHouseholdRepository,
	isNew: Boolean,
) : ObjBase(repository, isNew),
	ObjHousehold {

	// Simple properties
	override var name: String? by baseProperty()

	// Enum set property
	override val labelSet: EnumSetProperty<CodeLabel> by enumSetProperty()

	// Reference set property
	override val userSet: ReferenceSetProperty<ObjUser> by referenceSetProperty()

	// Single aggregate reference properties
	override var responsibleUserId: Any? by referenceIdProperty<ObjUser>()
	override var responsibleUser: ObjUser? by referenceProperty()

	// Part reference properties
	override var mainMemberId: Int? by partReferenceIdProperty<ObjHouseholdPartMember>()
	override var mainMember: ObjHouseholdPartMember? by partReferenceProperty()

	// Part list property
	override val memberList: PartListProperty<ObjHouseholdPartMember> by partListProperty()

	override fun doAddPart(
		property: Property<*>,
		partId: Int?,
	): Part<*> =
		if (property === memberList) {
			directory
				.getPartRepository(ObjHouseholdPartMember::class.java)
				.create(this, property, partId) as
				Part<*>
		} else {
			super.doAddPart(property, partId)
		}
}
