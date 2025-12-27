package dddrive.app.doc.model.impl

import dddrive.app.doc.model.Doc
import dddrive.app.doc.model.DocPartTransition
import dddrive.app.doc.model.base.DocPartBase
import dddrive.app.doc.model.enums.CodeCaseStage
import dddrive.ddd.core.model.PartRepository
import dddrive.ddd.property.delegate.baseProperty
import dddrive.ddd.property.delegate.enumProperty
import dddrive.ddd.property.model.Property
import java.time.OffsetDateTime

class DocPartTransitionImpl(
	doc: Doc,
	override val repository: PartRepository<Doc, DocPartTransition>,
	property: Property<*>,
	id: Int,
) : DocPartBase<Doc>(doc, repository, property, id),
	DocPartTransition {

	// seqNr is the part id
	override val seqNr: Int get() = id

	private var _tenantId: Any? by baseProperty(this, "tenantId")
	private var _userId: Any? by baseProperty(this, "userId")
	override val userId: Any get() = _userId!!

	private var _timestamp: OffsetDateTime? by baseProperty(this, "timestamp")
	override val timestamp: OffsetDateTime get() = _timestamp!!

	private var _oldCaseStage: CodeCaseStage? by enumProperty(this, "oldCaseStage")
	override val oldCaseStage: CodeCaseStage? get() = _oldCaseStage

	private var _newCaseStage: CodeCaseStage? by enumProperty(this, "newCaseStage")
	override val newCaseStage: CodeCaseStage get() = _newCaseStage!!

	override fun doAfterCreate() {
		super.doAfterCreate()
		_tenantId = aggregate.tenantId
	}

	override fun init(
		userId: Any,
		timestamp: OffsetDateTime,
		oldCaseStage: CodeCaseStage?,
		caseStage: CodeCaseStage,
	) {
		_userId = userId
		_timestamp = timestamp
		_oldCaseStage = oldCaseStage
		_newCaseStage = caseStage
	}

}
