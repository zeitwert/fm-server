package io.zeitwert.fm

import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.domain.test.model.ObjTest
import io.zeitwert.domain.test.model.ObjTestRepository
import io.zeitwert.domain.test.model.enums.CodeTestType
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

@SpringBootTest(classes = [TestApplication::class])
@ActiveProfiles("test")
class ObjTestTest {

	companion object {

		private const val TEST_JSON = "{ \"one\": \"one\", \"two\": 2 }"
		private const val TYPE_A = "type_a"
		private const val TYPE_B = "type_b"
	}

	@Autowired
	private lateinit var requestCtx: SessionContext

	@Autowired
	private lateinit var testRepository: ObjTestRepository

	@Test
	fun testBase() {
		assertNotNull(this.testRepository, "objTestRepository not null")
		assertEquals("obj_test", this.testRepository.aggregateType.id)

		requestCtx.tenantId
		requestCtx.userId
		requestCtx.currentTime

		val testA1 = this.testRepository.create()
		assertNotNull(testA1, "test not null")
		requireNotNull(testA1)

		assertNotNull(testA1.id, "id not null")
		assertNotNull(testA1.tenantId, "tenant not null")
		initObjTest(testA1, "One", TYPE_A)

		val testAId = testA1.id
		val testA1IdHash = System.identityHashCode(testA1)

		assertFalse(testA1.meta.isFrozen, "not frozen")
		assertNotNull(testA1.meta.createdByUserId, "createdByUser not null")
		assertNotNull(testA1.meta.createdAt, "createdAt not null")
		assertEquals(1, testA1.meta.transitionList.size)

		assertEquals(1, testA1.meta.transitionList.size)

		this.testRepository.store(testA1)

		val testA2 = this.testRepository.get(testAId)
		val testA2IdHash = System.identityHashCode(testA2)
		assertNotEquals(testA1IdHash, testA2IdHash)

		assertTrue(testA2.meta.isFrozen, "frozen")
		assertNotNull(testA2.meta.modifiedByUserId, "modifiedByUser not null")
		assertNotNull(testA2.meta.modifiedAt, "modifiedAt not null")
	}

	@Test
	fun testProperties() {
		requestCtx.tenantId
		requestCtx.userId
		requestCtx.currentTime

		val testA1 = this.testRepository.create()
		requireNotNull(testA1)

		val testAId = testA1.id
		initObjTest(testA1, "One", TYPE_A)

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
		assertEquals(CodeTestType.TYPE_A, testA1.testType)

		val testB1: ObjTest = this.testRepository.create()
		initObjTest(testB1, "Two", TYPE_B)
		val testBId: Any = testB1.id
		this.testRepository.store(testB1)

		testA1.refObjId = testBId
		assertEquals(
			"[Short Test One, Long Test One] ([Short Test Two, Long Test Two])",
			testA1.caption,
		)

		this.testRepository.store(testA1)

		val testA2 = this.testRepository.load(testAId)

		assertEquals("[Short Test One, Long Test One] ([Short Test Two, Long Test Two])", testA2.caption)
		assertEquals("Short Test One", testA2.shortText)
		assertEquals("Long Test One", testA2.longText)
		assertEquals(42, testA2.int)
		assertEquals(BigDecimal.valueOf(42).setScale(3), testA2.nr?.setScale(3))
		assertEquals(false, testA2.isDone)
		assertEquals(LocalDate.of(1966, 9, 8), testA2.date)
		assertEquals(JSON.valueOf(TEST_JSON), JSON.valueOf(testA2.json))
		assertEquals(CodeTestType.TYPE_A, testA2.testType)
		assertEquals(testBId, testA2.refObj?.id)

		testA2.shortText = "another shortText"
		testA2.longText = "another longText"
		testA2.int = 41
		testA2.nr = BigDecimal.valueOf(41)
		testA2.isDone = true
		testA2.date = LocalDate.of(1966, 1, 5)
		testA2.json = null
		testA2.testType = CodeTestType.TYPE_B
		testA2.refObjId = null

		assertEquals("[another shortText, another longText]", testA2.caption)
		assertEquals("another shortText", testA2.shortText)
		assertEquals("another longText", testA2.longText)
		assertEquals(41, testA2.int)
		assertEquals(BigDecimal.valueOf(41).setScale(3), testA2.nr?.setScale(3))
		assertEquals(true, testA2.isDone)
		assertEquals(LocalDate.of(1966, 1, 5), testA2.date)
		assertNull(testA2.json)
		assertEquals(CodeTestType.TYPE_B, testA2.testType)
		assertNull(testA2.refObj)
	}

	@Test
	fun testEnumSet() {
		requestCtx.tenantId
		requestCtx.userId
		requestCtx.currentTime

		val testA1 = this.testRepository.create()
		requireNotNull(testA1)

		val testAId = testA1.id
		initObjTest(testA1, "One", TYPE_A)

		assertFalse(testA1.testTypeSet.contains(CodeTestType.TYPE_A))
		testA1.testTypeSet.add(CodeTestType.TYPE_A)
		assertEquals(1, testA1.testTypeSet.size)
		assertTrue(testA1.testTypeSet.contains(CodeTestType.TYPE_A))

		testA1.testTypeSet.add(CodeTestType.TYPE_B)
		assertTrue(testA1.testTypeSet.contains(CodeTestType.TYPE_B))
		assertEquals(2, testA1.testTypeSet.size)

		testA1.testTypeSet.remove(CodeTestType.TYPE_B)
		assertTrue(testA1.testTypeSet.contains(CodeTestType.TYPE_A))
		assertFalse(testA1.testTypeSet.contains(CodeTestType.TYPE_B))
		assertEquals(1, testA1.testTypeSet.size)

		testA1.testTypeSet.add(CodeTestType.TYPE_C)
		assertTrue(testA1.testTypeSet.contains(CodeTestType.TYPE_C))
		assertEquals(2, testA1.testTypeSet.size)

		assertEquals(1, testA1.meta.transitionList.size)
		this.testRepository.store(testA1)
		assertEquals(2, testA1.meta.transitionList.size)

		val testA2: ObjTest = this.testRepository.load(testAId)
		assertEquals(2, testA2.meta.transitionList.size)

		assertEquals(2, testA2.testTypeSet.size)
		assertTrue(testA2.testTypeSet.contains(CodeTestType.TYPE_A))
		assertTrue(testA2.testTypeSet.contains(CodeTestType.TYPE_C))

		testA2.testTypeSet.remove(CodeTestType.TYPE_A)
		testA2.testTypeSet.add(CodeTestType.TYPE_B)

		assertEquals(2, testA2.testTypeSet.size)
		assertFalse(testA2.testTypeSet.contains(CodeTestType.TYPE_A))
		assertTrue(testA2.testTypeSet.contains(CodeTestType.TYPE_B))
		assertTrue(testA2.testTypeSet.contains(CodeTestType.TYPE_C))
	}

	@Test
	fun testPartList() {
		requestCtx.tenantId
		requestCtx.userId
		requestCtx.currentTime

		lateinit var testAId: Any

		run {
			val testA1 = this.testRepository.create()
			assertEquals(0, testA1.meta.version)
			assertEquals(1, testA1.meta.transitionList.size)

			initObjTest(testA1, "One", TYPE_A)
			assertEquals(0, testA1.nodeList.size)

			testA1.nodeList.add(null).apply { shortText = "A" }
			assertEquals(1, testA1.nodeList.size)

			val nodeB1 = testA1.nodeList.add(null).apply { shortText = "B" }
			assertEquals(2, testA1.nodeList.size)

			testA1.nodeList.add(null).apply { shortText = "C" }
			assertEquals(3, testA1.nodeList.size)

			testA1.nodeList.remove(nodeB1.id)
			assertEquals(2, testA1.nodeList.size)

			testA1.nodeList.add(null).apply { shortText = "D" }
			assertEquals(3, testA1.nodeList.size)

			testAId = testA1.id
			this.testRepository.store(testA1)
			assertEquals(1, testA1.meta.version)
			assertEquals(2, testA1.meta.transitionList.size)
		}

		run {
			val testA2 = this.testRepository.load(testAId)
			assertEquals(1, testA2.meta.version)
			assertEquals(2, testA2.meta.transitionList.size)
			assertEquals(3, testA2.nodeList.size)

			assertTrue(testA2.nodeList.any { n -> "A" == n.shortText })
			assertTrue(testA2.nodeList.none { n -> "B" == n.shortText })
			assertTrue(testA2.nodeList.any { n -> "C" == n.shortText })
			assertTrue(testA2.nodeList.any { n -> "D" == n.shortText })

			testA2.nodeList.remove(testA2.nodeList.first { n -> "C" == n.shortText }.id)
			assertEquals(2, testA2.nodeList.size)

			testA2.nodeList.add(null).apply { shortText = "E" }
			assertEquals(3, testA2.nodeList.size)

			this.testRepository.store(testA2)
			assertEquals(2, testA2.meta.version)
			assertEquals(3, testA2.meta.transitionList.size)
		}

		run {
			val testA3 = this.testRepository.load(testAId)
			assertEquals(2, testA3.meta.version)
			assertEquals(3, testA3.meta.transitionList.size)
			assertEquals(3, testA3.nodeList.size)

			assertTrue(testA3.nodeList.any { n -> "A" == n.shortText })
			assertTrue(testA3.nodeList.none { n -> "B" == n.shortText })
			assertTrue(testA3.nodeList.none { n -> "C" == n.shortText })
			assertTrue(testA3.nodeList.any { n -> "D" == n.shortText })
			assertTrue(testA3.nodeList.any { n -> "E" == n.shortText })
		}

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
		val testType: CodeTestType = CodeTestType.getTestType(testTypeId)!!
		test.testType = testType
	}

}
