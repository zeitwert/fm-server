package dddrive.domain.household.persist.mem.impl

import dddrive.domain.household.model.ObjHousehold
import dddrive.domain.household.model.enums.CodeLabel
import dddrive.domain.household.model.enums.CodeSalutation
import dddrive.domain.household.persist.ObjHouseholdPersistenceProvider
import dddrive.domain.household.persist.mem.pto.ObjHouseholdPartMemberPto
import dddrive.domain.household.persist.mem.pto.ObjHouseholdPto
import dddrive.domain.obj.persist.mem.base.MemObjPersistenceProviderBase
import org.springframework.stereotype.Component

@Component("objHouseholdPersistenceProvider")
class MemObjHouseholdPersistenceProviderImpl :
	MemObjPersistenceProviderBase<ObjHousehold, ObjHouseholdPto>(ObjHousehold::class.java),
	ObjHouseholdPersistenceProvider {

	override fun toAggregate(
		pto: ObjHouseholdPto,
		aggregate: ObjHousehold,
	) {
		val aggregateMeta = aggregate.meta

		try {
			aggregateMeta.disableCalc()

			super.toAggregate(pto, aggregate)

			aggregate.name = pto.name

			// Using new collection API
			aggregate.labelSet.clear()
			pto.labels?.forEach { labelId ->
				val codeLabel = CodeLabel.getItem(labelId)
				aggregate.labelSet.add(codeLabel)
			}

			aggregate.userSet.clear()
			pto.users?.forEach { aggregate.userSet.add(it) }

			pto.members?.forEach { memberPto ->
				val member = aggregate.memberList.add(memberPto.id)
				member.salutation = memberPto.salutation?.let { CodeSalutation.getItem(it) }
				member.name = memberPto.name
			}

			aggregate.mainMemberId = pto.mainMemberId
			aggregate.responsibleUserId = pto.responsibleUserId

			pto.members?.forEach { memberPto ->
				memberPto.id?.let { currentMemberId ->
					val member = aggregate.memberList.getById(currentMemberId)
					member.spouseId = memberPto.spouseId
				}
			}
		} finally {
			aggregateMeta.enableCalc()
			aggregate.meta.calcAll()
		}
	}

	override fun fromAggregate(aggregate: ObjHousehold): ObjHouseholdPto {
		val members =
			aggregate
				.memberList
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
			responsibleUserId = aggregate.responsibleUserId as? Int,
		)
	}
}
