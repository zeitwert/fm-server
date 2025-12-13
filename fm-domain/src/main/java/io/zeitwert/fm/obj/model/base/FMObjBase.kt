package io.zeitwert.fm.obj.model.base

import io.dddrive.core.obj.model.Obj
import io.dddrive.core.obj.model.ObjRepository
import io.dddrive.core.obj.model.base.ObjBase
import io.dddrive.core.property.model.BaseProperty
import io.zeitwert.fm.obj.model.EntityWithExtn

/**
 * Base class for FM Obj entities using the NEW dddrive framework (io.dddrive.core.*).
 *
 * This class extends the new dddrive ObjBase and adds FM-specific functionality:
 * - accountId and extnAccountId properties for account association
 * - Extension map support via EntityWithExtn interface
 * - Note repository access for AggregateWithNotesMixin support
 *
 * This class coexists with the old FMObjBase (which extends io.dddrive.obj.model.base.ObjExtnBase)
 * to allow gradual migration of domain entities from old to new dddrive.
 *
 * Usage during migration:
 * - New entities should extend FMObjCoreBase
 * - Old entities remain on FMObjBase until migrated
 * - Both can coexist in the same application
 *
 * @param repository The repository for managing this Obj
 */
abstract class FMObjBase(
	repository: ObjRepository<out Obj>
) : ObjBase(repository), EntityWithExtn {

	//@formatter:off
	private val _accountId: BaseProperty<Int> = this.addBaseProperty("accountId", Int::class.java)
	private val _extnAccountId: BaseProperty<Int> = this.addBaseProperty("extnAccountId", Int::class.java)
	@Suppress("UNCHECKED_CAST", "ktlint")
	private val _extnMap: BaseProperty<MutableMap<String, Any>> = this.addBaseProperty("extnMap", MutableMap::class.java as Class<MutableMap<String, Any>>)
	//@formatter:on

	/**
	 * Gets/sets the account ID associated with this Obj.
	 * Setting accountId also sets extnAccountId to the same value.
	 */
	var accountId: Int?
		get() = _accountId.value
		set(value) {
			_accountId.value = value
			_extnAccountId.value = value
		}

	/**
	 * Gets the extension account ID (mirrors accountId for extension table storage).
	 */
	val extnAccountId: Int?
		get() = _extnAccountId.value

	// EntityWithExtn implementation
	override var extnMap: Map<String, Any>?
		get() = _extnMap.value
		set(value) {
			_extnMap.value = value?.toMutableMap()
		}

	override fun hasExtn(key: String): Boolean = _extnMap.value?.containsKey(key) == true

	override fun getExtn(key: String): Any = _extnMap.value?.get(key)
		?: throw NoSuchElementException("Extension key not found: $key")

	override fun setExtn(key: String, value: Any) {
		var map = _extnMap.value
		if (map == null) {
			map = mutableMapOf()
		}
		map[key] = value
		_extnMap.value = map
	}

	override fun removeExtn(key: String) {
		val map = _extnMap.value
		if (map != null) {
			map.remove(key)
			_extnMap.value = if (map.isEmpty()) null else map
		}
	}

}
