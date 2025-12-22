package io.dddrive.doc.model.base

import io.dddrive.ddd.model.PartRepository
import io.dddrive.doc.model.Doc
import io.dddrive.doc.model.DocPartTransition
import io.dddrive.doc.model.enums.CodeCaseStage
import io.dddrive.oe.model.ObjUser
import io.dddrive.property.model.Property
import java.time.OffsetDateTime

abstract class DocPartTransitionBase(
	doc: Doc,
	repository: PartRepository<Doc, DocPartTransition>,
	property: Property<*>,
	id: Int,
) : DocPartBase<Doc>(doc, repository, property, id),
	DocPartTransition {

	protected val _tenantId = this.addBaseProperty("tenantId", Any::class.java)
	protected val _user = this.addReferenceProperty("user", ObjUser::class.java)
	protected val _timestamp = this.addBaseProperty("timestamp", OffsetDateTime::class.java)
	protected val _oldCaseStage = this.addEnumProperty("oldCaseStage", CodeCaseStage::class.java)
	protected val _newCaseStage = this.addEnumProperty("newCaseStage", CodeCaseStage::class.java)

	@Suppress("UNCHECKED_CAST")
	override val repository get() = super.repository as PartRepository<Doc, DocPartTransition>

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
