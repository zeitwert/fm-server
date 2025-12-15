package io.zeitwert.fm.test.model.impl

import io.dddrive.core.ddd.model.AggregatePersistenceProvider
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase
import io.zeitwert.fm.test.model.ObjTest
import io.zeitwert.fm.test.model.ObjTestPartNode
import io.zeitwert.fm.test.model.ObjTestRepository
import io.zeitwert.fm.test.model.base.ObjTestBase
import io.zeitwert.fm.test.model.base.ObjTestPartNodeBase
import io.zeitwert.fm.test.persist.ObjTestPersistenceProviderImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

/**
 * Repository implementation for ObjTest using the NEW dddrive framework.
 */
@Component("objTestRepository")
class ObjTestRepositoryImpl :
	FMObjRepositoryBase<ObjTest>(
		ObjTestRepository::class.java,
		ObjTest::class.java,
		ObjTestBase::class.java,
		AGGREGATE_TYPE_ID,
	),
	ObjTestRepository {

	private lateinit var persistenceProvider: ObjTestPersistenceProviderImpl

	@Autowired
	@Lazy
	fun setPersistenceProvider(persistenceProvider: ObjTestPersistenceProviderImpl) {
		this.persistenceProvider = persistenceProvider
	}

	override fun getPersistenceProvider(): AggregatePersistenceProvider<ObjTest> = persistenceProvider

	override fun registerParts() {
		this.addPart(ObjTest::class.java, ObjTestPartNode::class.java, ObjTestPartNodeBase::class.java)
	}

	companion object {

		private const val AGGREGATE_TYPE_ID = "obj_test"
	}

}
