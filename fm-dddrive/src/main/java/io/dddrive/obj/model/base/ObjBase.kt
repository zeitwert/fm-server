package io.dddrive.obj.model.base

import io.dddrive.ddd.model.Part
import io.dddrive.ddd.model.base.AggregateBase
import io.dddrive.obj.model.Obj
import io.dddrive.obj.model.ObjMeta
import io.dddrive.obj.model.ObjPartTransition
import io.dddrive.obj.model.ObjRepository
import io.dddrive.oe.model.ObjUser
import io.dddrive.property.delegate.baseProperty
import io.dddrive.property.delegate.partListProperty
import io.dddrive.property.delegate.referenceIdProperty
import io.dddrive.property.delegate.referenceProperty
import io.dddrive.property.model.PartListProperty
import io.dddrive.property.model.Property
import java.time.OffsetDateTime

abstract class ObjBase(
	override val repository: ObjRepository<out Obj>,
	isNew: Boolean,
) : AggregateBase(repository, isNew),
	Obj,
	ObjMeta {

	// ============================================================================
	// Delegated properties (ObjMeta interface)
	// ============================================================================

	var closedByUserId: Any? by referenceIdProperty<ObjUser>()
	override var closedByUser: ObjUser? by referenceProperty()

	override var closedAt: OffsetDateTime? by baseProperty()

	private val _transitionList: PartListProperty<ObjPartTransition> by partListProperty()
	override val transitionList: List<ObjPartTransition> get() = _transitionList.toList()

	// ============================================================================

	override val meta: ObjMeta
		get() = this

	override val objTypeId get() = repository.aggregateType.id

	// Trigger delegate initialization to register properties before persistence layer access.
	@Suppress("UNUSED_EXPRESSION")
	override fun doInit() {
		super.doInit()
		closedByUserId
		closedByUser
		closedAt
		_transitionList
	}

	override fun doAfterCreate(
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		super.doAfterCreate(userId, timestamp)
		try {
			disableCalc()
			_transitionList.add(null).init(userId, timestamp)
		} finally {
			enableCalc()
		}
	}

	override fun doBeforeStore(
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		_transitionList.add(null).init(userId, timestamp)

		super.doBeforeStore(userId, timestamp)

		try {
			disableCalc()
			_version = version + 1
			modifiedByUserId = userId
			modifiedAt = timestamp
		} finally {
			enableCalc()
		}
	}

	override fun delete(
		userId: Any?,
		timestamp: OffsetDateTime?,
	) {
		closedByUserId = userId
		closedAt = timestamp
	}

	override fun doAddPart(
		property: Property<*>,
		partId: Int?,
	): Part<*> {
		if (property === _transitionList) {
			return directory
				.getPartRepository(ObjPartTransition::class.java)
				.create(this, property, partId)
		}
		return super.doAddPart(property, partId)
	}

	protected fun setCaption(caption: String?) {
		_caption = caption
	}

}
