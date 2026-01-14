package io.domain.test.model.impl

import io.domain.test.model.DocTest
import io.domain.test.model.DocTestRepository
import io.zeitwert.app.doc.model.base.FMDocRepositoryBase
import io.zeitwert.app.session.model.SessionContext
import org.springframework.stereotype.Component

@Component("docTestRepository")
class DocTestRepositoryImpl(
	override val sessionContext: SessionContext,
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
