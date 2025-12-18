package io.dddrive.domain.household.model.base

import io.dddrive.core.ddd.model.Part
import io.dddrive.core.obj.model.base.ObjBase
import io.dddrive.core.oe.model.ObjUser
import io.dddrive.core.property.model.Property
import io.dddrive.domain.household.model.ObjHousehold
import io.dddrive.domain.household.model.ObjHouseholdPartMember
import io.dddrive.domain.household.model.ObjHouseholdRepository
import io.dddrive.domain.household.model.enums.CodeLabel

abstract class ObjHouseholdBase(
	repository: ObjHouseholdRepository,
	isNew: Boolean,
) : ObjBase(repository, isNew),
	ObjHousehold {

	protected val _name = this.addBaseProperty("name", String::class.java)
	protected val _labelSet = this.addEnumSetProperty("labelSet", CodeLabel::class.java)
	protected val _userSet = this.addReferenceSetProperty("userSet", ObjUser::class.java)
	protected val _mainMember = this.addPartReferenceProperty("mainMember", ObjHouseholdPartMember::class.java)
	protected val _memberList = this.addPartListProperty("memberList", ObjHouseholdPartMember::class.java)

	override fun doAddPart(
		property: Property<*>,
		partId: Int?,
	): Part<*> =
		if (property === this._memberList) {
			this.directory.getPartRepository(ObjHouseholdPartMember::class.java).create(this, property, partId) as Part<*>
		} else {
			super.doAddPart(property, partId)
		}

}
