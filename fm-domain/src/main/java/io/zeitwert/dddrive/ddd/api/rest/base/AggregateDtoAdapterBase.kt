package io.zeitwert.dddrive.ddd.api.rest.base

import io.dddrive.core.ddd.model.Aggregate
import io.dddrive.core.ddd.model.AggregateRepository
import io.dddrive.core.oe.model.ObjTenant
import io.dddrive.core.oe.model.ObjUser
import io.zeitwert.dddrive.ddd.api.rest.AggregateDtoAdapter
import io.zeitwert.dddrive.ddd.api.rest.DtoDetailLevel
import io.zeitwert.dddrive.ddd.api.rest.DtoUtils
import io.zeitwert.dddrive.ddd.api.rest.dto.AggregateDto
import org.springframework.beans.factory.annotation.Autowired

abstract class AggregateDtoAdapterBase<A : Aggregate, D : AggregateDto<A>>(
	protected val repo: AggregateRepository<A>,
) : AggregateDtoAdapter<A, D> {

	private lateinit var tenantRepo: AggregateRepository<ObjTenant>
	private lateinit var userRepo: AggregateRepository<ObjUser>

	override fun fromAggregate(
		id: String,
		detailLevel: DtoDetailLevel,
	): D = fromAggregate(repo.get(DtoUtils.idFromString(id)!!), detailLevel)

	abstract override fun fromAggregate(
		aggregate: A,
		detailLevel: DtoDetailLevel,
	): D

	@Autowired
	protected fun setTenantRepo(tenantRepo: AggregateRepository<ObjTenant>) {
		this.tenantRepo = tenantRepo
	}

	protected fun getTenant(tenantId: Any): ObjTenant = tenantRepo.get(tenantId)

	@Autowired
	protected fun setUserRepo(userRepo: AggregateRepository<ObjUser>) {
		this.userRepo = userRepo
	}

	protected fun getUser(userId: Any): ObjUser? = userRepo.get(userId)

}
