package io.dddrive.domain.household.model.base

import io.dddrive.core.ddd.model.PartRepository
import io.dddrive.core.obj.model.base.ObjPartBase
import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.EnumProperty
import io.dddrive.core.property.model.PartReferenceProperty
import io.dddrive.core.property.model.Property
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
	override val repository: PartRepository<ObjHousehold, ObjHouseholdPartMember>
		get() {
			return super.repository as PartRepository<ObjHousehold, ObjHouseholdPartMember>
		}

	override fun delete() {}

	override val household: ObjHousehold = this.aggregate

}
