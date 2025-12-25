package io.zeitwert.fm.test.model.impl

import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase
import io.zeitwert.fm.test.model.ObjTest
import io.zeitwert.fm.test.model.ObjTestPartNode
import io.zeitwert.fm.test.model.ObjTestRepository
import org.springframework.stereotype.Component

@Component("objTestRepository")
class ObjTestRepositoryImpl :
	FMObjRepositoryBase<ObjTest>(
		ObjTestRepository::class.java,
		ObjTest::class.java,
		ObjTestImpl::class.java,
		AGGREGATE_TYPE_ID,
	),
	ObjTestRepository {

	override fun registerParts() {
		this.addPart(ObjTest::class.java, ObjTestPartNode::class.java, ObjTestPartNodeImpl::class.java)
	}

	companion object {

		private const val AGGREGATE_TYPE_ID = "obj_test"
	}

}
