package io.zeitwert.fm.doc.persist

import io.crnk.core.queryspec.QuerySpec
import io.dddrive.doc.model.Doc
import io.zeitwert.dddrive.persist.SqlIdProvider
import io.zeitwert.fm.doc.model.db.Tables
import io.zeitwert.fm.app.model.RequestContextFM
import org.jooq.DSLContext
import org.springframework.stereotype.Component

@Component("docPersistenceProvider")
open class FMDocVSqlPersistenceProviderImpl<D : Doc>(
	override val dslContext: DSLContext,
	override val requestCtx: RequestContextFM,
) : FMDocSqlPersistenceProviderBase<Doc>(Doc::class.java) {

	override val idProvider: SqlIdProvider get() = baseRecordMapper

	override val baseRecordMapper = DocRecordMapperImpl(dslContext)

	override val extnRecordMapper = null

	override fun doFind(query: QuerySpec): List<Any> = doFind(Tables.DOC, Tables.DOC.ID, query)

}
