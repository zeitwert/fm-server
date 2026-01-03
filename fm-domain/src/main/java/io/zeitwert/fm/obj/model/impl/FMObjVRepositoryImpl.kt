package io.zeitwert.fm.obj.model.impl

import dddrive.app.obj.model.Obj
import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.fm.obj.model.FMObjVRepository
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase
import org.springframework.stereotype.Component

@Component("objRepository")
class FMObjVRepositoryImpl(
	override val sessionContext: SessionContext,
) : FMObjRepositoryBase<Obj>(
		Obj::class.java,
		AGGREGATE_TYPE_ID,
	),
	FMObjVRepository {

	override fun createAggregate(isNew: Boolean) = ObjVImpl(this, isNew)

	override fun create(): Obj = throw UnsupportedOperationException("this is a readonly repository")

	override fun load(id: Any): Obj = throw UnsupportedOperationException("this is a readonly repository")

	override fun store(aggregate: Obj) = throw UnsupportedOperationException("this is a readonly repository")

	companion object {

		private const val AGGREGATE_TYPE_ID = "obj"
	}

}
