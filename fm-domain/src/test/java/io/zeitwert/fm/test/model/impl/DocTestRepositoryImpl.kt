package io.zeitwert.fm.test.model.impl

import io.zeitwert.fm.app.model.RequestContextFM
import io.zeitwert.fm.doc.model.base.FMDocRepositoryBase
import io.zeitwert.fm.test.model.DocTest
import io.zeitwert.fm.test.model.DocTestRepository
import org.springframework.stereotype.Component

@Component("docTestRepository")
class DocTestRepositoryImpl(
	override val requestCtx: RequestContextFM,
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
