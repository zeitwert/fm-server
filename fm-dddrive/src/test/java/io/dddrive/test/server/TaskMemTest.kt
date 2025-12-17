package io.dddrive.test.server

import io.dddrive.core.doc.model.enums.CodeCaseDef
import io.dddrive.core.doc.model.enums.CodeCaseDefEnum
import io.dddrive.core.doc.model.enums.CodeCaseStage
import io.dddrive.core.doc.model.enums.CodeCaseStageEnum
import io.dddrive.core.oe.model.ObjTenant
import io.dddrive.core.oe.model.ObjUser
import io.dddrive.core.property.model.PropertyChangeListener
import io.dddrive.domain.oe.model.ObjTenantRepository
import io.dddrive.domain.oe.model.ObjUserRepository
import io.dddrive.domain.task.model.DocTaskRepository
import io.dddrive.domain.task.model.enums.CodeTaskPriority
import io.dddrive.test.server.test.TestApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

@SpringBootTest(classes = [TestApplication::class])
@ActiveProfiles("domain", "mem")
class TaskMemTest : PropertyChangeListener {

	@Autowired
	private lateinit var tenantRepo: ObjTenantRepository

	@Autowired
	private lateinit var userRepo: ObjUserRepository

	@Autowired
	private lateinit var taskRepo: DocTaskRepository

	@Autowired
	private lateinit var caseDefEnum: CodeCaseDefEnum

	@Autowired
	private lateinit var caseStageEnum: CodeCaseStageEnum

	private lateinit var tenant: ObjTenant
	private lateinit var user: ObjUser
	private lateinit var user1: ObjUser

	private lateinit var simpleTaskDef: CodeCaseDef
	private lateinit var taskNewStage: CodeCaseStage
	private lateinit var taskInProgressStage: CodeCaseStage
	private lateinit var taskDoneStage: CodeCaseStage

	override fun propertyChange(
		op: String,
		path: String,
		value: String?,
		oldValue: String?,
		isInCalc: Boolean,
	) {
		// Simple println for now, can be expanded if specific change logging is needed for debugging tests
		// println("PropertyChange { op: $op, path: $path, value: $value, oldValue: $oldValue, isInCalc: $isInCalc }")
	}

	@BeforeEach
	fun setUp() {
		// Get tenant and users
		tenant = tenantRepo.getByKey(ObjTenantRepository.KERNEL_TENANT_KEY).orElse(null)
		assertNotNull(tenant, "Kernel tenant should exist")

		user = userRepo.getByEmail(ObjUserRepository.KERNEL_USER_EMAIL).orElse(null)
		assertNotNull(user, "Kernel user should exist")

		user1 =
			userRepo.getByEmail("user1@example.com").orElseGet {
				val newUser = userRepo.create(tenant.id, user.id, OffsetDateTime.now())
				newUser.name = "Test User 1"
				newUser.email = "user1@example.com"
				userRepo.store(newUser, user.id, OffsetDateTime.now())
				newUser
			}
		assertNotNull(user1, "user1 should exist")

		// Get case definitions and stages
		simpleTaskDef = caseDefEnum.getItem("simpleTask")
		assertNotNull(simpleTaskDef, "Simple task case definition should exist")

		taskNewStage = caseStageEnum.getItem("task.new")
		assertNotNull(taskNewStage, "Task new stage should exist")

		taskInProgressStage = caseStageEnum.getItem("task.inProgress")
		assertNotNull(taskInProgressStage, "Task in progress stage should exist")

		taskDoneStage = caseStageEnum.getItem("task.done")
		assertNotNull(taskDoneStage, "Task done stage should exist")
	}

	@Test
	fun testTaskWorkflowWithComments() { // Renamed to reflect new tests
		assertEquals("docTask", taskRepo.aggregateType.id)

		val now = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS)
		val dueDate = now.plusDays(7)
		val remindDate = now.plusDays(5)

		// Create a new task
		val task = taskRepo.create(tenant.id, user.id, now)
		task.meta.addPropertyChangeListener(this)
		val taskId = task.id
		assertNotNull(taskId, "Task ID should not be null")

		// Initially, the task should be frozen until caseDef is set
		assertTrue(task.isFrozen, "Task should be frozen initially")

		// Set the case definition - this will unfreeze the task
		task.setCaseDef(simpleTaskDef)
		assertFalse(task.isFrozen, "Task should be unfrozen after setting caseDef")
		assertEquals(simpleTaskDef, task.meta.caseDef, "CaseDef should be set")

		// Set initial case stage
		task.setCaseStage(taskNewStage, user.id as Int, now)
		assertEquals(taskNewStage, task.meta.caseStage, "CaseStage should be 'New'")
		assertTrue(task.meta.isInWork, "Task should be in work (not terminal)")

		// Set task-specific properties
		task.subject = "Implement new feature with comments"
		task.content = "Implement the user authentication module with OAuth2 support and add comment functionality."
		task.priority = CodeTaskPriority.HIGH
		task.private = false
		task.dueAt = dueDate
		task.remindAt = remindDate
		task.assignee = user1

		// Verify properties
		assertEquals("Implement new feature with comments", task.subject)
		assertEquals(
			"Implement the user authentication module with OAuth2 support and add comment functionality.",
			task.content,
		)
		assertEquals(CodeTaskPriority.HIGH, task.priority)
		assertEquals(false, task.private)
		assertEquals(dueDate, task.dueAt)
		assertEquals(remindDate, task.remindAt)
		assertEquals(user1.id, task.assignee.id)
		assertEquals("Implement new feature with comments", task.caption, "Caption should be derived from subject")

		// Check initial transition
		assertEquals(1, task.meta.transitionList.size, "Should have one initial transition")
		val initialTransition = task.meta.transitionList.first()
		assertNull(initialTransition.oldCaseStage, "Initial transition old stage should be null")
		assertEquals(taskNewStage, initialTransition.newCaseStage)
		assertEquals(user.id, initialTransition.user.id) // Compare IDs for user objects
		assertEquals(0, task.commentList.size, "Initially, task should have no comments")

		// Add first comment
		val commentText1 = "This is the first comment on the task."
		val comment1TimeBefore = OffsetDateTime.now()
		val comment1 = task.addComment()
		comment1.text = commentText1
		val comment1TimeAfter = OffsetDateTime.now()

		assertNotNull(comment1.id, "Comment 1 ID should not be null")
		assertEquals(commentText1, comment1.text, "Comment 1 text should match")
		assertNotNull(comment1.createdAt, "Comment 1 createdAt should not be null")
		assertTrue(
			comment1.createdAt!! >= comment1TimeBefore && comment1.createdAt!! <= comment1TimeAfter,
			"Comment 1 createdAt timestamp should be recent",
		)

		assertEquals(1, task.commentList.size, "Task should have 1 comment")
		assertTrue(task.commentList.contains(comment1), "Task comment list should contain comment1")

		val commentText2 = "User1 adds a follow-up comment."
		val comment2TimeBefore = OffsetDateTime.now()
		val comment2 = task.addComment()
		comment2.text = commentText2
		val comment2TimeAfter = OffsetDateTime.now()

		assertNotNull(comment2.id, "Comment 2 ID should not be null")
		assertEquals(commentText2, comment2.text, "Comment 2 text should match")
		assertTrue(
			comment2.createdAt!! >= comment2TimeBefore && comment2.createdAt!! <= comment2TimeAfter,
			"Comment 2 createdAt timestamp should be recent",
		)
		assertEquals(2, task.commentList.size, "Task should have 2 comments")

		// Store the task
		taskRepo.store(task, user.id, now.plusMinutes(1))

		// Load and verify
		val loadedTask = taskRepo.get(taskId)!!
		assertNotNull(loadedTask)
		assertTrue(loadedTask.isFrozen, "Loaded task should be frozen (read-only)")

		// Verify all properties persisted correctly
		assertEquals("Implement new feature with comments", loadedTask.subject)
		assertEquals(CodeTaskPriority.HIGH, loadedTask.priority)
		assertEquals(simpleTaskDef, loadedTask.meta.caseDef)
		assertEquals(taskNewStage, loadedTask.meta.caseStage)
		assertEquals(user1.id, loadedTask.assignee.id)

		// Verify comments persisted
		assertEquals(2, loadedTask.commentList.size, "Loaded task should have 2 comments")
		val loadedComment1 = loadedTask.commentList.find { it.id == comment1.id }
		assertNotNull(loadedComment1, "Loaded comment 1 should exist")
		assertEquals(commentText1, loadedComment1!!.text)
		assertNotNull(loadedComment1.createdAt)

		val loadedComment2 = loadedTask.commentList.find { it.id == comment2.id }
		assertNotNull(loadedComment2, "Loaded comment 2 should exist")
		assertEquals(commentText2, loadedComment2!!.text)
		assertNotNull(loadedComment2.createdAt)

		// Should have 2 transitions now (initial + store)
		assertEquals(2, loadedTask.meta.transitionList.size)

		// Test workflow progression
		val mutableTask = taskRepo.load(taskId) // load for editing
		assertFalse(mutableTask.isFrozen, "Loaded for edit task should not be frozen")

		// Move task to "In Progress"
		val progressTime = now.plusMinutes(10)
		mutableTask.setCaseStage(taskInProgressStage, user1.id as Int, progressTime)
		assertEquals(taskInProgressStage, mutableTask.meta.caseStage)

		// Add a comment after stage change
		val commentText3 = "Task is now in progress."
		val comment3 = mutableTask.addComment()
		comment3.text = commentText3
		assertEquals(
			3,
			mutableTask.commentList.size,
			"Mutable task should have 3 comments after adding one in progress",
		)

		// Store after stage change
		taskRepo.store(mutableTask, user1.id, progressTime.plusSeconds(5))

		// Verify stage change and new comment
		val taskAfterProgress = taskRepo.get(taskId)!!
		assertEquals(taskInProgressStage, taskAfterProgress.meta.caseStage)
		assertEquals(3, taskAfterProgress.meta.transitionList.size) // initial + store + stage change
		assertEquals(3, taskAfterProgress.commentList.size, "Task after progress should have 3 comments")
		val loadedComment3 = taskAfterProgress.commentList.find { it.id == comment3.id }
		assertNotNull(loadedComment3, "Loaded comment 3 should exist")
		assertEquals(commentText3, loadedComment3!!.text)

		// Check the stage change transition
		val stageChangeTransition =
			taskAfterProgress.meta.transitionList[taskAfterProgress.meta.transitionList.size - 1]
		assertEquals(taskNewStage, stageChangeTransition.oldCaseStage)
		assertEquals(taskInProgressStage, stageChangeTransition.newCaseStage)
		assertEquals(user1.id, stageChangeTransition.user.id)

		// Test removing a comment
		val taskToEditComments = taskRepo.load(taskId)
		assertNotNull(
			taskToEditComments.commentList.find { it.id == comment1.id },
			"Comment 1 should exist before removal",
		)
		taskToEditComments.removeComment(comment1.id!!)
		assertEquals(2, taskToEditComments.commentList.size, "Task should have 2 comments after removal")
		assertNull(
			taskToEditComments.commentList.find { it.id == comment1.id },
			"Comment 1 should not exist after removal",
		)
		taskRepo.store(taskToEditComments, user.id, now.plusMinutes(20))

		val taskAfterCommentRemoval = taskRepo.get(taskId)!!
		assertEquals(2, taskAfterCommentRemoval.commentList.size, "Loaded task should have 2 comments after removal")
		assertNull(
			taskAfterCommentRemoval.commentList.find { it.id == comment1.id },
			"Loaded comment 1 should not exist after removal",
		)
		assertNotNull(
			taskAfterCommentRemoval.commentList.find { it.id == comment2.id },
			"Loaded comment 2 should still exist",
		)
		assertNotNull(
			taskAfterCommentRemoval.commentList.find { it.id == comment3.id },
			"Loaded comment 3 should still exist",
		)

		// Complete the task
		val completeTask = taskRepo.load(taskId)
		val completeTime = now.plusMinutes(30)
		completeTask.setCaseStage(taskDoneStage, user1.id as Int, completeTime)
		assertEquals(taskDoneStage, completeTask.meta.caseStage)
		assertFalse(completeTask.meta.isInWork, "Done task should not be in work (terminal stage)")

		taskRepo.store(completeTask, user1.id, completeTime.plusSeconds(5))

		// Verify final state
		val completedTask = taskRepo.get(taskId)!!
		assertEquals(taskDoneStage, completedTask.meta.caseStage)
		assertFalse(completedTask.meta.isInWork)
		// Transitions: initial, store1, stage_change_store, comment_removal_store, final_stage_change_store
		assertEquals(5, completedTask.meta.transitionList.size)
		assertEquals(2, completedTask.commentList.size, "Completed task should still have 2 comments")
	}

	@Test
	fun testTaskWithMinimalDataAndNoComments() { // Renamed
		val now = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS)

		// Create task with minimal data
		val task = taskRepo.create(tenant.id, user.id, now)
		task.setCaseDef(simpleTaskDef)
		task.setCaseStage(taskNewStage, user.id as Int, now)
		task.subject = "Minimal task, no comments"
		// Not setting: content, priority, isPrivate, dueAt, remindAt, assignee

		assertEquals(0, task.commentList.size, "Minimal task should have 0 comments initially")

		taskRepo.store(task, user.id, now.plusMinutes(1))

		// Load and verify defaults/nulls
		val loaded = taskRepo.get(task.id)!!
		assertNotNull(loaded)
		assertEquals("Minimal task, no comments", loaded.subject)
		assertNull(loaded.content, "content should be null")
		assertNull(loaded.priority, "priority should be null")
		assertNull(loaded.private, "private should be null")
		assertNull(loaded.dueAt, "dueAt should be null")
		assertNull(loaded.remindAt, "remindAt should be null")
		assertNull(loaded.assignee, "assignee should be null")
		assertEquals(user.id, loaded.owner.id, "Owner should be creator")
		assertEquals(0, loaded.commentList.size, "Loaded minimal task should have 0 comments")
	}

	@Test
	fun testMultipleTasksAndQuerying() {
		val now = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS)

		// Create multiple tasks
		val task1 = taskRepo.create(tenant.id, user.id, now)
		task1.setCaseDef(simpleTaskDef)
		task1.setCaseStage(taskNewStage, user.id as Int, now)
		task1.subject = "Task 1"
		task1.priority = CodeTaskPriority.LOW
		val comment = task1.addComment()
		comment.text = "Comment for Task 1"
		taskRepo.store(task1, user.id, now)

		val task2 = taskRepo.create(tenant.id, user.id, now.plusMinutes(1))
		task2.setCaseDef(simpleTaskDef)
		task2.setCaseStage(taskInProgressStage, user.id as Int, now.plusMinutes(1))
		task2.subject = "Task 2"
		task2.priority = CodeTaskPriority.URGENT
		task2.assignee = user1
		val comment1 = task2.addComment()
		comment1.text = "First comment for Task 2"
		val comment2 = task2.addComment()
		comment2.text = "Second comment for Task 2"
		taskRepo.store(task2, user.id, now.plusMinutes(1))

		// Test getAll
		val allTasks = taskRepo.getAll(tenant.id)
		assertTrue(allTasks.any { it == task1.id }, "All tasks should include task1")
		assertTrue(allTasks.any { it == task2.id }, "All tasks should include task2")

		val retrievedTask1Id = allTasks.find { it == task1.id }!!
		val retrievedTask1 = taskRepo.get(retrievedTask1Id)
		assertEquals(1, retrievedTask1!!.commentList.size)

		val retrievedTask2Id = allTasks.find { it == task2.id }!!
		val retrievedTask2 = taskRepo.get(retrievedTask2Id)
		assertEquals(2, retrievedTask2!!.commentList.size)

		// Test getByForeignKey for assignee
// 		val user1Tasks = taskRepo.getByForeignKey("assigneeId", user1.id!!)
// 		assertEquals(1, user1Tasks.size, "Should find 1 task assigned to user1")
// 		assertEquals("Task 2", user1Tasks.first().subject)
	}

	@Test
	fun testSetValueByPath() {
		val task = taskRepo.create(tenant.id, user.id, OffsetDateTime.now())
		task.setCaseDef(simpleTaskDef)

		// Set simple property
		val newSubject = "Updated Subject via Path"
		task.setValueByPath("subject", newSubject)
		assertEquals(newSubject, task.subject)

		// Set reference property
		task.setValueByPath("assignee.id", user1.id)
		assertEquals(user1.id, task.assignee.id)

		// Set property on a part in a list
		val comment = task.addComment()
		val commentIndex = task.commentList.indexOf(comment)
		val newCommentText = "Updated comment text via path"
		task.setValueByPath("commentList[$commentIndex].text", newCommentText)
		assertEquals(newCommentText, comment.text)
	}
}
