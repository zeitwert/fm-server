package io.zeitwert.fm

import dddrive.app.doc.model.enums.CodeCaseStage
import dddrive.app.doc.model.enums.CodeCaseStageEnum.Companion.getCaseStage
import dddrive.ddd.query.query
import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.oe.model.ObjUser
import io.zeitwert.fm.oe.model.ObjUserRepository
import io.zeitwert.fm.task.model.DocTask
import io.zeitwert.fm.task.model.DocTaskRepository
import io.zeitwert.fm.task.model.enums.CodeTaskPriority
import io.zeitwert.fm.task.model.enums.CodeTaskPriority.Enumeration.getPriority
import io.zeitwert.test.TestApplication
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.OffsetDateTime
import java.time.ZoneOffset

@SpringBootTest(classes = [TestApplication::class])
@ActiveProfiles("test")
class TaskTest {

	@Autowired
	private val sessionContext: SessionContext? = null

	@Autowired
	private val userRepository: ObjUserRepository? = null

	@Autowired
	private val accountRepository: ObjAccountRepository? = null

	@Autowired
	private val taskRepository: DocTaskRepository? = null

	@Test
	@Throws(Exception::class)
	fun testTask() {
		this.getTestData(sessionContext)

		val userId = sessionContext!!.userId
		val now = sessionContext.currentTime

		Assertions.assertNotNull(this.taskRepository, "taskRepository not null")
		Assertions.assertEquals("doc_task", this.taskRepository!!.aggregateType.id)

		var taskA1: DocTask? = this.taskRepository.create()

		Assertions.assertNotNull(taskA1, "task not null")
		Assertions.assertNotNull(taskA1!!.id, "id not null")
		Assertions.assertNotNull(taskA1.tenantId, "tenant not null")

		val taskA_id = taskA1.id
		val taskA_idHash = System.identityHashCode(taskA1)

		Assertions.assertNotNull(taskA1.meta.createdByUserId, "createdByUser not null")
		Assertions.assertNotNull(taskA1.meta.createdAt, "createdAt not null")

		this.initTask1(taskA1, userId, now)
		Assertions.assertEquals(taskA1.meta.caseStage, StageNew)
		Assertions.assertTrue(taskA1.meta.isInWork)
		this.checkTask1(taskA1)

		this.taskRepository.store(taskA1)
		taskA1 = null

		var taskA2: DocTask? = this.taskRepository.load(taskA_id)
		val taskA2_idHash = System.identityHashCode(taskA2)

		Assertions.assertNotEquals(taskA_idHash, taskA2_idHash)
		Assertions.assertNotNull(taskA2!!.meta.modifiedByUserId, "modifiedByUser not null")
		Assertions.assertNotNull(taskA2.meta.modifiedAt, "modifiedAt not null")
		Assertions.assertEquals(taskA2.meta.caseStage, StageNew)
		Assertions.assertTrue(taskA2.meta.isInWork)

		this.checkTask1(taskA2)

		this.initTask2(taskA2, userId, now)
		Assertions.assertTrue(taskA2.meta.isInWork)

		this.taskRepository.store(taskA2)
		taskA2 = null

		var taskA3: DocTask? = this.taskRepository.load(taskA_id)

		Assertions.assertEquals(taskA3!!.meta.caseStage, StageProgress)
		Assertions.assertTrue(taskA3.meta.isInWork)

		this.checkTask2(taskA3)

		this.initTask1(taskA3, userId, now)
		taskA3.meta.setCaseStage(StageDone!!, userId, now)
		Assertions.assertFalse(taskA3.meta.isInWork)

		this.taskRepository.store(taskA3)
		taskA3 = null

		val taskA4 = this.taskRepository.get(taskA_id)

		Assertions.assertEquals(taskA4.meta.caseStage, StageDone)
		Assertions.assertFalse(taskA4.meta.isInWork)

		this.checkTask1(taskA4)
	}

	private fun getTestData(sessionContext: SessionContext?) {
		RelatedTo = this.userRepository!!.getByEmail(USER_EMAIL).get()
		Assertions.assertNotNull(RelatedTo, "relatedTo")
		Account = this.accountRepository!!.get(this.accountRepository.find(null).first())
		Assertions.assertNotNull(Account, "account")
		StageNew = getCaseStage("task.new")
		StageProgress = getCaseStage("task.progress")
		StageDone = getCaseStage("task.done")
		PrioNormal = getPriority("normal")
		PrioHigh = getPriority("high")
	}

	private fun initTask1(
		task: DocTask,
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		task.meta.setCaseStage(getCaseStage("task.new"), userId, timestamp)
		task.relatedToId = RelatedTo!!.id
		task.accountId = Account!!.id
		task.subject = "Todo"
		task.content = "content"
		task.isPrivate = false
		task.priority = PrioNormal
		task.dueAt = DueDate
	}

	private fun checkTask1(task: DocTask) {
		Assertions.assertEquals(task.relatedToId, RelatedTo!!.id)
		Assertions.assertEquals(Account!!.id, task.accountId, "account id")
		Assertions.assertEquals(Account!!.id, task.accountId, "account id")
		Assertions.assertEquals("Todo", task.subject)
		Assertions.assertEquals("content", task.content)
		Assertions.assertEquals(false, task.isPrivate)
		Assertions.assertEquals(task.priority, PrioNormal)
		Assertions.assertEquals(DueDate, task.dueAt)
	}

	private fun initTask2(
		task: DocTask,
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		Assertions.assertEquals(Account!!.id, task.accountId, "account id")
		Assertions.assertEquals(Account!!.id, task.accountId, "account id")
		task.subject = "Todos"
		task.content = "contents"
		task.isPrivate = true
		task.priority = PrioHigh
		task.dueAt = DueDate.plusDays(4)
		task.meta.setCaseStage(StageProgress!!, userId, timestamp)
	}

	private fun checkTask2(task: DocTask) {
		Assertions.assertEquals("contents", task.content)
		Assertions.assertEquals(true, task.isPrivate)
		Assertions.assertEquals(task.priority, PrioHigh)
		Assertions.assertEquals(task.dueAt, DueDate.plusDays(4))
		Assertions.assertEquals(task.meta.caseStage, StageProgress)
	}

	@Test
	fun testFindByRelatedToId() {
		this.getTestData(sessionContext)

		val userId = sessionContext!!.userId
		val now = sessionContext.currentTime

		// Create two tasks related to the same user (RelatedTo)
		val task1 = this.taskRepository!!.create()
		this.initTask1(task1, userId, now)
		this.taskRepository.store(task1)
		val task1Id = task1.id

		val task2 = this.taskRepository.create()
		this.initTask1(task2, userId, now)
		task2.subject = "Task 2"
		this.taskRepository.store(task2)
		val task2Id = task2.id

		// Create a task related to a different object (Account)
		val task3 = this.taskRepository.create()
		task3.meta.setCaseStage(getCaseStage("task.new"), userId, now)
		task3.relatedToId = Account!!.id
		task3.accountId = Account!!.id
		task3.subject = "Task 3 - different related"
		task3.content = "content"
		task3.isPrivate = false
		task3.priority = PrioNormal
		task3.dueAt = DueDate
		this.taskRepository.store(task3)
		val task3Id = task3.id

		// Find tasks by relatedToId using QuerySpec
		val querySpec = query {
			filter { "relatedToId" eq RelatedTo!!.id }
		}
		val foundIds = this.taskRepository.find(querySpec)

		// Should find task1 and task2 (both related to RelatedTo user)
		Assertions.assertTrue(foundIds.contains(task1Id), "Should find task1")
		Assertions.assertTrue(foundIds.contains(task2Id), "Should find task2")
		Assertions.assertFalse(foundIds.contains(task3Id), "Should NOT find task3 (different relatedToId)")

		// Find tasks related to Account
		val querySpec2 = query {
			filter { "relatedToId" eq Account!!.id }
		}
		val foundIds2 = this.taskRepository.find(querySpec2)

		// Should find task3 (related to Account)
		Assertions.assertTrue(foundIds2.contains(task3Id), "Should find task3")
		Assertions.assertFalse(foundIds2.contains(task1Id), "Should NOT find task1")
		Assertions.assertFalse(foundIds2.contains(task2Id), "Should NOT find task2")
	}

	companion object {

		val DueDate: OffsetDateTime = OffsetDateTime.of(2023, 9, 8, 0, 0, 0, 0, ZoneOffset.ofHours(2))

		const val USER_EMAIL: String = "cc@zeitwert.io"
		const val TEST_ACCOUNT: String = "TA"

		var RelatedTo: ObjUser? = null
		var Account: ObjAccount? = null
		var StageNew: CodeCaseStage? = null
		var StageProgress: CodeCaseStage? = null
		var StageDone: CodeCaseStage? = null
		var PrioNormal: CodeTaskPriority? = null
		var PrioHigh: CodeTaskPriority? = null
	}

}
