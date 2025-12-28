package dddrive.app.obj.model.base

import dddrive.app.ddd.model.AggregateSPI
import dddrive.app.ddd.model.SessionContext
import dddrive.app.ddd.model.base.AggregateBase
import dddrive.app.obj.model.Obj
import dddrive.app.obj.model.ObjMeta
import dddrive.app.obj.model.ObjPartTransition
import dddrive.app.obj.model.ObjRepository
import dddrive.ddd.property.delegate.baseProperty
import dddrive.ddd.property.delegate.partListProperty
import dddrive.ddd.property.model.PartListProperty
import dddrive.ddd.property.model.Property
import java.time.OffsetDateTime

abstract class ObjBase(
	override val repository: ObjRepository<out Obj>,
	isNew: Boolean,
) : AggregateBase(repository, isNew),
	AggregateSPI,
	Obj,
	ObjMeta {

	override var closedByUserId: Any? by baseProperty(this, "closedByUserId")
	override var closedAt: OffsetDateTime? by baseProperty(this, "closedAt")

	private val _transitionList: PartListProperty<Obj, ObjPartTransition> = partListProperty(this, "transitionList")
	override val transitionList: List<ObjPartTransition> get() = _transitionList.toList()

	override val meta: ObjMeta
		get() = this

	override val objTypeId get() = repository.aggregateType.id

	override fun doAfterCreate(sessionContext: SessionContext) {
		super.doAfterCreate(sessionContext)
		try {
			disableCalc()
			_transitionList.add(null).init(sessionContext.userId, sessionContext.timestamp)
		} finally {
			enableCalc()
		}
	}

	override fun doBeforeStore(sessionContext: SessionContext) {
		super.doBeforeStore(sessionContext)
		_transitionList.add(null).init(sessionContext.userId, sessionContext.timestamp)
	}

	override fun delete(
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		closedByUserId = userId
		closedAt = timestamp
	}

	override fun doAddPart(
		property: Property<*>,
		partId: Int?,
	): dddrive.ddd.core.model.Part<*> {
		if (property === _transitionList) {
			return directory.getPartRepository(ObjPartTransition::class.java).create(this, property, partId)
		}
		return super.doAddPart(property, partId)
	}

}
