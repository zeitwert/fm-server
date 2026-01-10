package io.zeitwert.dddrive.doc.model.impl

import dddrive.app.doc.model.Doc
import io.zeitwert.app.model.SessionContext
import io.zeitwert.dddrive.doc.model.FMDocVRepository
import io.zeitwert.dddrive.doc.model.base.FMDocRepositoryBase
import io.zeitwert.dddrive.doc.persist.FMDocVSqlPersistenceProviderImpl
import org.springframework.stereotype.Component

@Component("docRepository")
class FMDocVRepositoryImpl(
	override val sessionContext: SessionContext,
) : FMDocRepositoryBase<Doc>(
		Doc::class.java,
		AGGREGATE_TYPE_ID,
	),
	FMDocVRepository {

	override val persistenceProvider get() = super.persistenceProvider as FMDocVSqlPersistenceProviderImpl

	override fun isDoc(id: Any): Boolean = persistenceProvider.isDoc(id)

	override fun createAggregate(isNew: Boolean) = DocVImpl(this, isNew)

	override fun create(): Doc = throw UnsupportedOperationException("this is a readonly repository")

	override fun load(id: Any): Doc = throw UnsupportedOperationException("this is a readonly repository")

	override fun store(aggregate: Doc) = throw UnsupportedOperationException("this is a readonly repository")

	companion object {

		private const val AGGREGATE_TYPE_ID = "doc"
	}

}
