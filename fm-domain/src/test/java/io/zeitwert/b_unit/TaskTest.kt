package io.zeitwert.b_unit

import dddrive.app.doc.model.enums.CodeCaseStage
import dddrive.app.doc.model.enums.CodeCaseStageEnum.Companion.getCaseStage
import dddrive.query.query
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.data.config.TestDataSetup
import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.oe.model.ObjUser
import io.zeitwert.fm.oe.model.ObjUserRepository
import io.zeitwert.fm.task.model.DocTask
import io.zeitwert.fm.task.model.DocTaskRepository
import io.zeitwert.fm.task.model.enums.CodeTaskPriority
import io.zeitwert.test.TestApplication
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest(classes = [TestApplication::class])
@ActiveProfiles("test")
class TaskTest {

	@Autowired
	lateinit var sessionContext: SessionContext

	@Autowired
	lateinit var userRepository: ObjUserRepository

	@Autowired
	lateinit var accountRepository: ObjAccountRepository

	@Autowired
	lateinit var taskRepository: DocTaskRepository

	val DueDate: OffsetDateTime = OffsetDateTime.of(2023, 9, 8, 0, 0, 0, 0, ZoneOffset.ofHours(2))

	val USER_EMAIL: String = "cc@zeitwert.io"
	val TEST_ACCOUNT: String = "TA"

	lateinit var firstRelatedTo: ObjUser
	lateinit var secondRelatedTo: ObjAccount

	lateinit var newStage: CodeCaseStage
	lateinit var progressStage: CodeCaseStage
	lateinit var doneStage: CodeCaseStage

	@Test
	@Throws(Exception::class)
	fun testTask() {
		initTestData()

		val userId = sessionContext.userId
		val now = sessionContext.currentTime

		assertNotNull(taskRepository, "taskRepository not null")
		assertEquals("doc_task", taskRepository.aggregateType.id)

		val taskA1 = taskRepository.create()

		assertNotNull(taskA1, "task not null")
		assertNotNull(taskA1.id, "id not null")
		assertNotNull(taskA1.tenantId, "tenant not null")

		val taskA_id = taskA1.id
		val taskA_idHash = System.identityHashCode(taskA1)

		assertNotNull(taskA1.meta.createdByUserId, "createdByUser not null")
		assertNotNull(taskA1.meta.createdAt, "createdAt not null")

		initTask1(taskA1, userId, now)
		assertEquals(taskA1.meta.caseStage, newStage)
		assertTrue(taskA1.meta.isInWork)
		checkTask1(taskA1)

		taskRepository.store(taskA1)

		val taskA2 = taskRepository.load(taskA_id)
		val taskA2_idHash = System.identityHashCode(taskA2)

		assertNotEquals(taskA_idHash, taskA2_idHash)
		assertNotNull(taskA2.meta.modifiedByUserId, "modifiedByUser not null")
		assertNotNull(taskA2.meta.modifiedAt, "modifiedAt not null")
		assertEquals(taskA2.meta.caseStage, newStage)
		assertTrue(taskA2.meta.isInWork)

		val relatedTo = taskA2.relatedTo
		assertNotNull(relatedTo, "relatedTo not null")
		assertEquals(firstRelatedTo.id, relatedTo.id, "relatedTo id")

		checkTask1(taskA2)

		initTask2(taskA2, userId, now)
		assertTrue(taskA2.meta.isInWork)

		taskRepository.store(taskA2)

		val taskA3 = taskRepository.load(taskA_id)

		assertEquals(taskA3.meta.caseStage, progressStage)
		assertTrue(taskA3.meta.isInWork)

		checkTask2(taskA3)

		initTask1(taskA3, userId, now)
		taskA3.meta.setCaseStage(doneStage, userId, now)
		assertFalse(taskA3.meta.isInWork)

		taskRepository.store(taskA3)

		val taskA4 = taskRepository.get(taskA_id)

		assertEquals(taskA4.meta.caseStage, doneStage)
		assertFalse(taskA4.meta.isInWork)

		checkTask1(taskA4)
	}

	private fun initTestData() {
		firstRelatedTo = userRepository.getByEmail(USER_EMAIL).get()
		assertNotNull(firstRelatedTo, "relatedTo")
		secondRelatedTo = accountRepository.getByKey(TestDataSetup.TEST_ACCOUNT_KEY).get()
		assertNotNull(secondRelatedTo, "account")
		newStage = getCaseStage("task.new")
		progressStage = getCaseStage("task.progress")
		doneStage = getCaseStage("task.done")
	}

	private fun initTask1(
		task: DocTask,
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		task.meta.setCaseStage(getCaseStage("task.new"), userId, timestamp)
		task.relatedToId = firstRelatedTo.id
		task.accountId = secondRelatedTo.id
		task.subject = "Todo"
		task.content = "content"
		task.isPrivate = false
		task.priority = CodeTaskPriority.NORMAL
		task.dueAt = DueDate
	}

	private fun checkTask1(task: DocTask) {
		assertEquals(task.relatedToId, firstRelatedTo.id)
		assertEquals(secondRelatedTo.id, task.accountId, "account id")
		assertEquals(secondRelatedTo.id, task.accountId, "account id")
		assertEquals("Todo", task.subject)
		assertEquals("content", task.content)
		assertEquals(false, task.isPrivate)
		assertEquals(task.priority, CodeTaskPriority.NORMAL)
		assertEquals(DueDate, task.dueAt)
	}

	private fun initTask2(
		task: DocTask,
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		assertEquals(secondRelatedTo.id, task.accountId, "account id")
		assertEquals(secondRelatedTo.id, task.accountId, "account id")
		task.subject = "Todos"
		task.content = "contents"
		task.isPrivate = true
		task.priority = CodeTaskPriority.HIGH
		task.dueAt = DueDate.plusDays(4)
		task.meta.setCaseStage(progressStage, userId, timestamp)
	}

	private fun checkTask2(task: DocTask) {
		assertEquals("contents", task.content)
		assertEquals(true, task.isPrivate)
		assertEquals(task.priority, CodeTaskPriority.HIGH)
		assertEquals(task.dueAt, DueDate.plusDays(4))
		assertEquals(task.meta.caseStage, progressStage)
	}

	@Test
	fun testFindByRelatedToId() {
		initTestData()

		val userId = sessionContext.userId
		val now = sessionContext.currentTime

		// Create two tasks related to the same user (RelatedTo)
		val task1 = taskRepository.create()
		initTask1(task1, userId, now)
		taskRepository.store(task1)
		val task1Id = task1.id

		val task2 = taskRepository.create()
		initTask1(task2, userId, now)
		task2.subject = "Task 2"
		taskRepository.store(task2)
		val task2Id = task2.id

		// Create a task related to a different object (Account)
		val task3 = taskRepository.create()
		task3.meta.setCaseStage(getCaseStage("task.new"), userId, now)
		task3.relatedToId = secondRelatedTo.id
		task3.accountId = secondRelatedTo.id
		task3.subject = "Task 3 - different related"
		task3.content = "content"
		task3.isPrivate = false
		task3.priority = CodeTaskPriority.NORMAL
		task3.dueAt = DueDate
		taskRepository.store(task3)
		val task3Id = task3.id

		// Find tasks by relatedToId using QuerySpec
		val querySpec = query {
			filter { "relatedToId" eq firstRelatedTo.id }
		}
		val foundIds = taskRepository.find(querySpec)

		// Should find task1 and task2 (both related to RelatedTo user)
		assertTrue(foundIds.contains(task1Id), "Should find task1")
		assertTrue(foundIds.contains(task2Id), "Should find task2")
		assertFalse(foundIds.contains(task3Id), "Should NOT find task3 (different relatedToId)")

		// Find tasks related to Account
		val querySpec2 = query {
			filter { "relatedToId" eq secondRelatedTo.id }
		}
		val foundIds2 = taskRepository.find(querySpec2)

		// Should find task3 (related to Account)
		assertTrue(foundIds2.contains(task3Id), "Should find task3")
		assertFalse(foundIds2.contains(task1Id), "Should NOT find task1")
		assertFalse(foundIds2.contains(task2Id), "Should NOT find task2")
	}

}
