package io.zeitwert.fm.test.model.impl

import io.zeitwert.fm.app.model.SessionContextFM
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase
import io.zeitwert.fm.test.model.ObjTest
import io.zeitwert.fm.test.model.ObjTestPartNode
import io.zeitwert.fm.test.model.ObjTestRepository
import org.springframework.stereotype.Component

@Component("objTestRepository")
class ObjTestRepositoryImpl(
	override val requestCtx: SessionContextFM,
) : FMObjRepositoryBase<ObjTest>(
		ObjTest::class.java,
		AGGREGATE_TYPE_ID,
	),
	ObjTestRepository {

	override fun createAggregate(isNew: Boolean): ObjTest = ObjTestImpl(this, isNew)

	override fun registerParts() {
		this.addPart(ObjTestPartNode::class.java, ::ObjTestPartNodeImpl)
	}

	companion object {

		private const val AGGREGATE_TYPE_ID = "obj_test"
	}

}
