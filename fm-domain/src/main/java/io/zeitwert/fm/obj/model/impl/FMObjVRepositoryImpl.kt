package io.zeitwert.fm.obj.model.impl

import io.dddrive.obj.model.Obj
import io.zeitwert.fm.obj.model.FMObjVRepository
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component("objRepository")
class FMObjVRepositoryImpl :
	FMObjRepositoryBase<Obj>(
		Obj::class.java,
		AGGREGATE_TYPE_ID,
	),
	FMObjVRepository {

	override fun createAggregate(isNew: Boolean): Obj = throw UnsupportedOperationException("this is a readonly repository")

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
