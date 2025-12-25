package io.dddrive.domain.oe.model.impl

import io.dddrive.domain.oe.model.ObjTenantRepository
import io.dddrive.domain.oe.persist.ObjTenantPersistenceProvider
import io.dddrive.obj.model.base.ObjRepositoryBase
import io.dddrive.oe.model.ObjTenant
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Component
import java.util.*

@Component("objTenantRepository")
@DependsOn("objTenantPersistenceProvider")
class ObjTenantRepositoryImpl :
	ObjRepositoryBase<ObjTenant>(
		ObjTenantRepository::class.java,
		ObjTenant::class.java,
		ObjTenantTestImpl::class.java,
		AGGREGATE_TYPE,
	),
	ObjTenantRepository {

	override val persistenceProvider get() = directory.getPersistenceProvider(ObjTenant::class.java) as ObjTenantPersistenceProvider

	override fun getByKey(key: String): Optional<ObjTenant> = this.persistenceProvider.getByKey(key).map { get(it) }

	companion object {

		private const val AGGREGATE_TYPE = "objTenant"
	}

}
