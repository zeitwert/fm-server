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
	override val repository: DocTestRepository,
	isNew: Boolean,
) : FMDocBase(repository, isNew),
	DocTest,
	AggregateWithNotesMixin {

	override fun noteRepository() = directory.getRepository(ObjNote::class.java) as ObjNoteRepository

	fun accountRepository() = directory.getRepository(ObjAccount::class.java)

	override fun aggregate(): DocTest = this

	override fun doInit() {
		super.doInit()
		addBaseProperty("shortText", String::class.java)
		addBaseProperty("longText", String::class.java)
		addBaseProperty("date", LocalDate::class.java)
		addBaseProperty("int", Int::class.java)
		addBaseProperty("isDone", Boolean::class.java)
		addBaseProperty("json", JSON::class.java)
		addBaseProperty("nr", BigDecimal::class.java)
		addEnumProperty("testType", CodeTestType::class.java)
		addReferenceProperty("refObj", ObjTest::class.java)
		addReferenceProperty("refDoc", DocTest::class.java)
		addEnumSetProperty("testTypeSet", CodeTestType::class.java)
	}

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
