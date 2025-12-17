package io.dddrive.domain.household.model.base

import io.dddrive.core.ddd.model.Part
import io.dddrive.core.obj.model.base.ObjBase
import io.dddrive.core.oe.model.ObjUser
import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.EnumSetProperty
import io.dddrive.core.property.model.PartListProperty
import io.dddrive.core.property.model.PartReferenceProperty
import io.dddrive.core.property.model.Property
import io.dddrive.core.property.model.ReferenceSetProperty
import io.dddrive.domain.household.model.ObjHousehold
import io.dddrive.domain.household.model.ObjHouseholdPartMember
import io.dddrive.domain.household.model.ObjHouseholdRepository
import io.dddrive.domain.household.model.enums.CodeLabel

@Suppress("ktlint")
abstract class ObjHouseholdBase(
	repository: ObjHouseholdRepository,
	isNew: Boolean,
) : ObjBase(repository, isNew), ObjHousehold {

	//@formatter:off
	protected val _name: BaseProperty<String> = this.addBaseProperty("name", String::class.java)
	protected val _labelSet: EnumSetProperty<CodeLabel> = this.addEnumSetProperty("labelSet", CodeLabel::class.java)
	protected val _userSet: ReferenceSetProperty<ObjUser> = this.addReferenceSetProperty("userSet", ObjUser::class.java)
	protected val _mainMember: PartReferenceProperty<ObjHouseholdPartMember> = this.addPartReferenceProperty("mainMember", ObjHouseholdPartMember::class.java)
	protected val _memberList: PartListProperty<ObjHouseholdPartMember> = this.addPartListProperty("memberList", ObjHouseholdPartMember::class.java)
	//@formatter:on

	override fun doAddPart(property: Property<*>, partId: Int?): Part<*> {
		return if (property === this._memberList) {
			this.directory.getPartRepository(ObjHouseholdPartMember::class.java).create(this, property, partId) as Part<*>
		} else {
			super.doAddPart(property, partId)
		}
	}

}
