package io.zeitwert.fm.obj.persist

import io.dddrive.obj.model.Obj
import io.zeitwert.dddrive.persist.SqlIdProvider
import org.jooq.DSLContext
import org.springframework.stereotype.Component

@Component("objPersistenceProvider")
open class FMObjSqlPersistenceProviderImpl<O : Obj>(
	override val dslContext: DSLContext,
) : FMObjSqlPersistenceProviderBase<Obj>(Obj::class.java) {

	override val idProvider: SqlIdProvider get() = baseRecordMapper

	override val baseRecordMapper = ObjRecordMapperImpl(dslContext)

	override val extnRecordMapper = null

}
