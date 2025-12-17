package io.dddrive.core.doc.model.base

import io.dddrive.core.ddd.model.PartRepository
import io.dddrive.core.doc.model.Doc
import io.dddrive.core.doc.model.DocPartTransition
import io.dddrive.core.doc.model.enums.CodeCaseStage
import io.dddrive.core.oe.model.ObjUser
import io.dddrive.core.property.model.Property
import java.time.OffsetDateTime

abstract class DocPartTransitionBase(
	doc: Doc,
	repository: PartRepository<Doc, DocPartTransition>,
	property: Property<*>,
	id: Int,
) : DocPartBase<Doc>(doc, repository, property, id),
	DocPartTransition {

	// @formatter:off
	protected val _tenantId = this.addBaseProperty("tenantId", Any::class.java)
	protected val _user = this.addReferenceProperty("user", ObjUser::class.java)
	protected val _timestamp = this.addBaseProperty("timestamp", OffsetDateTime::class.java)
	protected val _oldCaseStage = this.addEnumProperty("oldCaseStage", CodeCaseStage::class.java)
	protected val _newCaseStage = this.addEnumProperty("newCaseStage", CodeCaseStage::class.java)
	// @formatter:on

	@Suppress("UNCHECKED_CAST")
	override val repository: PartRepository<Doc, DocPartTransition>
		get() = super.repository as PartRepository<Doc, DocPartTransition>

	override fun doAfterCreate() {
		this._tenantId.value = this.aggregate.tenantId
	}

	override fun init(
		userId: Any,
		timestamp: OffsetDateTime,
		oldCaseStage: CodeCaseStage?,
		caseStage: CodeCaseStage,
	) {
		_user.id = userId
		_timestamp.value = timestamp
		_oldCaseStage.value = oldCaseStage
		_newCaseStage.value = caseStage
	}

}
