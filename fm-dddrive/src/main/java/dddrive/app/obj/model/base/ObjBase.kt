package dddrive.app.obj.model.base

import dddrive.app.obj.model.Obj
import dddrive.app.obj.model.ObjMeta
import dddrive.app.obj.model.ObjPartTransition
import dddrive.app.obj.model.ObjRepository
import dddrive.app.validation.model.AggregatePartValidation
import dddrive.app.validation.model.enums.CodeValidationLevel
import dddrive.app.validation.model.impl.AggregatePartValidationImpl
import dddrive.ddd.core.model.base.AggregateBase
import dddrive.ddd.property.delegate.baseProperty
import dddrive.ddd.property.delegate.partListProperty
import dddrive.ddd.property.model.EntityWithPropertiesSPI
import dddrive.ddd.property.model.PartListProperty
import dddrive.ddd.property.model.Property
import java.time.OffsetDateTime

abstract class ObjBase(
	override val repository: ObjRepository<out Obj>,
	isNew: Boolean,
) : AggregateBase(repository, isNew),
	Obj,
	ObjMeta {

	protected var _tenantId: Any? by baseProperty(this, "tenantId")
	override val tenantId: Any get() = _tenantId!!

	override var ownerId: Any? by baseProperty(this, "ownerId")

	protected var _createdByUserId: Any? by baseProperty(this, "createdByUserId")
	override val createdByUserId: Any get() = _createdByUserId!!
	protected var _createdAt: OffsetDateTime? by baseProperty(this, "createdAt")
	override val createdAt: OffsetDateTime get() = _createdAt!!

	override var modifiedByUserId: Any? by baseProperty(this, "modifiedByUserId")
	override var modifiedAt: OffsetDateTime? by baseProperty(this, "modifiedAt")

	override var closedByUserId: Any? by baseProperty(this, "closedByUserId")
	override var closedAt: OffsetDateTime? by baseProperty(this, "closedAt")

	protected var _caption: String? by baseProperty(this, "caption")
	override val caption: String get() = _caption ?: ""

	private val _transitionList: PartListProperty<ObjPartTransition> = partListProperty(this, "transitionList")
	override val transitionList: List<ObjPartTransition> get() = _transitionList.toList()

	override val validationList: MutableList<AggregatePartValidation> = mutableListOf()

	override val meta: ObjMeta
		get() = this

	override val objTypeId get() = repository.aggregateType.id

	override fun doCreate(
		aggregateId: Any,
		tenantId: Any,
	) {
		super.doCreate(aggregateId, tenantId)
		_tenantId = tenantId
	}

	override fun doAfterCreate(
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		ownerId = userId
		_createdByUserId = userId
		_createdAt = timestamp
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
	): dddrive.ddd.core.model.Part<*> {
		if (property === _transitionList) {
			return directory.getPartRepository(ObjPartTransition::class.java).create(this, property, partId)
		}
		return super.doAddPart(property, partId)
	}

	protected fun setCaption(caption: String?) {
		_caption = caption
	}

	override fun beginCalc() {
		super.beginCalc()
		clearValidationList()
	}

	private fun clearValidationList() {
		validationList.clear()
	}

	fun addValidation(
		validationLevel: CodeValidationLevel,
		validation: String,
		entity: EntityWithPropertiesSPI,
	) {
		addValidation(validationLevel, validation, entity.relativePath)
	}

	fun addValidation(
		validationLevel: CodeValidationLevel,
		validation: String,
		property: Property<*>,
	) {
		addValidation(validationLevel, validation, property.relativePath)
	}

	fun addValidation(
		validationLevel: CodeValidationLevel,
		validation: String,
		path: String? = null,
	) {
		validationList.add(AggregatePartValidationImpl(validationList.size, validationLevel, validation, path))
	}

	override fun toString(): String = caption

}
