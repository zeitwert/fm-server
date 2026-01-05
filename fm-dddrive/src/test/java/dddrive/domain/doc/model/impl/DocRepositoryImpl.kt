package dddrive.domain.doc.model.impl

import dddrive.app.ddd.model.SessionContext
import dddrive.app.doc.model.Doc
import dddrive.app.doc.model.DocRepository
import dddrive.app.doc.model.base.DocRepositoryBase
import dddrive.domain.doc.persist.base.MapDocPersistenceProviderBase
import io.crnk.core.queryspec.QuerySpec
import org.springframework.beans.factory.ObjectProvider
import org.springframework.stereotype.Component

@Component("docRepository")
class DocRepositoryImpl(
	private val sessionContextProvider: ObjectProvider<SessionContext>,
) : DocRepositoryBase<Doc>(
		Doc::class.java,
		AGGREGATE_TYPE,
	),
	DocRepository<Doc> {

	override val sessionContext: SessionContext get() = sessionContextProvider.getObject()

	override val persistenceProvider = object : MapDocPersistenceProviderBase<Doc>(Doc::class.java) {}

	override fun createAggregate(isNew: Boolean): Doc = TODO()

	override fun find(query: QuerySpec?): List<Any> = persistenceProvider.find(query)

	companion object {

		private const val AGGREGATE_TYPE = "obj"
	}

}
