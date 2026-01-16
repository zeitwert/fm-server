package io.zeitwert.persist.sql.doc.impl

import dddrive.app.doc.model.Doc
import dddrive.query.QuerySpec
import io.zeitwert.app.doc.model.db.Tables
import io.zeitwert.app.session.model.KernelContext
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.persist.DocPersistenceProvider
import io.zeitwert.persist.sql.ddd.SqlIdProvider
import io.zeitwert.persist.sql.doc.base.DocSqlPersistenceProviderBase
import org.jooq.DSLContext
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Component("docPersistenceProvider")
@Primary
@ConditionalOnProperty(name = ["zeitwert.persistence_type"], havingValue = "sql", matchIfMissing = true)
open class DocSqlPersistenceProviderImpl(
	override val dslContext: DSLContext,
	override val sessionContext: SessionContext,
	override val kernelContext: KernelContext,
) : DocSqlPersistenceProviderBase<Doc>(Doc::class.java),
	DocPersistenceProvider {

	override val idProvider: SqlIdProvider get() = baseRecordMapper

	override val baseRecordMapper = DocRecordMapperImpl(dslContext)

	override val extnRecordMapper = null

	override fun isDoc(id: Any): Boolean = baseRecordMapper.isDoc(id)

	override fun doFind(query: QuerySpec): List<Any> = doFind(Tables.DOC, Tables.DOC.ID, query)

}
