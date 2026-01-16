package io.zeitwert.a_smoke

import dddrive.app.doc.model.enums.CodeCaseStageEnum
import io.domain.test.model.DocTest
import io.domain.test.model.DocTestRepository
import io.domain.test.model.ObjTest
import io.domain.test.model.ObjTestRepository
import io.domain.test.model.enums.CodeTestType
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.data.config.TestDataSetup
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.test.TestApplication
import org.jooq.JSON
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.time.OffsetDateTime

@SpringBootTest(classes = [TestApplication::class])
@ActiveProfiles("test")
class DocTestTest {

	companion object {

		private const val TEST_JSON = "{ \"one\": \"one\", \"two\": 2 }"
		private const val TYPE_A = "type_a"
		private const val TYPE_B = "type_b"
		private const val TYPE_C = "type_c"
	}

	@Autowired
	lateinit var sessionContext: SessionContext

	@Autowired
	lateinit var docTestRepo: DocTestRepository

	@Autowired
	lateinit var objTestRepo: ObjTestRepository

	@Autowired
	lateinit var accountRepository: ObjAccountRepository

	// CodeTestType is now a Kotlin enum with companion object Enumeration

	@Test
	fun testAggregate() {
		assertNotNull(docTestRepo, "docTestRepository not null")
		assertEquals("doc_test", docTestRepo.aggregateType.id)

		val userId = sessionContext.userId
		sessionContext.currentTime
		val account = accountRepository.getByKey(TestDataSetup.TEST_ACCOUNT_KEY).get()

		var testA1: DocTest = docTestRepo.create()
		assertEquals(0, testA1.meta.version)

		initDocTest(testA1, "One", TYPE_A, userId)
		testA1.accountId = account.id

		assertNotNull(testA1, "test not null")
		assertNotNull(testA1.id, "id not null")
		assertNotNull(testA1.tenantId, "tenant not null")

		val testAId = testA1.id
		val testA1IdHash: Int = System.identityHashCode(testA1)

		assertNotNull(testA1.meta.createdByUserId, "createdByUser not null")
		assertNotNull(testA1.meta.createdAt, "createdAt not null")
		assertNotNull(testA1.meta.caseStage, "caseStage not null")
		assertEquals("test.new", testA1.meta.caseStage?.id, "caseStage.id")
		assertEquals(1, testA1.meta.transitionList.size)
		assertEquals(account.id, testA1.accountId, "account id")
		assertEquals(account.id, testA1.accountId, "account id")

		docTestRepo.store(testA1)
		assertEquals(1, testA1.meta.version)
		assertEquals(2, testA1.meta.transitionList.size)

		val testA2: DocTest = docTestRepo.get(testAId)
		val testA2IdHash: Int = System.identityHashCode(testA2)

		assertEquals(1, testA2.meta.version)
		assertNotEquals(testA1IdHash, testA2IdHash)

		assertNotNull(testA2.meta.modifiedByUserId, "modifiedByUser not null")
		assertNotNull(testA2.meta.modifiedAt, "modifiedAt not null")
		assertEquals(2, testA2.meta.transitionList.size)
		assertEquals(account.id, testA2.accountId, "account id")
		assertEquals(account.id, testA2.accountId, "account id")
	}

	@Test
	fun testAggregateProperties() {
		val userId = sessionContext.userId
		val account = accountRepository.getByKey(TestDataSetup.TEST_ACCOUNT_KEY).get()

		val typeA: CodeTestType = CodeTestType.TYPE_A
		val typeB: CodeTestType = CodeTestType.TYPE_B
		val typeC: CodeTestType = CodeTestType.TYPE_C

		var testA1: DocTest = docTestRepo.create()
		val testAId = testA1.id

		initDocTest(testA1, "One", TYPE_A, userId)
		testA1.accountId = account.id

		assertNotNull(testA1.meta.createdByUserId, "createdByUser not null")
		assertNotNull(testA1.meta.createdAt, "createdAt not null")
		assertEquals("[Short Test One, Long Test One]", testA1.caption)
		assertEquals("Short Test One", testA1.shortText)
		assertEquals("Long Test One", testA1.longText)
		assertEquals(42, testA1.int)
		assertEquals(BigDecimal.valueOf(42), testA1.nr)
		assertEquals(false, testA1.isDone)
		assertEquals(LocalDate.of(1966, 9, 8), testA1.date)
		assertEquals(JSON.valueOf(TEST_JSON), JSON.valueOf(testA1.json))
		assertEquals(typeA, testA1.testType)

		val refObj: ObjTest = objTestRepo.create()
		initObjTest(refObj, "Two", TYPE_B)
		val refObjId: Any = refObj.id
		objTestRepo.store(refObj)

		testA1.refObjId = refObjId
		assertEquals(
			"[Short Test One, Long Test One] (RefObj:[Short Test Two, Long Test Two])",
			testA1.caption,
		)

		val refDoc: DocTest = docTestRepo.create()
		initDocTest(refDoc, "Two", TYPE_B, userId)
		refDoc.accountId = account.id
		val refDocId: Any = refDoc.id
		docTestRepo.store(refDoc)

		testA1.refDocId = refDocId
		assertEquals(
			"[Short Test One, Long Test One] (RefObj:[Short Test Two, Long Test Two]) (RefDoc:[Short Test Two, Long Test Two])",
			testA1.caption,
		)

		// Test EnumSet operations with TestType
		assertFalse(testA1.testTypeSet.contains(typeA))
		testA1.testTypeSet.add(typeA)
		assertTrue(testA1.testTypeSet.contains(typeA))
		testA1.testTypeSet.add(typeB)
		assertTrue(testA1.testTypeSet.contains(typeB))
		assertEquals(2, testA1.testTypeSet.size)
		testA1.testTypeSet.remove(typeB)
		assertTrue(testA1.testTypeSet.contains(typeA))
		assertFalse(testA1.testTypeSet.contains(typeB))
		assertEquals(1, testA1.testTypeSet.size)
		testA1.testTypeSet.add(typeC)
		assertTrue(testA1.testTypeSet.contains(typeC))
		assertEquals(2, testA1.testTypeSet.size)

		docTestRepo.store(testA1)

		val testA2: DocTest = docTestRepo.load(testAId)

		assertEquals(
			"[Short Test One, Long Test One] (RefObj:[Short Test Two, Long Test Two]) (RefDoc:[Short Test Two, Long Test Two])",
			testA2.caption,
		)
		assertEquals("Short Test One", testA2.shortText)
		assertEquals("Long Test One", testA2.longText)
		assertEquals(42, testA2.int)
		assertEquals(BigDecimal.valueOf(42).setScale(3), testA2.nr?.setScale(3))
		assertEquals(false, testA2.isDone)
		assertEquals(LocalDate.of(1966, 9, 8), testA2.date)
		assertEquals(JSON.valueOf(TEST_JSON), JSON.valueOf(testA2.json))
		assertEquals(typeA, testA2.testType)
		assertEquals(refObjId, testA2.refObj?.id)
		assertEquals(refDocId, testA2.refDoc?.id)

		assertEquals(2, testA2.testTypeSet.size)
		assertTrue(testA2.testTypeSet.contains(typeA))
		assertTrue(testA2.testTypeSet.contains(typeC))

		testA2.shortText = "another shortText"
		testA2.longText = "another longText"
		testA2.int = 41
		testA2.nr = BigDecimal.valueOf(41)
		testA2.isDone = true
		testA2.date = LocalDate.of(1966, 1, 5)
		testA2.json = null
		testA2.testType = typeB
		testA2.refObjId = null
		testA2.refDocId = null

		testA2.testTypeSet.remove(typeA)
		testA2.testTypeSet.add(typeB)

		assertEquals("[another shortText, another longText]", testA2.caption)
		assertEquals("another shortText", testA2.shortText)
		assertEquals("another longText", testA2.longText)
		assertEquals(41, testA2.int)
		assertEquals(BigDecimal.valueOf(41).setScale(3), testA2.nr?.setScale(3))
		assertEquals(true, testA2.isDone)
		assertEquals(LocalDate.of(1966, 1, 5), testA2.date)
		assertNull(testA2.json)
		assertEquals(typeB, testA2.testType)
		assertNull(testA2.refObj)

		assertEquals(2, testA2.testTypeSet.size)
		assertFalse(testA2.testTypeSet.contains(typeA))
		assertTrue(testA2.testTypeSet.contains(typeB))
		assertTrue(testA2.testTypeSet.contains(typeC))
	}

	private fun initDocTest(
		test: DocTest,
		name: String,
		testTypeId: String,
		userId: Any,
	) {
		test.meta.setCaseStage(CodeCaseStageEnum.getCaseStage("test.new"), userId, OffsetDateTime.now())
		assertEquals("[, ]", test.caption)

		test.shortText = "Short Test $name"
		assertEquals("[Short Test $name, ]", test.caption)

		test.longText = "Long Test $name"
		assertEquals("[Short Test $name, Long Test $name]", test.caption)

		test.int = 42
		test.nr = BigDecimal.valueOf(42)
		test.isDone = false
		test.date = LocalDate.of(1966, 9, 8)
		test.json = JSON.valueOf(TEST_JSON).toString()

		test.testType = CodeTestType.getTestType(testTypeId)
	}

	private fun initObjTest(
		test: ObjTest,
		name: String,
		testTypeId: String,
	) {
		assertEquals("[, ]", test.caption)

		test.shortText = "Short Test $name"
		assertEquals("[Short Test $name, ]", test.caption)

		test.longText = "Long Test $name"
		assertEquals("[Short Test $name, Long Test $name]", test.caption)

		test.int = 42
		test.nr = BigDecimal.valueOf(42)
		test.isDone = false
		test.date = LocalDate.of(1966, 9, 8)
		test.json = JSON.valueOf(TEST_JSON).toString()

		test.testType = CodeTestType.getTestType(testTypeId)
	}

}
