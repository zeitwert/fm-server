package io.dddrive.domain.household.persist.mem.impl

import io.dddrive.core.property.model.PartListProperty
import io.dddrive.dddrive.obj.persist.mem.base.MemObjPersistenceProviderBase
import io.dddrive.domain.household.model.ObjHousehold
import io.dddrive.domain.household.model.ObjHouseholdPartMember
import io.dddrive.domain.household.model.enums.CodeLabel
import io.dddrive.domain.household.model.enums.CodeSalutation
import io.dddrive.domain.household.persist.ObjHouseholdPersistenceProvider
import io.dddrive.domain.household.persist.mem.pto.ObjHouseholdPartMemberPto
import io.dddrive.domain.household.persist.mem.pto.ObjHouseholdPto
import org.springframework.stereotype.Component

@Component("objHouseholdPersistenceProvider")
class MemObjHouseholdPersistenceProviderImpl :
	MemObjPersistenceProviderBase<ObjHousehold, ObjHouseholdPto>(ObjHousehold::class.java),
	ObjHouseholdPersistenceProvider {

	@Suppress("UNCHECKED_CAST")
	override fun toAggregate(
		pto: ObjHouseholdPto,
		aggregate: ObjHousehold,
	) {
		val aggregateMeta = aggregate.meta

		try {
			aggregateMeta.disableCalc()

			super.toAggregate(pto, aggregate)

			aggregate.name = pto.name

			aggregate.clearLabelSet()
			pto.labels?.forEach { labelId ->
				val codeLabel = CodeLabel.Enumeration.getItem(labelId)
				aggregate.addLabel(codeLabel)
			}

			aggregate.clearUserSet()
			pto.users?.forEach(aggregate::addUser)

			val memberListProperty = aggregate.getProperty("memberList") as? PartListProperty<ObjHouseholdPartMember>
			pto.members?.forEach { memberPto ->
				val member = memberListProperty?.addPart(memberPto.id)
				member?.salutation = memberPto.salutation?.let { CodeSalutation.Enumeration.getItem(it) }
				member?.name = memberPto.name
			}

			aggregate.mainMemberId = pto.mainMemberId

			pto.members?.forEach { memberPto ->
				memberPto.id?.let { currentMemberId ->
					val member = aggregate.getMemberById(currentMemberId)
					member?.spouseId = memberPto.spouseId
				}
			}
		} finally {
			aggregateMeta.enableCalc()
			aggregate.meta.calcAll()
		}
	}

	override fun fromAggregate(aggregate: ObjHousehold): ObjHouseholdPto {
		val members =
			aggregate.memberList
				.map { member ->
					ObjHouseholdPartMemberPto(
						id = member.id,
						salutation = member.salutation?.id,
						name = member.name,
						spouseId = member.spouseId,
					)
				}.toList()

		return ObjHouseholdPto(
			id = aggregate.id as? Int,
			tenantId = aggregate.tenantId as? Int,
			meta = this.getMeta(aggregate),
			caption = aggregate.caption,
			name = aggregate.name,
			labels = aggregate.labelSet.mapNotNull { it.id }.toSet(),
			users = aggregate.userSet.mapNotNull { it as? Int }.toSet(),
			members = members,
			mainMemberId = aggregate.mainMemberId,
		)
	}
}
