package io.zeitwert.fm.test.model.impl

import io.dddrive.property.delegate.baseProperty
import io.dddrive.property.delegate.enumProperty
import io.dddrive.property.delegate.enumSetProperty
import io.dddrive.property.delegate.referenceIdProperty
import io.dddrive.property.delegate.referenceProperty
import io.dddrive.property.model.EnumSetProperty
import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.fm.collaboration.model.ObjNoteRepository
import io.zeitwert.fm.collaboration.model.impl.AggregateWithNotesMixin
import io.zeitwert.fm.doc.model.base.FMDocBase
import io.zeitwert.fm.test.model.DocTest
import io.zeitwert.fm.test.model.DocTestRepository
import io.zeitwert.fm.test.model.ObjTest
import io.zeitwert.fm.test.model.enums.CodeTestType
import org.jooq.JSON
import java.math.BigDecimal
import java.time.LocalDate

open class DocTestImpl(
	override val repository: DocTestRepository,
	isNew: Boolean,
) : FMDocBase(repository, isNew),
	DocTest,
	AggregateWithNotesMixin {

	// Simple base properties
	override var shortText: String? by baseProperty()
	override var longText: String? by baseProperty()
	override var date: LocalDate? by baseProperty()
	override var int: Int? by baseProperty()
	override var isDone: Boolean? by baseProperty()
	private var _json: JSON? by baseProperty()
	override var json: String?
		get() = _json?.data()
		set(value) {
			_json = if (value != null) JSON.json(value) else null
		}
	override var nr: BigDecimal? by baseProperty()

	// Enum property
	override var testType: CodeTestType? by enumProperty()

	// Reference properties
	override var refObjId: Any? by referenceIdProperty<ObjTest>()
	override var refObj: ObjTest? by referenceProperty()
	override var refDocId: Any? by referenceIdProperty<DocTest>()
	override var refDoc: DocTest? by referenceProperty()

	// Enum set property
	override val testTypeSet: EnumSetProperty<CodeTestType> by enumSetProperty()

	override fun noteRepository() = directory.getRepository(ObjNote::class.java) as ObjNoteRepository

	private fun accountRepository() = directory.getRepository(ObjAccount::class.java)

	override fun aggregate(): DocTest = this

	override fun doCalcAll() {
		super.doCalcAll()
		this.calcCaption()
	}

	private fun calcCaption() {
		val shortTextStr = shortText ?: ""
		val longTextStr = longText ?: ""
		val refObjSuffix = if (refObjId == null) "" else " (RefObj:${refObj!!.caption})"
		val refDocSuffix = if (refDocId == null) "" else " (RefDoc:${refDoc!!.caption})"
		setCaption("[$shortTextStr, $longTextStr]$refObjSuffix$refDocSuffix")
	}

	override val account get() = if (accountId != null) accountRepository().get(accountId!!) else null

}
