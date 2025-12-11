package io.zeitwert.dddrive.ddd.api.rest.base

import io.zeitwert.dddrive.ddd.api.rest.DtoDetailLevel
import io.zeitwert.dddrive.ddd.api.rest.dto.AggregateDto
import io.dddrive.core.ddd.model.Aggregate
import io.dddrive.core.ddd.model.AggregateRepository
import io.dddrive.core.oe.model.ObjTenant
import io.dddrive.core.oe.model.ObjUser
import io.zeitwert.dddrive.ddd.api.rest.AggregateDtoAdapter
import io.zeitwert.dddrive.ddd.api.rest.DtoUtils
import org.springframework.beans.factory.annotation.Autowired

abstract class AggregateDtoAdapterBase<A : Aggregate, D : AggregateDto<A>>(
	private val repo: AggregateRepository<A>,
) : AggregateDtoAdapter<A, D> {
	private var tenantRepo: AggregateRepository<ObjTenant>? = null
	private var userRepo: AggregateRepository<ObjUser>? = null

	protected fun getRepo(): AggregateRepository<A> = repo

	override fun fromAggregate(
		id: String,
		detailLevel: DtoDetailLevel,
	): D = fromAggregate(repo.get(DtoUtils.idFromString(id)), detailLevel)

	abstract override fun fromAggregate(
		aggregate: A,
		detailLevel: DtoDetailLevel,
	): D

	protected fun getTenantRepo(): AggregateRepository<ObjTenant>? = tenantRepo

	@Autowired
	protected fun setTenantRepo(tenantRepo: AggregateRepository<ObjTenant>) {
		this.tenantRepo = tenantRepo
	}

	protected fun getTenant(tenantId: Any?): ObjTenant? = tenantRepo?.get(tenantId)

	protected fun getUserRepo(): AggregateRepository<ObjUser>? = userRepo

	@Autowired
	protected fun setUserRepo(userRepo: AggregateRepository<ObjUser>) {
		this.userRepo = userRepo
	}

	protected fun getUser(userId: Any?): ObjUser? = userRepo?.get(userId)
}
