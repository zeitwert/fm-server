package io.zeitwert.persist.sql

import dddrive.app.obj.model.Obj
import dddrive.query.QuerySpec
import io.zeitwert.app.obj.model.db.Tables
import io.zeitwert.app.session.model.KernelContext
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.persist.ObjPersistenceProvider
import io.zeitwert.persist.sql.ddd.SqlIdProvider
import io.zeitwert.persist.sql.obj.ObjRecordMapperImpl
import io.zeitwert.persist.sql.obj.ObjSqlPersistenceProviderBase
import org.jooq.DSLContext
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Component("objPersistenceProvider")
@Primary
@ConditionalOnProperty(name = ["zeitwert.persistence.type"], havingValue = "sql", matchIfMissing = true)
open class ObjSqlPersistenceProviderImpl(
	override val dslContext: DSLContext,
	override val sessionContext: SessionContext,
	override val kernelContext: KernelContext,
) : ObjSqlPersistenceProviderBase<Obj>(Obj::class.java),
	ObjPersistenceProvider {

	override val idProvider: SqlIdProvider get() = baseRecordMapper

	override val baseRecordMapper = ObjRecordMapperImpl(dslContext)

	override val extnRecordMapper = null

	override fun isObj(id: Any): Boolean = baseRecordMapper.isObj(id)

	override fun doFind(query: QuerySpec): List<Any> = doFind(Tables.OBJ, Tables.OBJ.ID, query)

}
