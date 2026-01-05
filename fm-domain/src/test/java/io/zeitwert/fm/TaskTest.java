package io.zeitwert.fm;

import dddrive.app.doc.model.enums.CodeCaseStage;
import dddrive.app.doc.model.enums.CodeCaseStageEnum;
import io.crnk.core.queryspec.FilterOperator;
import io.crnk.core.queryspec.PathSpec;
import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.dddrive.app.model.SessionContext;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.oe.model.ObjUser;
import io.zeitwert.fm.oe.model.ObjUserRepository;
import io.zeitwert.fm.task.model.DocTask;
import io.zeitwert.fm.task.model.DocTaskRepository;
import io.zeitwert.fm.task.model.enums.CodeTaskPriority;
import io.zeitwert.test.TestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test")
public class TaskTest {

	static final OffsetDateTime DueDate = OffsetDateTime.of(2023, 9, 8, 0, 0, 0, 0, ZoneOffset.ofHours(2));

	static final String USER_EMAIL = "cc@zeitwert.io";
	static final String TEST_ACCOUNT = "TA";

	static ObjUser RelatedTo;
	static ObjAccount Account;
	static CodeCaseStage StageNew;
	static CodeCaseStage StageProgress;
	static CodeCaseStage StageDone;
	static CodeTaskPriority PrioNormal;
	static CodeTaskPriority PrioHigh;

	@Autowired
	private SessionContext sessionContext;

	@Autowired
	private ObjUserRepository userRepository;

	@Autowired
	private ObjAccountRepository accountRepository;

	@Autowired
	private DocTaskRepository taskRepository;

	@Test
	public void testTask() throws Exception {

		this.getTestData(sessionContext);

		Object userId = sessionContext.getUserId();
		OffsetDateTime now = sessionContext.getCurrentTime();

		assertNotNull(this.taskRepository, "taskRepository not null");
		assertEquals("doc_task", this.taskRepository.getAggregateType().getId());

		DocTask taskA1 = this.taskRepository.create();

		assertNotNull(taskA1, "task not null");
		assertNotNull(taskA1.getId(), "id not null");
		assertNotNull(taskA1.getTenantId(), "tenant not null");

		Object taskA_id = taskA1.getId();
		Integer taskA_idHash = System.identityHashCode(taskA1);

		assertNotNull(taskA1.getMeta().getCreatedByUserId(), "createdByUser not null");
		assertNotNull(taskA1.getMeta().getCreatedAt(), "createdAt not null");

		this.initTask1(taskA1, userId, now);
		assertEquals(taskA1.getMeta().getCaseStage(), StageNew);
		assertTrue(taskA1.getMeta().isInWork());
		this.checkTask1(taskA1);

		this.taskRepository.store(taskA1);
		taskA1 = null;

		DocTask taskA2 = this.taskRepository.load(taskA_id);
		Integer taskA2_idHash = System.identityHashCode(taskA2);

		assertNotEquals(taskA_idHash, taskA2_idHash);
		assertNotNull(taskA2.getMeta().getModifiedByUserId(), "modifiedByUser not null");
		assertNotNull(taskA2.getMeta().getModifiedAt(), "modifiedAt not null");
		assertEquals(taskA2.getMeta().getCaseStage(), StageNew);
		assertTrue(taskA2.getMeta().isInWork());

		this.checkTask1(taskA2);

		this.initTask2(taskA2, userId, now);
		assertTrue(taskA2.getMeta().isInWork());

		this.taskRepository.store(taskA2);
		taskA2 = null;

		DocTask taskA3 = this.taskRepository.load(taskA_id);

		assertEquals(taskA3.getMeta().getCaseStage(), StageProgress);
		assertTrue(taskA3.getMeta().isInWork());

		this.checkTask2(taskA3);

		this.initTask1(taskA3, userId, now);
		taskA3.getMeta().setCaseStage(StageDone, userId, now);
		assertFalse(taskA3.getMeta().isInWork());

		this.taskRepository.store(taskA3);
		taskA3 = null;

		DocTask taskA4 = this.taskRepository.get(taskA_id);

		assertEquals(taskA4.getMeta().getCaseStage(), StageDone);
		assertFalse(taskA4.getMeta().isInWork());

		this.checkTask1(taskA4);

	}

	private void getTestData(SessionContext sessionContext) {
		RelatedTo = this.userRepository.getByEmail(USER_EMAIL).get();
		assertNotNull(RelatedTo, "relatedTo");
		Account = this.accountRepository.get(this.accountRepository.find(null).getFirst());
		assertNotNull(Account, "account");
		StageNew = CodeCaseStageEnum.getCaseStage("task.new");
		StageProgress = CodeCaseStageEnum.getCaseStage("task.progress");
		StageDone = CodeCaseStageEnum.getCaseStage("task.done");
		PrioNormal = CodeTaskPriority.getPriority("normal");
		PrioHigh = CodeTaskPriority.getPriority("high");
	}

	private void initTask1(DocTask task, Object userId, OffsetDateTime timestamp) {
		task.getMeta().setCaseStage(CodeCaseStageEnum.getCaseStage("task.new"), userId, timestamp);
		task.setRelatedToId(RelatedTo.getId());
		task.setAccountId(Account.getId());
		task.setSubject("Todo");
		task.setContent("content");
		task.setPrivate(false);
		task.setPriority(PrioNormal);
		task.setDueAt(DueDate);
	}

	private void checkTask1(DocTask task) {
		assertEquals(task.getRelatedToId(), RelatedTo.getId());
		assertEquals(Account.getId(), task.getAccountId(), "account id");
		assertEquals(Account.getId(), task.getAccountId(), "account id");
		assertEquals("Todo", task.getSubject());
		assertEquals("content", task.getContent());
		assertEquals(false, task.isPrivate());
		assertEquals(task.getPriority(), PrioNormal);
		assertEquals(DueDate, task.getDueAt());
	}

	private void initTask2(DocTask task, Object userId, OffsetDateTime timestamp) {
		assertEquals(Account.getId(), task.getAccountId(), "account id");
		assertEquals(Account.getId(), task.getAccountId(), "account id");
		task.setSubject("Todos");
		task.setContent("contents");
		task.setPrivate(true);
		task.setPriority(PrioHigh);
		task.setDueAt(DueDate.plusDays(4));
		task.getMeta().setCaseStage(StageProgress, userId, timestamp);
	}

	private void checkTask2(DocTask task) {
		assertEquals("contents", task.getContent());
		assertEquals(true, task.isPrivate());
		assertEquals(task.getPriority(), PrioHigh);
		assertEquals(task.getDueAt(), DueDate.plusDays(4));
		assertEquals(task.getMeta().getCaseStage(), StageProgress);
	}

	@Test
	public void testFindByRelatedToId() {
		this.getTestData(sessionContext);

		Object userId = sessionContext.getUserId();
		OffsetDateTime now = sessionContext.getCurrentTime();

		// Create two tasks related to the same user (RelatedTo)
		DocTask task1 = this.taskRepository.create();
		this.initTask1(task1, userId, now);
		this.taskRepository.store(task1);
		Object task1Id = task1.getId();

		DocTask task2 = this.taskRepository.create();
		this.initTask1(task2, userId, now);
		task2.setSubject("Task 2");
		this.taskRepository.store(task2);
		Object task2Id = task2.getId();

		// Create a task related to a different object (Account)
		DocTask task3 = this.taskRepository.create();
		task3.getMeta().setCaseStage(CodeCaseStageEnum.getCaseStage("task.new"), userId, now);
		task3.setRelatedToId(Account.getId());
		task3.setAccountId(Account.getId());
		task3.setSubject("Task 3 - different related");
		task3.setContent("content");
		task3.setPrivate(false);
		task3.setPriority(PrioNormal);
		task3.setDueAt(DueDate);
		this.taskRepository.store(task3);
		Object task3Id = task3.getId();

		// Find tasks by relatedToId using QuerySpec
		QuerySpec querySpec = new QuerySpec(DocTask.class);
		querySpec.addFilter(PathSpec.of("relatedToId").filter(FilterOperator.EQ, RelatedTo.getId()));

		List<Object> foundIds = this.taskRepository.find(querySpec);

		// Should find task1 and task2 (both related to RelatedTo user)
		assertTrue(foundIds.contains(task1Id), "Should find task1");
		assertTrue(foundIds.contains(task2Id), "Should find task2");
		assertFalse(foundIds.contains(task3Id), "Should NOT find task3 (different relatedToId)");

		// Find tasks related to Account
		QuerySpec querySpec2 = new QuerySpec(DocTask.class);
		querySpec2.addFilter(PathSpec.of("relatedToId").filter(FilterOperator.EQ, Account.getId()));

		List<Object> foundIds2 = this.taskRepository.find(querySpec2);

		// Should find task3 (related to Account)
		assertTrue(foundIds2.contains(task3Id), "Should find task3");
		assertFalse(foundIds2.contains(task1Id), "Should NOT find task1");
		assertFalse(foundIds2.contains(task2Id), "Should NOT find task2");
	}

}
