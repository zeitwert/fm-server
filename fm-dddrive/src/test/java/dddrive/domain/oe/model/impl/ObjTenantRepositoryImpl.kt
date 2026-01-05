package dddrive.domain.oe.model.impl

import dddrive.app.ddd.model.SessionContext
import dddrive.app.obj.model.base.ObjRepositoryBase
import dddrive.domain.oe.model.ObjTenant
import dddrive.domain.oe.model.ObjTenantRepository
import dddrive.ddd.query.QuerySpec
import dddrive.domain.oe.persist.ObjTenantPersistenceProvider
import org.springframework.beans.factory.ObjectProvider
import org.springframework.stereotype.Component
import java.util.*

@Component("objTenantRepository")
class ObjTenantRepositoryImpl(
	private val sessionContextProvider: ObjectProvider<SessionContext>,
) : ObjRepositoryBase<ObjTenant>(
		ObjTenant::class.java,
		AGGREGATE_TYPE,
	),
	ObjTenantRepository {

	override val sessionContext: SessionContext get() = sessionContextProvider.getObject()

	override val persistenceProvider get() = directory.getPersistenceProvider(ObjTenant::class.java) as ObjTenantPersistenceProvider

	override fun createAggregate(isNew: Boolean): ObjTenant = ObjTenantImpl(this, isNew)

	override fun find(query: QuerySpec?): List<Any> = persistenceProvider.find(query)

	override fun getByKey(key: String): Optional<ObjTenant> = this.persistenceProvider.getByKey(key).map { get(it) }

	companion object {

		private const val AGGREGATE_TYPE = "objTenant"
	}

}
