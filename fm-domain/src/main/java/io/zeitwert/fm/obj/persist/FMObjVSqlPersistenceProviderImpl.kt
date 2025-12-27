package io.zeitwert.fm.obj.persist

import dddrive.app.obj.model.Obj
import io.crnk.core.queryspec.QuerySpec
import io.zeitwert.dddrive.persist.SqlIdProvider
import io.zeitwert.fm.app.model.RequestContextFM
import io.zeitwert.fm.obj.model.db.Tables
import org.jooq.DSLContext
import org.springframework.stereotype.Component

@Component("objPersistenceProvider")
open class FMObjVSqlPersistenceProviderImpl<O : Obj>(
	override val dslContext: DSLContext,
	override val requestCtx: RequestContextFM,
) : FMObjSqlPersistenceProviderBase<Obj>(Obj::class.java) {

	override val idProvider: SqlIdProvider get() = baseRecordMapper

	override val baseRecordMapper = ObjRecordMapperImpl(dslContext)

	override val extnRecordMapper = null

	override fun doFind(query: QuerySpec): List<Any> = doFind(Tables.OBJ, Tables.OBJ.ID, query)

}
