package io.zeitwert.fm.doc.model.base

import io.dddrive.core.doc.model.Doc
import io.dddrive.core.doc.model.DocRepository
import io.dddrive.core.doc.model.base.DocBase
import io.dddrive.core.property.model.BaseProperty
import io.zeitwert.fm.ddd.model.EntityWithExtn

/**
 * Base class for FM Orders.
 *
 * This class extends dddrive.DocBase and adds FM-specific functionality:
 * - accountId property for account association
 * - Extension map support via EntityWithExtn interface
 *
 * @param repository The repository for managing this Doc
 */
abstract class FMDocBase(
	repository: DocRepository<out Doc>,
	val isNew: Boolean,
) : DocBase(repository),
	EntityWithExtn {

	// @formatter:off
	private val _accountId: BaseProperty<Int> = this.addBaseProperty("accountId", Int::class.java)
	private val _extnMap = mutableMapOf<String, Any>()
	// @formatter:on

	/**
	 * Gets/sets the account ID associated with this Doc.
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
