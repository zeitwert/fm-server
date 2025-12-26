package io.dddrive.doc.model.impl

import io.dddrive.ddd.model.PartRepository
import io.dddrive.doc.model.Doc
import io.dddrive.doc.model.DocPartTransition
import io.dddrive.doc.model.base.DocPartBase
import io.dddrive.doc.model.enums.CodeCaseStage
import io.dddrive.oe.model.ObjUser
import io.dddrive.property.delegate.baseProperty
import io.dddrive.property.delegate.enumProperty
import io.dddrive.property.delegate.referenceIdProperty
import io.dddrive.property.delegate.referenceProperty
import io.dddrive.property.model.Property
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

	// Private mutable backing for read-only interface properties
	private var _tenantId: Any? by baseProperty(this, "tenantId")
	private var _user: ObjUser? by referenceProperty(this, "user")
	private var _userId: Any? by referenceIdProperty<ObjUser>(this, "user")
	private var _timestamp: OffsetDateTime? by baseProperty(this, "timestamp")
	private var _oldCaseStage: CodeCaseStage? by enumProperty(this, "oldCaseStage")
	private var _newCaseStage: CodeCaseStage? by enumProperty(this, "newCaseStage")

	override val user: ObjUser get() = _user!!
	override val timestamp: OffsetDateTime get() = _timestamp!!
	override val oldCaseStage: CodeCaseStage? get() = _oldCaseStage
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
