package dddrive.app.ddd.model.base

import dddrive.app.ddd.model.Aggregate
import dddrive.app.ddd.model.AggregateMeta
import dddrive.app.ddd.model.AggregateSPI
import dddrive.app.ddd.model.SessionContext
import dddrive.app.validation.model.AggregatePartValidation
import dddrive.app.validation.model.enums.CodeValidationLevel
import dddrive.app.validation.model.impl.AggregatePartValidationImpl
import dddrive.ddd.core.model.AggregateRepository
import dddrive.ddd.core.model.base.AggregateBase
import dddrive.ddd.path.relativePath
import dddrive.ddd.property.delegate.baseProperty
import dddrive.ddd.property.model.EntityWithProperties
import dddrive.ddd.property.model.Property
import java.time.OffsetDateTime

abstract class AggregateBase(
	override val repository: AggregateRepository<out Aggregate>,
	isNew: Boolean,
) : AggregateBase(repository, isNew),
	AggregateSPI,
	Aggregate,
	AggregateMeta {

	protected var _tenantId: Any? by baseProperty(this, "tenantId")
	override val tenantId: Any get() = _tenantId!!

	override var ownerId: Any? by baseProperty(this, "ownerId")

	protected var _caption: String? by baseProperty(this, "caption")
	override val caption: String get() = _caption ?: ""

	protected var _createdByUserId: Any? by baseProperty(this, "createdByUserId")
	override val createdByUserId: Any get() = _createdByUserId!!
	protected var _createdAt: OffsetDateTime? by baseProperty(this, "createdAt")
	override val createdAt: OffsetDateTime get() = _createdAt!!

	override var modifiedByUserId: Any? by baseProperty(this, "modifiedByUserId")
	override var modifiedAt: OffsetDateTime? by baseProperty(this, "modifiedAt")

	override val validationList: MutableList<AggregatePartValidation> = mutableListOf()

	override fun doAfterCreate(sessionContext: SessionContext) {
		_tenantId = sessionContext.tenantId
		ownerId = sessionContext.userId
		_createdByUserId = sessionContext.userId
		_createdAt = sessionContext.timestamp
	}

	override fun doBeforeStore(sessionContext: SessionContext) {
		try {
			disableCalc()
			_version = version + 1
			modifiedByUserId = sessionContext.userId
			modifiedAt = sessionContext.timestamp
		} finally {
			enableCalc()
		}
	}

	override fun beginCalc() {
		super.beginCalc()
		clearValidationList()
	}

	protected fun setCaption(caption: String?) {
		_caption = caption
	}

	private fun clearValidationList() {
		validationList.clear()
	}

	fun addValidation(
		validationLevel: CodeValidationLevel,
		validation: String,
		entity: EntityWithProperties,
	) {
		addValidation(validationLevel, validation, entity.relativePath())
	}

	fun addValidation(
		validationLevel: CodeValidationLevel,
		validation: String,
		property: Property<*>,
	) {
		addValidation(validationLevel, validation, property.relativePath())
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
