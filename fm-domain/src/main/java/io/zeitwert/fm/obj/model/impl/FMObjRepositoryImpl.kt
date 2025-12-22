package io.zeitwert.fm.obj.model.impl

import io.dddrive.obj.model.Obj
import io.dddrive.obj.model.base.ObjBase
import io.dddrive.obj.model.base.ObjRepositoryBase
import io.zeitwert.fm.obj.model.FMObjRepository
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component("objRepository")
class FMObjRepositoryImpl :
	ObjRepositoryBase<Obj>(
		FMObjRepository::class.java,
		Obj::class.java,
		ObjBase::class.java,
		AGGREGATE_TYPE_ID,
	),
	FMObjRepository {

	override fun create(
		tenantId: Any,
		userId: Any,
		timestamp: OffsetDateTime,
	): Obj = throw UnsupportedOperationException("this is a readonly repository")

	override fun load(id: Any): Obj = throw UnsupportedOperationException("this is a readonly repository")

	override fun store(
		aggregate: Obj,
		userId: Any,
		timestamp: OffsetDateTime,
	) = throw UnsupportedOperationException("this is a readonly repository")

	companion object {

		private const val AGGREGATE_TYPE_ID = "obj"
	}
}
