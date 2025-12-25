package io.zeitwert.fm.test.model.impl

import io.zeitwert.fm.doc.model.base.FMDocRepositoryBase
import io.zeitwert.fm.test.model.DocTest
import io.zeitwert.fm.test.model.DocTestRepository
import org.springframework.stereotype.Component

@Component("docTestRepository")
class DocTestRepositoryImpl :
	FMDocRepositoryBase<DocTest>(
		DocTestRepository::class.java,
		DocTest::class.java,
		DocTestImpl::class.java,
		AGGREGATE_TYPE_ID,
	),
	DocTestRepository {

	companion object {

		private const val AGGREGATE_TYPE_ID = "doc_test"
	}

}
