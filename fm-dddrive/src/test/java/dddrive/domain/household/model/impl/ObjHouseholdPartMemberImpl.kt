package dddrive.domain.household.model.impl

import dddrive.app.obj.model.base.ObjPartBase
import dddrive.ddd.model.PartRepository
import dddrive.ddd.property.delegate.baseProperty
import dddrive.ddd.property.delegate.enumProperty
import dddrive.ddd.property.delegate.partReferenceIdProperty
import dddrive.ddd.property.delegate.partReferenceProperty
import dddrive.ddd.property.model.Property
import dddrive.domain.household.model.ObjHousehold
import dddrive.domain.household.model.ObjHouseholdPartMember
import dddrive.domain.household.model.enums.CodeSalutation

class ObjHouseholdPartMemberImpl(
	obj: ObjHousehold,
	override val repository: PartRepository<ObjHousehold, ObjHouseholdPartMember>,
	property: Property<*>,
	id: Int,
) : ObjPartBase<ObjHousehold>(obj, repository, property, id),
	ObjHouseholdPartMember {

	override val household: ObjHousehold = aggregate

	override var salutation by enumProperty<CodeSalutation>("salutation")
	override var name by baseProperty<String>("name")
	override var spouseId by partReferenceIdProperty<ObjHousehold, ObjHouseholdPartMember>("spouse")
	override var spouse by partReferenceProperty<ObjHousehold, ObjHouseholdPartMember>("spouse")

}
