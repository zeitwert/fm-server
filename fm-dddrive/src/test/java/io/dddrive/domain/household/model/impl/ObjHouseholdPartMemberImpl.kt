package io.dddrive.domain.household.model.impl

import io.dddrive.ddd.model.PartRepository
import io.dddrive.domain.household.model.ObjHousehold
import io.dddrive.domain.household.model.ObjHouseholdPartMember
import io.dddrive.domain.household.model.enums.CodeSalutation
import io.dddrive.obj.model.base.ObjPartBase
import io.dddrive.property.delegate.baseProperty
import io.dddrive.property.delegate.enumProperty
import io.dddrive.property.delegate.partReferenceIdProperty
import io.dddrive.property.delegate.partReferenceProperty
import io.dddrive.property.model.Property

class ObjHouseholdPartMemberImpl(
	obj: ObjHousehold,
	override val repository: PartRepository<ObjHousehold, ObjHouseholdPartMember>,
	property: Property<*>,
	id: Int,
) : ObjPartBase<ObjHousehold>(obj, repository, property, id),
	ObjHouseholdPartMember {

	override val household: ObjHousehold = aggregate

	// Enum property
	override var salutation: CodeSalutation? by enumProperty()

	// Base property
	override var name: String? by baseProperty()

	// Part reference properties
	override var spouseId: Int? by partReferenceIdProperty<ObjHouseholdPartMember>()
	override var spouse: ObjHouseholdPartMember? by partReferenceProperty()

	override fun delete() {}

}
