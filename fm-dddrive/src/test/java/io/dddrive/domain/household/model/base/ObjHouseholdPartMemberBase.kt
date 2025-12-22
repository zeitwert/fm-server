package io.dddrive.domain.household.model.base

import io.dddrive.ddd.model.PartRepository
import io.dddrive.obj.model.base.ObjPartBase
import io.dddrive.property.model.BaseProperty
import io.dddrive.property.model.EnumProperty
import io.dddrive.property.model.PartReferenceProperty
import io.dddrive.property.model.Property
import io.dddrive.domain.household.model.ObjHousehold
import io.dddrive.domain.household.model.ObjHouseholdPartMember
import io.dddrive.domain.household.model.enums.CodeSalutation

@Suppress("ktlint")
abstract class ObjHouseholdPartMemberBase(
	obj: ObjHousehold,
	repository: PartRepository<ObjHousehold, ObjHouseholdPartMember>,
	property: Property<*>,
	id: Int
) : ObjPartBase<ObjHousehold>(obj, repository, property, id), ObjHouseholdPartMember {

	//@formatter:off
	protected val _salutation: EnumProperty<CodeSalutation> = this.addEnumProperty("salutation", CodeSalutation::class.java)
	protected val _name: BaseProperty<String> = this.addBaseProperty("name", String::class.java)
	protected val _spouse: PartReferenceProperty<ObjHouseholdPartMember> = this.addPartReferenceProperty("spouse", ObjHouseholdPartMember::class.java)
	//@formatter:on

	@Suppress("UNCHECKED_CAST")
	override val repository get() = super.repository as PartRepository<ObjHousehold, ObjHouseholdPartMember>

	override fun delete() {}

	override val household: ObjHousehold = this.aggregate

}
