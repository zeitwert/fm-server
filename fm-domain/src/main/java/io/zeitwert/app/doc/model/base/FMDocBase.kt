package io.zeitwert.app.doc.model.base

import dddrive.app.ddd.model.SessionContext
import dddrive.app.doc.model.Doc
import dddrive.app.doc.model.DocRepository
import dddrive.app.doc.model.base.DocBase
import dddrive.property.delegate.referenceIdProperty
import io.zeitwert.app.ddd.model.EntityWithExtn
import io.zeitwert.fm.account.model.ItemWithAccount
import io.zeitwert.fm.account.model.ObjAccount

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
	override val repository: DocRepository<out Doc>,
	isNew: Boolean,
) : DocBase(repository, isNew),
	EntityWithExtn {

	var accountId by referenceIdProperty<ObjAccount>("account")

	private val _extnMap = mutableMapOf<String, Any>()

	override fun doAfterCreate(sessionContext: SessionContext) {
		super.doAfterCreate(sessionContext)
		// !hasAccount is possible for modifications in kernel tenant (f.ex. to create avatar image for user)
		if (this is ItemWithAccount && (sessionContext as io.zeitwert.app.session.model.SessionContext).hasAccount()) {
			this.accountId = sessionContext.accountId!!
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
