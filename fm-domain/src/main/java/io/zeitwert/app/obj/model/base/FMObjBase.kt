package io.zeitwert.app.obj.model.base

import dddrive.app.ddd.model.SessionContext
import dddrive.app.obj.model.Obj
import dddrive.app.obj.model.ObjRepository
import dddrive.app.obj.model.base.ObjBase
import dddrive.property.delegate.referenceIdProperty
import dddrive.property.model.Property
import io.zeitwert.app.ddd.model.EntityWithExtn
import io.zeitwert.fm.account.model.ItemWithAccount

/**
 * Base class for FM Objects.
 *
 * This class extends dddrive.ObjBase and adds FM-specific functionality:
 * - accountId property for account association
 * - Extension map support via EntityWithExtn interface
 * - Note repository access for AggregateWithNotesMixin support
 *
 * @param repository The repository for managing this Obj
 */
abstract class FMObjBase(
	override val repository: ObjRepository<out Obj>,
	isNew: Boolean,
) : ObjBase(repository, isNew),
	EntityWithExtn {

	var accountId by referenceIdProperty<Obj>("account")

	private val _extnMap = mutableMapOf<String, Any>()

	override fun doAfterCreate(sessionContext: SessionContext) {
		super.doAfterCreate(sessionContext)
		// !hasAccount is possible for modifications in kernel tenant (f.ex. to create avatar image for user)
		if (this is ItemWithAccount && (sessionContext as io.zeitwert.app.session.model.SessionContext).hasAccount()) {
			this.accountId = sessionContext.accountId!!
		}
	}

	override fun doBeforeSet(
		property: Property<*>,
		value: Any?,
		oldValue: Any?,
	) {
		super.doAfterSet(property, value, oldValue)
		if (property.name == "accountId") {
			require(value == null || value is Int) { "accountId is Int" }
		}
	}

	// EntityWithExtn implementation
	override val extnMap: Map<String, Any> = _extnMap

	override fun hasExtn(key: String): Boolean = _extnMap.containsKey(key)

	override fun getExtn(key: String): Any? = _extnMap.get(key)

	override fun setExtn(
		key: String,
		value: Any?,
	) {
		if (value == null) {
			_extnMap.remove(key)
		} else {
			_extnMap[key] = value
		}
	}

}
