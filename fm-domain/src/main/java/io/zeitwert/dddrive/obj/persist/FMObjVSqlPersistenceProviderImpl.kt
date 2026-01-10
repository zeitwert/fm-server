package io.zeitwert.dddrive.obj.persist

import dddrive.app.obj.model.Obj
import dddrive.ddd.query.QuerySpec
import io.zeitwert.app.model.SessionContext
import io.zeitwert.dddrive.obj.model.db.Tables
import io.zeitwert.persist.sql.SqlIdProvider
import org.jooq.DSLContext
import org.springframework.stereotype.Component

@Component("objPersistenceProvider")
open class FMObjVSqlPersistenceProviderImpl(
	override val dslContext: DSLContext,
	override val sessionContext: SessionContext,
) : FMObjSqlPersistenceProviderBase<Obj>(Obj::class.java) {

	override val idProvider: SqlIdProvider get() = baseRecordMapper

	override val baseRecordMapper = ObjRecordMapperImpl(dslContext)

	override val extnRecordMapper = null

	fun isObj(id: Any): Boolean = baseRecordMapper.isObj(id)

	override fun doFind(query: QuerySpec): List<Any> = doFind(Tables.OBJ, Tables.OBJ.ID, query)

}
