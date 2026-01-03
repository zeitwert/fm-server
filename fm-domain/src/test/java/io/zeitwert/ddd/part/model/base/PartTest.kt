package io.zeitwert.ddd.part.model.base

import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.domain.test.model.ObjTest
import io.zeitwert.domain.test.model.ObjTestPartNode
import io.zeitwert.domain.test.model.ObjTestRepository
import io.zeitwert.domain.test.model.enums.CodeTestType.Enumeration.getTestType
import io.zeitwert.test.TestApplication
import org.jooq.JSON
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.lang.String
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.Any
import kotlin.Int
import kotlin.arrayOf
import kotlin.run

@SpringBootTest(classes = [TestApplication::class])
@ActiveProfiles("test")
class PartTest {

	@Autowired
	private lateinit var sessionContext: SessionContext

	@Autowired
	private lateinit var testRepository: ObjTestRepository

	@Test
	fun testNodeList() {
		Assertions.assertNotNull(this.testRepository, "testRepository not null")
		Assertions.assertEquals("obj_test", this.testRepository.aggregateType.id)

		lateinit var testA_id: Any

		run {
			val testA1 = this.testRepository.create()
			this.initObjTest(testA1, "One", "type_a")
			testA_id = testA1.id

			Assertions.assertEquals(0, testA1.nodeList.size)

			val testA1_n0 = testA1.nodeList.add(null as Int?)
			this.initObjTestPartNode(testA1_n0, "First", "type_a")
			Assertions.assertEquals(1, testA1.nodeList.size)

			val testA1_n1 = testA1.nodeList.add(null as Int?)
			this.initObjTestPartNode(testA1_n1, "Second", "type_b")
			val testA1_n2 = testA1.nodeList.add(null as Int?)
			this.initObjTestPartNode(testA1_n2, "Third", "type_c")

			Assertions.assertEquals(3, testA1.nodeList.size)

			Assertions.assertEquals(testA1_n0, testA1.nodeList[0])
			Assertions.assertEquals(testA1_n1, testA1.nodeList[1])
			Assertions.assertEquals(testA1_n2, testA1.nodeList[2])
			Assertions.assertEquals(
				testA1.nodeList.stream().toList(),
				listOf<ObjTestPartNode?>(testA1_n0, testA1_n1, testA1_n2),
			)
			Assertions.assertEquals(
				"Short Test Node First,Short Test Node Second,Short Test Node Third",
				String.join(
					",",
					testA1.nodeList
						.stream()
						.map<kotlin.String?>(ObjTestPartNode::shortText)
						.toList(),
				),
			)
			Assertions.assertEquals("Short Test Node Second", testA1.nodeList[1].shortText)

			testA1.nodeList.remove(testA1_n1.id)
			Assertions.assertEquals(2, testA1.nodeList.size)
			Assertions.assertEquals(testA1_n2, testA1.nodeList.get(1))
			Assertions.assertEquals(testA1_n2, testA1.nodeList.getById(testA1_n2.id))
			Assertions.assertEquals(2, testA1.nodeList.size)
			Assertions.assertEquals(testA1_n0.shortText, testA1.nodeList[0].shortText)
			Assertions.assertEquals(testA1_n2.shortText, testA1.nodeList[1].shortText)

			Assertions.assertEquals(testA1_n0.shortText, testA1.nodeList[0].shortText)
			Assertions.assertEquals(testA1_n2.shortText, testA1.nodeList[1].shortText)
			Assertions.assertEquals(testA1.nodeList.stream().toList(), listOf<ObjTestPartNode?>(testA1_n0, testA1_n2))

			this.testRepository.store(testA1)
		}

		run {
			val testA2 = this.testRepository.load(testA_id)

			Assertions.assertEquals(2, testA2.nodeList.size)
			Assertions.assertEquals(
				"Short Test Node First,Short Test Node Third",
				String.join(
					",",
					testA2.nodeList
						.stream()
						.map<kotlin.String?>(ObjTestPartNode::shortText)
						.toList(),
				),
			)
			Assertions.assertEquals("Short Test Node Third", testA2.nodeList.get(1).shortText)

			val testA2_n2 = testA2.nodeList.add(null as Int?)
			this.initObjTestPartNode(testA2_n2, "Fourth", "type_b")
			testA2.nodeList.get(1).int = 43

			this.testRepository.store(testA2)
		}

		run {
			val testA3 = this.testRepository.load(testA_id)

			Assertions.assertEquals(3, testA3.nodeList.size)
			Assertions.assertEquals(
				"Short Test Node First,Short Test Node Third,Short Test Node Fourth",
				String.join(
					",",
					testA3.nodeList
						.stream()
						.map<kotlin.String?>(ObjTestPartNode::shortText)
						.toList(),
				),
			)

			testA3.nodeList.clear()
			Assertions.assertEquals(0, testA3.nodeList.size)
			Assertions.assertEquals(0, testA3.nodeList.size)
		}

	}

	private fun initObjTest(
		test: ObjTest,
		name: kotlin.String,
		testTypeId: kotlin.String,
	) {
		Assertions.assertEquals("[, ]", test.caption)
		test.shortText = "Short Test $name"
		Assertions.assertEquals("[Short Test $name, ]", test.caption)
		test.longText = "Long Test $name"
		Assertions.assertEquals("[Short Test $name, Long Test $name]", test.caption)
		test.int = 42
		test.nr = BigDecimal.valueOf(42)
		test.isDone = false
		test.date = LocalDate.of(1966, 9, 8)
		test.json = JSON.valueOf(TEST_JSON).toString()
		val testType = getTestType(testTypeId)
		test.testType = testType
	}

	private fun initObjTestPartNode(
		node: ObjTestPartNode,
		name: kotlin.String?,
		testTypeId: kotlin.String?,
	) {
		node.shortText = "Short Test Node $name"
		node.longText = "Long Test Node $name"
		node.int = 42
		node.nr = BigDecimal.valueOf(42)
		node.isDone = false
		node.date = LocalDate.of(1966, 9, 8)
		node.json = JSON.valueOf(TEST_JSON).toString()
		val testType = getTestType(testTypeId)
		node.testType = testType
	}

	companion object {

		private const val TEST_JSON = "{ \"one\": \"one\", \"two\": 2 }"
	}

}
