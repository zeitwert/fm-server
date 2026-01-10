package io.zeitwert.domain.test.model.impl

import io.zeitwert.app.obj.model.base.FMObjRepositoryBase
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.domain.test.model.ObjTest
import io.zeitwert.domain.test.model.ObjTestPartNode
import io.zeitwert.domain.test.model.ObjTestRepository
import org.springframework.stereotype.Component

@Component("objTestRepository")
class ObjTestRepositoryImpl(
	override val sessionContext: SessionContext,
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
