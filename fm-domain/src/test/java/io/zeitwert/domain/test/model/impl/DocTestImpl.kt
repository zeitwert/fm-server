package io.zeitwert.domain.test.model.impl

import dddrive.ddd.property.delegate.baseProperty
import dddrive.ddd.property.delegate.enumProperty
import dddrive.ddd.property.delegate.enumSetProperty
import dddrive.ddd.property.delegate.referenceIdProperty
import dddrive.ddd.property.delegate.referenceProperty
import dddrive.ddd.property.model.EnumSetProperty
import io.zeitwert.domain.test.model.DocTest
import io.zeitwert.domain.test.model.DocTestRepository
import io.zeitwert.domain.test.model.ObjTest
import io.zeitwert.domain.test.model.enums.CodeTestType
import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.fm.collaboration.model.ObjNoteRepository
import io.zeitwert.fm.collaboration.model.impl.AggregateWithNotesMixin
import io.zeitwert.fm.doc.model.base.FMDocBase
import org.jooq.JSON
import java.math.BigDecimal
import java.time.LocalDate

class DocTestImpl(
	override val repository: DocTestRepository,
	isNew: Boolean,
) : FMDocBase(repository, isNew),
	DocTest,
	AggregateWithNotesMixin {

	override var shortText by baseProperty<String>("shortText")
	override var longText by baseProperty<String>("longText")
	override var date by baseProperty<LocalDate>("date")
	override var int by baseProperty<Int>("integer")
	override var isDone by baseProperty<Boolean>("isDone")
	private var _json by baseProperty<JSON>("json")
	override var json: String?
		get() = _json?.data()
		set(value) {
			_json = if (value != null) JSON.json(value) else null
		}
	override var nr by baseProperty<BigDecimal>("nr")
	override var testType by enumProperty<CodeTestType>("testType")
	override var refObj by referenceProperty<ObjTest>("refObj")
	override var refObjId by referenceIdProperty<ObjTest>("refObj")
	override var refDoc by referenceProperty<DocTest>("refDoc")
	override var refDocId by referenceIdProperty<DocTest>("refDoc")
	override val testTypeSet: EnumSetProperty<CodeTestType> = enumSetProperty("testTypeSet")

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
