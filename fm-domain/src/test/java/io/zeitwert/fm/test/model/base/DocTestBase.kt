package io.zeitwert.fm.test.model.base

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

abstract class DocTestBase(
	repository: DocTestRepository,
	isNew: Boolean,
) : FMDocBase(repository, isNew),
	DocTest,
	AggregateWithNotesMixin {

	private val _shortText = addBaseProperty("shortText", String::class.java)
	private val _longText = addBaseProperty("longText", String::class.java)
	private val _date = addBaseProperty("date", LocalDate::class.java)
	private val _int = addBaseProperty("int", Int::class.java)
	private val _isDone = addBaseProperty("isDone", Boolean::class.java)
	private val _json = addBaseProperty("json", JSON::class.java)
	private val _nr = addBaseProperty("nr", BigDecimal::class.java)
	private val _testType = addEnumProperty("testType", CodeTestType::class.java)
	private val _refObj = addReferenceProperty("refObj", ObjTest::class.java)
	private val _refDoc = addReferenceProperty("refDoc", DocTest::class.java)
	private val _testTypeSet = addEnumSetProperty("testTypeSet", CodeTestType::class.java)

	override val repository get() = super.repository as DocTestRepository

	override fun noteRepository() = directory.getRepository(ObjNote::class.java) as ObjNoteRepository

	fun accountRepository() = directory.getRepository(ObjAccount::class.java)

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
		this._caption.value = "[$shortTextStr, $longTextStr]$refObjSuffix$refDocSuffix"
	}

	override val account get() = if (accountId != null) accountRepository().get(accountId!!) else null

}
