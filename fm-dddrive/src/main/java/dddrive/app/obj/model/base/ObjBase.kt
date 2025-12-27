package dddrive.app.obj.model.base

import dddrive.app.obj.model.Obj
import dddrive.app.obj.model.ObjMeta
import dddrive.app.obj.model.ObjPartTransition
import dddrive.app.obj.model.ObjRepository
import dddrive.ddd.core.model.Part
import dddrive.ddd.core.model.base.AggregateBase
import dddrive.ddd.property.delegate.baseProperty
import dddrive.ddd.property.delegate.partListProperty
import dddrive.ddd.property.delegate.referenceIdProperty
import dddrive.ddd.property.delegate.referenceProperty
import dddrive.ddd.property.model.PartListProperty
import dddrive.ddd.property.model.Property
import io.dddrive.oe.model.ObjUser
import java.time.OffsetDateTime

abstract class ObjBase(
	override val repository: ObjRepository<out Obj>,
	isNew: Boolean,
) : dddrive.ddd.core.model.base.AggregateBase(repository, isNew),
	Obj,
	ObjMeta {

	var closedByUserId: Any? by _root_ide_package_.dddrive.ddd.property.delegate.referenceIdProperty<ObjUser>(
		this,
		"closedByUser",
	)
	override var closedByUser: ObjUser? by _root_ide_package_.dddrive.ddd.property.delegate.referenceProperty(
		this,
		"closedByUser",
	)
	override var closedAt: OffsetDateTime? by _root_ide_package_.dddrive.ddd.property.delegate.baseProperty(
		this,
		"closedAt",
	)

	private val _transitionList: dddrive.ddd.property.model.PartListProperty<ObjPartTransition> =
		_root_ide_package_.dddrive.ddd.property.delegate
			.partListProperty(this, "transitionList")
	override val transitionList: List<ObjPartTransition> get() = _transitionList.toList()

	override val meta: ObjMeta
		get() = this

	override val objTypeId get() = repository.aggregateType.id

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
		property: dddrive.ddd.property.model.Property<*>,
		partId: Int?,
	): dddrive.ddd.core.model.Part<*> {
		if (property === _transitionList) {
			return directory.getPartRepository(ObjPartTransition::class.java).create(this, property, partId)
		}
		return super.doAddPart(property, partId)
	}

	protected fun setCaption(caption: String?) {
		_caption = caption
	}

}
