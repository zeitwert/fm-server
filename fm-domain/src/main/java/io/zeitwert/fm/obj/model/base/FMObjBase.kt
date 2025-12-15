package io.zeitwert.fm.obj.model.base

import io.dddrive.core.obj.model.Obj
import io.dddrive.core.obj.model.ObjRepository
import io.dddrive.core.obj.model.base.ObjBase
import io.dddrive.core.property.model.BaseProperty
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
	repository: ObjRepository<out Obj>,
	val isNew: Boolean,
) : ObjBase(repository),
	EntityWithExtn {

	// @formatter:off
	private val _accountId: BaseProperty<Int> = this.addBaseProperty("accountId", Int::class.java)
	private val _extnMap = mutableMapOf<String, Any>()
	// @formatter:on

	/**
	 * Gets/sets the account ID associated with this Obj.
	 * Setting accountId also sets extnAccountId to the same value.
	 */
	var accountId: Int?
		get() = _accountId.value
		set(value) {
			_accountId.value = value
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
