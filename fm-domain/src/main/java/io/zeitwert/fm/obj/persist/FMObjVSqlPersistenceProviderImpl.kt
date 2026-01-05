package io.zeitwert.fm.obj.persist

import dddrive.app.obj.model.Obj
import dddrive.ddd.query.QuerySpec
import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.dddrive.persist.SqlIdProvider
import io.zeitwert.fm.obj.model.db.Tables
import org.jooq.DSLContext
import org.springframework.stereotype.Component

@Component("objPersistenceProvider")
open class FMObjVSqlPersistenceProviderImpl<O : Obj>(
	override val dslContext: DSLContext,
	override val sessionContext: SessionContext,
) : FMObjSqlPersistenceProviderBase<Obj>(Obj::class.java) {

	override val idProvider: SqlIdProvider get() = baseRecordMapper

	override val baseRecordMapper = ObjRecordMapperImpl(dslContext)

	override val extnRecordMapper = null

	override fun doFind(query: QuerySpec): List<Any> = doFind(Tables.OBJ, Tables.OBJ.ID, query)

}
