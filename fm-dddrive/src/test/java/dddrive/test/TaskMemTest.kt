package dddrive.test

import dddrive.app.ddd.model.SessionContext
import dddrive.app.doc.model.enums.CodeCaseDef
import dddrive.app.doc.model.enums.CodeCaseDefEnum
import dddrive.app.doc.model.enums.CodeCaseStage
import dddrive.app.doc.model.enums.CodeCaseStageEnum
import dddrive.ddd.core.model.AggregateSPI
import dddrive.ddd.path.setValueByPath
import dddrive.ddd.property.model.PropertyChangeListener
import dddrive.domain.oe.model.ObjTenantRepository
import dddrive.domain.oe.model.ObjUser
import dddrive.domain.oe.model.ObjUserRepository
import dddrive.domain.task.model.DocTaskRepository
import dddrive.domain.task.model.enums.CodeTaskPriority
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

@SpringBootTest(classes = [TestApplication::class])
@ActiveProfiles("test")
class TaskMemTest : PropertyChangeListener {

	@Autowired
	private lateinit var sessionContext: SessionContext

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
		// println("PropertyChange { op: $op, path: $path, value: $value, oldValue: $oldValue, isInCalc:
		// $isInCalc }")
	}

	@BeforeEach
	fun setUp() {
		user1 = userRepo.getByEmail("user1@example.com").orElseGet {
			val newUser = userRepo.create()
			newUser.name = "Test User 1"
			newUser.email = "user1@example.com"
			userRepo.store(newUser)
			newUser
		}
		Assertions.assertNotNull(user1, "user1 should exist")

		// Get case definitions and stages
		simpleTaskDef = caseDefEnum.getItem("simpleTask")
		Assertions.assertNotNull(simpleTaskDef, "Simple task case definition should exist")

		taskNewStage = caseStageEnum.getItem("task.new")
		Assertions.assertNotNull(taskNewStage, "Task new stage should exist")

		taskInProgressStage = caseStageEnum.getItem("task.inProgress")
		Assertions.assertNotNull(taskInProgressStage, "Task in progress stage should exist")

		taskDoneStage = caseStageEnum.getItem("task.done")
		Assertions.assertNotNull(taskDoneStage, "Task done stage should exist")
	}

	@Test
	fun testTaskWorkflowWithComments() { // Renamed to reflect new tests
		Assertions.assertEquals("docTask", taskRepo.aggregateType.id)

		val now = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS)
		val dueDate = now.plusDays(7)
		val remindDate = now.plusDays(5)

		// Create a new task
		val task = taskRepo.create()
		(task as AggregateSPI).addPropertyChangeListener(this)
		val taskId = task.id
		Assertions.assertNotNull(taskId, "Task ID should not be null")

		// Initially, the task should be frozen until caseDef is set
		// Assertions.assertTrue(task.isFrozen, "Task should be frozen initially")

		// Set initial case stage - this will unfreeze the task
		task.meta.setCaseStage(taskNewStage, sessionContext.userId as Int, now)
		Assertions.assertFalse(task.isFrozen, "Task unfrozen after setting caseDef")
		Assertions.assertEquals(simpleTaskDef, task.meta.caseDef, "CaseDef set")
		Assertions.assertEquals(taskNewStage, task.meta.caseStage, "CaseStage 'New'")
		Assertions.assertTrue(task.meta.isInWork, "Task is in work (not terminal)")

		// Set task-specific properties
		task.subject = "Implement new feature with comments"
		task.content = "Implement the user authentication module with OAuth2 support and add comment functionality."
		task.priority = CodeTaskPriority.HIGH
		task.isPrivate = false
		task.dueAt = dueDate
		task.remindAt = remindDate
		task.assigneeId = user1.id

		// Verify properties
		Assertions.assertEquals("Implement new feature with comments", task.subject)
		Assertions.assertEquals(
			"Implement the user authentication module with OAuth2 support and add comment functionality.",
			task.content,
		)
		Assertions.assertEquals(CodeTaskPriority.HIGH, task.priority)
		Assertions.assertEquals(false, task.isPrivate)
		Assertions.assertEquals(dueDate, task.dueAt)
		Assertions.assertEquals(remindDate, task.remindAt)
		Assertions.assertEquals(user1.id, task.assigneeId)
		Assertions.assertEquals(
			"Implement new feature with comments",
			task.caption,
			"Caption should be derived from subject",
		)

		// Check initial transition
		Assertions.assertEquals(1, task.meta.transitionList.size, "Should have one initial transition")
		val initialTransition = task.meta.transitionList.first()
		Assertions.assertNull(initialTransition.oldCaseStage, "Initial transition old stage should be null")
		Assertions.assertEquals(taskNewStage, initialTransition.newCaseStage)
		Assertions.assertEquals(sessionContext.userId, initialTransition.userId) // Compare IDs for user objects
		Assertions.assertEquals(0, task.commentList.size, "Initially, task should have no comments")

		// Add first comment
		val commentText1 = "This is the first comment on the task."
		val comment1TimeBefore = OffsetDateTime.now()
		val comment1 = task.commentList.add()
		comment1.text = commentText1
		val comment1TimeAfter = OffsetDateTime.now()

		Assertions.assertNotNull(comment1.id, "Comment 1 ID should not be null")
		Assertions.assertEquals(commentText1, comment1.text, "Comment 1 text should match")
		Assertions.assertNotNull(comment1.createdAt, "Comment 1 createdAt should not be null")
		Assertions.assertTrue(
			comment1.createdAt!! >= comment1TimeBefore && comment1.createdAt!! <= comment1TimeAfter,
			"Comment 1 createdAt timestamp should be recent",
		)

		Assertions.assertEquals(1, task.commentList.size, "Task should have 1 comment")
		Assertions.assertTrue(task.commentList.contains(comment1), "Task comment list should contain comment1")

		val commentText2 = "User1 adds a follow-up comment."
		val comment2TimeBefore = OffsetDateTime.now()
		val comment2 = task.commentList.add()
		comment2.text = commentText2
		val comment2TimeAfter = OffsetDateTime.now()

		Assertions.assertNotNull(comment2.id, "Comment 2 ID should not be null")
		Assertions.assertEquals(commentText2, comment2.text, "Comment 2 text should match")
		Assertions.assertTrue(
			comment2.createdAt!! >= comment2TimeBefore && comment2.createdAt!! <= comment2TimeAfter,
			"Comment 2 createdAt timestamp should be recent",
		)
		Assertions.assertEquals(2, task.commentList.size, "Task should have 2 comments")

		// Store the task
		taskRepo.store(task)

		// Load and verify
		val loadedTask = taskRepo.get(taskId)
		Assertions.assertNotNull(loadedTask)
		Assertions.assertTrue(loadedTask.isFrozen, "Loaded task should be frozen (read-only)")

		// Verify all properties persisted correctly
		Assertions.assertEquals("Implement new feature with comments", loadedTask.subject)
		Assertions.assertEquals(CodeTaskPriority.HIGH, loadedTask.priority)
		Assertions.assertEquals(simpleTaskDef, loadedTask.meta.caseDef)
		Assertions.assertEquals(taskNewStage, loadedTask.meta.caseStage)
		Assertions.assertEquals(user1.id, loadedTask.assigneeId)

		// Verify comments persisted
		Assertions.assertEquals(2, loadedTask.commentList.size, "Loaded task should have 2 comments")
		val loadedComment1 = loadedTask.commentList.find { it.id == comment1.id }
		Assertions.assertNotNull(loadedComment1, "Loaded comment 1 should exist")
		Assertions.assertEquals(commentText1, loadedComment1!!.text)
		Assertions.assertNotNull(loadedComment1.createdAt)

		val loadedComment2 = loadedTask.commentList.find { it.id == comment2.id }
		Assertions.assertNotNull(loadedComment2, "Loaded comment 2 should exist")
		Assertions.assertEquals(commentText2, loadedComment2!!.text)
		Assertions.assertNotNull(loadedComment2.createdAt)

		// Should have 2 transitions now (initial + store)
		Assertions.assertEquals(2, loadedTask.meta.transitionList.size)

		// Test workflow progression
		val mutableTask = taskRepo.load(taskId) // load for editing
		Assertions.assertFalse(mutableTask.isFrozen, "Loaded for edit task should not be frozen")

		// Move task to "In Progress"
		val progressTime = now.plusMinutes(10)
		mutableTask.meta.setCaseStage(taskInProgressStage, user1.id as Int, progressTime)
		Assertions.assertEquals(taskInProgressStage, mutableTask.meta.caseStage)

		// Add a comment after stage change
		val commentText3 = "Task is now in progress."
		val comment3 = mutableTask.commentList.add()
		comment3.text = commentText3
		Assertions.assertEquals(
			3,
			mutableTask.commentList.size,
			"Mutable task should have 3 comments after adding one in progress",
		)

		// Store after stage change
		taskRepo.store(mutableTask)

		// Verify stage change and new comment
		val taskAfterProgress = taskRepo.get(taskId)
		Assertions.assertEquals(taskInProgressStage, taskAfterProgress.meta.caseStage)
		Assertions.assertEquals(3, taskAfterProgress.meta.transitionList.size) // initial + store + stage change
		Assertions.assertEquals(
			3,
			taskAfterProgress.commentList.size,
			"Task after progress should have 3 comments",
		)
		val loadedComment3 = taskAfterProgress.commentList.find { it.id == comment3.id }
		Assertions.assertNotNull(loadedComment3, "Loaded comment 3 should exist")
		Assertions.assertEquals(commentText3, loadedComment3!!.text)

		// Check the stage change transition
		val stageChangeTransition = taskAfterProgress.meta.transitionList[taskAfterProgress.meta.transitionList.size - 1]
		Assertions.assertEquals(taskNewStage, stageChangeTransition.oldCaseStage)
		Assertions.assertEquals(taskInProgressStage, stageChangeTransition.newCaseStage)
		Assertions.assertEquals(sessionContext.userId, stageChangeTransition.userId)

		// Test removing a comment
		val taskToEditComments = taskRepo.load(taskId)
		Assertions.assertNotNull(
			taskToEditComments.commentList.find { it.id == comment1.id },
			"Comment 1 should exist before removal",
		)
		taskToEditComments.commentList.remove(comment1.id)
		Assertions.assertEquals(
			2,
			taskToEditComments.commentList.size,
			"Task should have 2 comments after removal",
		)
		Assertions.assertNull(
			taskToEditComments.commentList.find { it.id == comment1.id },
			"Comment 1 should not exist after removal",
		)
		taskRepo.store(taskToEditComments)

		val taskAfterCommentRemoval = taskRepo.get(taskId)
		Assertions.assertEquals(
			2,
			taskAfterCommentRemoval.commentList.size,
			"Loaded task should have 2 comments after removal",
		)
		Assertions.assertNull(
			taskAfterCommentRemoval.commentList.find { it.id == comment1.id },
			"Loaded comment 1 should not exist after removal",
		)
		Assertions.assertNotNull(
			taskAfterCommentRemoval.commentList.find { it.id == comment2.id },
			"Loaded comment 2 should still exist",
		)
		Assertions.assertNotNull(
			taskAfterCommentRemoval.commentList.find { it.id == comment3.id },
			"Loaded comment 3 should still exist",
		)

		// Complete the task
		val completeTask = taskRepo.load(taskId)
		val completeTime = now.plusMinutes(30)
		completeTask.meta.setCaseStage(taskDoneStage, user1.id as Int, completeTime)
		Assertions.assertEquals(taskDoneStage, completeTask.meta.caseStage)
		Assertions.assertFalse(completeTask.meta.isInWork, "Done task should not be in work (terminal stage)")

		taskRepo.store(completeTask)

		// Verify final state
		val completedTask = taskRepo.get(taskId)
		Assertions.assertEquals(taskDoneStage, completedTask.meta.caseStage)
		Assertions.assertFalse(completedTask.meta.isInWork)
		// Transitions: initial, store1, stage_change_store, comment_removal_store,
		// final_stage_change_store
		Assertions.assertEquals(5, completedTask.meta.transitionList.size)
		Assertions.assertEquals(2, completedTask.commentList.size, "Completed task should still have 2 comments")
	}

	@Test
	fun testTaskWithMinimalDataAndNoComments() { // Renamed
		val now = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS)

		// Create task with minimal data
		val task = taskRepo.create()
		task.meta.setCaseStage(taskNewStage, sessionContext.userId as Int, now)
		task.subject = "Minimal task, no comments"
		// Not setting: content, priority, isPrivate, dueAt, remindAt, assignee

		Assertions.assertEquals(0, task.commentList.size, "Minimal task should have 0 comments initially")

		taskRepo.store(task)

		// Load and verify defaults/nulls
		val loaded = taskRepo.get(task.id)
		Assertions.assertNotNull(loaded)
		Assertions.assertEquals("Minimal task, no comments", loaded.subject)
		Assertions.assertNull(loaded.content, "content should be null")
		Assertions.assertNull(loaded.priority, "priority should be null")
		Assertions.assertNull(loaded.isPrivate, "private should be null")
		Assertions.assertNull(loaded.dueAt, "dueAt should be null")
		Assertions.assertNull(loaded.remindAt, "remindAt should be null")
		Assertions.assertNull(loaded.assigneeId, "assignee should be null")
		Assertions.assertEquals(sessionContext.userId, loaded.ownerId, "Owner should be creator")
		Assertions.assertEquals(0, loaded.commentList.size, "Loaded minimal task should have 0 comments")
	}

	@Test
	fun testMultipleTasksAndQuerying() {
		val now = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS)

		// Create multiple tasks
		val task1 = taskRepo.create()
		task1.meta.setCaseStage(taskNewStage, sessionContext.userId as Int, now)
		task1.subject = "Task 1"
		task1.priority = CodeTaskPriority.LOW
		val comment = task1.commentList.add()
		comment.text = "Comment for Task 1"
		taskRepo.store(task1)

		val task2 = taskRepo.create()
		task2.meta.setCaseStage(taskInProgressStage, sessionContext.userId as Int, now.plusMinutes(1))
		task2.subject = "Task 2"
		task2.priority = CodeTaskPriority.URGENT
		task2.assigneeId = user1.id
		val comment1 = task2.commentList.add()
		comment1.text = "First comment for Task 2"
		val comment2 = task2.commentList.add()
		comment2.text = "Second comment for Task 2"
		taskRepo.store(task2)

		// // Test getAll
		// val allTasks = taskRepo.find()
		// assertTrue(allTasks.any { it == task1.id }, "All tasks should include task1")
		// assertTrue(allTasks.any { it == task2.id }, "All tasks should include task2")
		//
		// val retrievedTask1Id = allTasks.find { it == task1.id }!!
		// val retrievedTask1 = taskRepo.get(retrievedTask1Id)
		// assertEquals(1, retrievedTask1.commentList.size)
		//
		// val retrievedTask2Id = allTasks.find { it == task2.id }!!
		// val retrievedTask2 = taskRepo.get(retrievedTask2Id)
		// assertEquals(2, retrievedTask2.commentList.size)

		// Test getByForeignKey for assignee
		// 		val user1Tasks = taskRepo.getByForeignKey("assigneeId", user1.id!!)
		// 		assertEquals(1, user1Tasks.size, "Should find 1 task assigned to user1")
		// 		assertEquals("Task 2", user1Tasks.first().subject)
	}

	@Test
	fun testSetValueByPath() {
		val now = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS)
		val task = taskRepo.create()
		task.meta.setCaseStage(taskNewStage, sessionContext.userId as Int, now)

		// Set simple property
		val content = "Updated Subject via Path"
		task.setValueByPath("content", content)
		Assertions.assertEquals(content, task.content)

		// Set simple property
		val newSubject = "Updated Subject via Path"
		task.setValueByPath("subject", newSubject)
		Assertions.assertEquals(newSubject, task.subject)

		// Access isPrivate first to register the delegate property
		Assertions.assertNull(task.isPrivate)
		task.setValueByPath("isPrivate", true)
		Assertions.assertEquals(true, task.isPrivate)

		// Set reference property - use Id suffix for setters
		task.setValueByPath("assigneeId", user1.id)
		Assertions.assertEquals(user1.id, task.assigneeId)

		// Set property on a part in a list
		val comment = task.commentList.add()
		val commentIndex = task.commentList.indexOf(comment)
		val newCommentText = "Updated comment text via path"
		// Access text first to register the delegate property
		Assertions.assertNull(comment.text)
		task.setValueByPath("commentList[$commentIndex].text", newCommentText)
		Assertions.assertEquals(newCommentText, comment.text)
	}

}
