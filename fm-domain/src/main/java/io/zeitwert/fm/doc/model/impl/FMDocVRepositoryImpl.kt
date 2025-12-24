package io.zeitwert.fm.doc.model.impl

import io.dddrive.doc.model.Doc
import io.dddrive.doc.model.base.DocBase
import io.zeitwert.fm.doc.model.FMDocVRepository
import io.zeitwert.fm.doc.model.base.FMDocRepositoryBase
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component("docRepository")
class FMDocVRepositoryImpl :
	FMDocRepositoryBase<Doc>(
		FMDocVRepository::class.java,
		Doc::class.java,
		DocBase::class.java,
		AGGREGATE_TYPE_ID,
	),
	FMDocVRepository {

	override fun create(
		tenantId: Any,
		userId: Any,
		timestamp: OffsetDateTime,
	): Doc = throw UnsupportedOperationException("this is a readonly repository")

	override fun load(id: Any): Doc = throw UnsupportedOperationException("this is a readonly repository")

	override fun store(
		aggregate: Doc,
		userId: Any,
		timestamp: OffsetDateTime,
	) = throw UnsupportedOperationException("this is a readonly repository")

	companion object {

		private const val AGGREGATE_TYPE_ID = "doc"
	}

}
