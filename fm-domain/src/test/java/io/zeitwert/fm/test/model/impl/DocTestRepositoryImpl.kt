package io.zeitwert.fm.test.model.impl

import io.dddrive.core.ddd.model.AggregatePersistenceProvider
import io.zeitwert.fm.doc.model.base.FMDocRepositoryBase
import io.zeitwert.fm.test.model.DocTest
import io.zeitwert.fm.test.model.DocTestRepository
import io.zeitwert.fm.test.model.base.DocTestBase
import io.zeitwert.fm.test.persist.jooq.DocTestPersistenceProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

/**
 * Repository implementation for DocTest using the NEW dddrive framework.
 */
@Component("docTestRepository")
class DocTestRepositoryImpl() : FMDocRepositoryBase<DocTest>(
	DocTestRepository::class.java,
	DocTest::class.java,
	DocTestBase::class.java,
	AGGREGATE_TYPE_ID
), DocTestRepository {

	private lateinit var persistenceProvider: DocTestPersistenceProvider

	@Autowired
	@Lazy
	fun setPersistenceProvider(persistenceProvider: DocTestPersistenceProvider) {
		this.persistenceProvider = persistenceProvider
	}

	override fun getPersistenceProvider(): AggregatePersistenceProvider<DocTest> = persistenceProvider

	companion object {
		private const val AGGREGATE_TYPE_ID = "doc_test"
	}

}

