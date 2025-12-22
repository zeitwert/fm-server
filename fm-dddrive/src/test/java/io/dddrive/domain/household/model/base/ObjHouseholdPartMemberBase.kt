package io.dddrive.domain.household.model.base

import io.dddrive.ddd.model.PartRepository
import io.dddrive.domain.household.model.ObjHousehold
import io.dddrive.domain.household.model.ObjHouseholdPartMember
import io.dddrive.domain.household.model.enums.CodeSalutation
import io.dddrive.obj.model.base.ObjPartBase
import io.dddrive.property.model.Property

@Suppress("ktlint")
abstract class ObjHouseholdPartMemberBase(
	obj: ObjHousehold,
	override val repository: PartRepository<ObjHousehold, ObjHouseholdPartMember>,
	property: Property<*>,
	id: Int
) : ObjPartBase<ObjHousehold>(obj, repository, property, id), ObjHouseholdPartMember {

	override fun doInit() {
		super.doInit()
		addEnumProperty("salutation", CodeSalutation::class.java)
		addBaseProperty("name", String::class.java)
		addPartReferenceProperty("spouse", ObjHouseholdPartMember::class.java)
	}

	override fun delete() {}

	override val household: ObjHousehold = this.aggregate

}
