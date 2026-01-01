package io.zeitwert.domain.test.model.impl

import io.zeitwert.domain.test.model.DocTest
import io.zeitwert.domain.test.model.DocTestRepository
import io.zeitwert.fm.app.model.SessionContextFM
import io.zeitwert.fm.doc.model.base.FMDocRepositoryBase
import org.springframework.stereotype.Component

@Component("docTestRepository")
class DocTestRepositoryImpl(
	override val requestCtx: SessionContextFM,
) : FMDocRepositoryBase<DocTest>(
		DocTest::class.java,
		AGGREGATE_TYPE_ID,
	),
	DocTestRepository {

	override fun createAggregate(isNew: Boolean): DocTest = DocTestImpl(this, isNew)

	companion object {

		private const val AGGREGATE_TYPE_ID = "doc_test"
	}

}
