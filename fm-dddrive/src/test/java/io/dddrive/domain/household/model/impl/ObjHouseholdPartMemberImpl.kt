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
	override var salutation: CodeSalutation? by enumProperty(this, "salutation")
	override var name: String? by baseProperty(this, "name")
	override var spouseId: Int? by partReferenceIdProperty<ObjHouseholdPartMember>(this, "spouse")
	override var spouse: ObjHouseholdPartMember? by partReferenceProperty(this, "spouse")

	override fun delete() {}

}
