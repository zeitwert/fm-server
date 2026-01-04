package dddrive.app.ddd.model.base

import dddrive.app.ddd.model.Aggregate
import dddrive.app.ddd.model.AggregateMeta
import dddrive.app.ddd.model.AggregateSPI
import dddrive.app.ddd.model.SessionContext
import dddrive.app.obj.model.Obj
import dddrive.app.validation.model.AggregatePartValidation
import dddrive.app.validation.model.enums.CodeValidationLevel
import dddrive.app.validation.model.impl.AggregatePartValidationImpl
import dddrive.ddd.core.model.AggregateRepository
import dddrive.ddd.core.model.Entity
import dddrive.ddd.core.model.base.AggregateBase
import dddrive.ddd.path.relativePath
import dddrive.ddd.property.delegate.baseProperty
import dddrive.ddd.property.delegate.referenceIdProperty
import dddrive.ddd.property.model.Property
import java.time.OffsetDateTime

abstract class AggregateBase(
	override val repository: AggregateRepository<out Aggregate>,
	isNew: Boolean,
) : AggregateBase(repository, isNew),
	AggregateSPI,
	Aggregate,
	AggregateMeta {

	override val meta: AggregateMeta get() = this

	protected var _tenantId by referenceIdProperty<Obj>("tenant")
	override val tenantId get() = _tenantId!!

	override var ownerId by referenceIdProperty<Obj>("owner")

	protected var _caption by baseProperty<String>("caption")
	override val caption get() = _caption ?: ""

	protected var _createdByUserId by referenceIdProperty<Obj>("createdByUser")
	override val createdByUserId get() = _createdByUserId!!
	protected var _createdAt by baseProperty<OffsetDateTime>("createdAt")
	override val createdAt get() = _createdAt!!

	override var modifiedByUserId by referenceIdProperty<Obj>("modifiedByUser")
	override var modifiedAt by baseProperty<OffsetDateTime>("modifiedAt")

	override val validationList: MutableList<AggregatePartValidation> = mutableListOf()

	override fun doAfterCreate(sessionContext: SessionContext) {
		_tenantId = sessionContext.tenantId
		ownerId = sessionContext.userId
		_createdByUserId = sessionContext.userId
		_createdAt = sessionContext.currentTime
	}

	override fun doBeforeStore(sessionContext: SessionContext) {
		try {
			disableCalc()
			_version = version + 1
			modifiedByUserId = sessionContext.userId
			modifiedAt = sessionContext.currentTime
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
		entity: Entity<*>,
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
