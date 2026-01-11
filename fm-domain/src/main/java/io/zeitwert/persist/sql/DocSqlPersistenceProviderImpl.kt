package io.zeitwert.persist.sql

import dddrive.app.doc.model.Doc
import dddrive.query.QuerySpec
import io.zeitwert.app.doc.model.db.Tables
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.persist.sql.ddd.SqlIdProvider
import io.zeitwert.persist.sql.doc.DocRecordMapperImpl
import io.zeitwert.persist.sql.doc.DocSqlPersistenceProviderBase
import org.jooq.DSLContext
import org.springframework.stereotype.Component

@Component("docPersistenceProvider")
open class DocSqlPersistenceProviderImpl(
	override val dslContext: DSLContext,
	override val sessionContext: SessionContext,
) : DocSqlPersistenceProviderBase<Doc>(Doc::class.java) {

	override val idProvider: SqlIdProvider get() = baseRecordMapper

	override val baseRecordMapper = DocRecordMapperImpl(dslContext)

	override val extnRecordMapper = null

	fun isDoc(id: Any): Boolean = baseRecordMapper.isDoc(id)

	override fun doFind(query: QuerySpec): List<Any> = doFind(Tables.DOC, Tables.DOC.ID, query)

}
