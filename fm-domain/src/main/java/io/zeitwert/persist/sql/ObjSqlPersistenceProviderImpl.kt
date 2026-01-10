package io.zeitwert.persist.sql

import dddrive.app.obj.model.Obj
import dddrive.ddd.query.QuerySpec
import io.zeitwert.app.obj.model.db.Tables
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.persist.sql.ddd.SqlIdProvider
import io.zeitwert.persist.sql.obj.ObjRecordMapperImpl
import io.zeitwert.persist.sql.obj.ObjSqlPersistenceProviderBase
import org.jooq.DSLContext
import org.springframework.stereotype.Component

@Component("objPersistenceProvider")
open class ObjSqlPersistenceProviderImpl(
	override val dslContext: DSLContext,
	override val sessionContext: SessionContext,
) : ObjSqlPersistenceProviderBase<Obj>(Obj::class.java) {

	override val idProvider: SqlIdProvider get() = baseRecordMapper

	override val baseRecordMapper = ObjRecordMapperImpl(dslContext)

	override val extnRecordMapper = null

	fun isObj(id: Any): Boolean = baseRecordMapper.isObj(id)

	override fun doFind(query: QuerySpec): List<Any> = doFind(Tables.OBJ, Tables.OBJ.ID, query)

}
