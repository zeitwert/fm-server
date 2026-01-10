package io.zeitwert.dddrive.doc.persist

import dddrive.app.doc.model.Doc
import dddrive.ddd.query.QuerySpec
import io.zeitwert.app.model.SessionContext
import io.zeitwert.dddrive.doc.model.db.Tables
import io.zeitwert.persist.sql.SqlIdProvider
import org.jooq.DSLContext
import org.springframework.stereotype.Component

@Component("docPersistenceProvider")
open class FMDocVSqlPersistenceProviderImpl(
	override val dslContext: DSLContext,
	override val sessionContext: SessionContext,
) : FMDocSqlPersistenceProviderBase<Doc>(Doc::class.java) {

	override val idProvider: SqlIdProvider get() = baseRecordMapper

	override val baseRecordMapper = DocRecordMapperImpl(dslContext)

	override val extnRecordMapper = null

	fun isDoc(id: Any): Boolean = baseRecordMapper.isDoc(id)

	override fun doFind(query: QuerySpec): List<Any> = doFind(Tables.DOC, Tables.DOC.ID, query)

}
