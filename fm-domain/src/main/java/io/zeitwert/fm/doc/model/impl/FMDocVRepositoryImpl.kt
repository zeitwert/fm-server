package io.zeitwert.fm.doc.model.impl

import dddrive.app.doc.model.Doc
import io.zeitwert.fm.app.model.SessionContextFM
import io.zeitwert.fm.doc.model.FMDocVRepository
import io.zeitwert.fm.doc.model.base.FMDocRepositoryBase
import org.springframework.stereotype.Component

@Component("docRepository")
class FMDocVRepositoryImpl(
	override val requestCtx: SessionContextFM,
) : FMDocRepositoryBase<Doc>(
		Doc::class.java,
		AGGREGATE_TYPE_ID,
	),
	FMDocVRepository {

	override fun createAggregate(isNew: Boolean) = DocVImpl(this, isNew)

	override fun create(): Doc = throw UnsupportedOperationException("this is a readonly repository")

	override fun load(id: Any): Doc = throw UnsupportedOperationException("this is a readonly repository")

	override fun store(aggregate: Doc) = throw UnsupportedOperationException("this is a readonly repository")

	companion object {

		private const val AGGREGATE_TYPE_ID = "doc"
	}

}
