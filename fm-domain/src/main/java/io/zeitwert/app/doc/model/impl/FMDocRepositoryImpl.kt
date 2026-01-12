package io.zeitwert.app.doc.model.impl

import dddrive.app.doc.model.Doc
import io.zeitwert.app.doc.model.FMDocRepository
import io.zeitwert.app.doc.model.base.FMDocRepositoryBase
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.persist.DocPersistenceProvider
import org.springframework.stereotype.Component

@Component("docRepository")
class FMDocRepositoryImpl(
	override val sessionContext: SessionContext,
) : FMDocRepositoryBase<Doc>(
		Doc::class.java,
		AGGREGATE_TYPE_ID,
	),
	FMDocRepository {

	override val persistenceProvider get() = super.persistenceProvider as DocPersistenceProvider

	override fun isDoc(id: Any): Boolean = persistenceProvider.isDoc(id)

	override fun createAggregate(isNew: Boolean) = DocImpl(this, isNew)

	override fun create(): Doc = throw UnsupportedOperationException("this is a readonly repository")

	override fun load(id: Any): Doc = throw UnsupportedOperationException("this is a readonly repository")

	override fun store(aggregate: Doc) = throw UnsupportedOperationException("this is a readonly repository")

	companion object {

		private const val AGGREGATE_TYPE_ID = "doc"
	}

}
