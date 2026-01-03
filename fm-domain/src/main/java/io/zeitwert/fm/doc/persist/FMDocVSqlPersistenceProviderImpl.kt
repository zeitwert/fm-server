package io.zeitwert.fm.doc.persist

import dddrive.app.doc.model.Doc
import io.crnk.core.queryspec.QuerySpec
import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.dddrive.persist.SqlIdProvider
import io.zeitwert.fm.doc.model.db.Tables
import org.jooq.DSLContext
import org.springframework.stereotype.Component

@Component("docPersistenceProvider")
open class FMDocVSqlPersistenceProviderImpl<D : Doc>(
	override val dslContext: DSLContext,
	override val sessionContext: SessionContext,
) : FMDocSqlPersistenceProviderBase<Doc>(Doc::class.java) {

	override val idProvider: SqlIdProvider get() = baseRecordMapper

	override val baseRecordMapper = DocRecordMapperImpl(dslContext)

	override val extnRecordMapper = null

	override fun doFind(query: QuerySpec): List<Any> = doFind(Tables.DOC, Tables.DOC.ID, query)

}
