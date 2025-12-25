package io.zeitwert.fm.obj.model.base

import io.dddrive.obj.model.Obj
import io.dddrive.obj.model.ObjRepository
import io.dddrive.obj.model.base.ObjBase
import io.dddrive.property.delegate.baseProperty
import io.zeitwert.fm.ddd.model.EntityWithExtn

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

	private val _extnMap = mutableMapOf<String, Any>()

	// Delegated property for account association
	open var accountId: Any? by baseProperty()

	@Suppress("UNUSED_EXPRESSION")
	override fun doInit() {
		super.doInit()
		accountId // trigger delegate initialization
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
