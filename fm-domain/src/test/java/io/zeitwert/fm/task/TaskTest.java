package io.zeitwert.fm.task;

import io.dddrive.core.doc.model.enums.CodeCaseStage;
import io.dddrive.core.doc.model.enums.CodeCaseStageEnum;
import io.dddrive.core.oe.model.ObjUser;
import io.zeitwert.dddrive.app.model.RequestContext;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.doc.model.base.FMDocBase;
import io.zeitwert.fm.oe.model.ObjUserFMRepository;
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
	private RequestContext requestCtx;

	@Autowired
	private ObjUserFMRepository userRepository;

	@Autowired
	private ObjAccountRepository accountRepo;

	@Autowired
	private DocTaskRepository taskRepository;

	@Test
	public void testTask() throws Exception {

		this.getTestData(requestCtx);

		Object tenantId = requestCtx.getTenantId();
		Object userId = requestCtx.getUserId();
		OffsetDateTime now = requestCtx.getCurrentTime();

		assertNotNull(this.taskRepository, "taskRepository not null");
		assertEquals("doc_task", this.taskRepository.getAggregateType().getId());

		DocTask taskA1 = this.taskRepository.create(tenantId, userId, now);

		assertNotNull(taskA1, "task not null");
		assertNotNull(taskA1.getId(), "id not null");
		assertNotNull(taskA1.getTenantId(), "tenant not null");

		Object taskA_id = taskA1.getId();
		Integer taskA_idHash = System.identityHashCode(taskA1);

		assertNotNull(taskA1.getMeta().getCreatedByUser(), "createdByUser not null");
		assertNotNull(taskA1.getMeta().getCreatedAt(), "createdAt not null");

		this.initTask1(taskA1, userId, now);
		assertEquals(taskA1.getMeta().getCaseStage(), StageNew);
		assertTrue(taskA1.getMeta().isInWork());
		this.checkTask1(taskA1);

		this.taskRepository.store(taskA1, userId, now);
		taskA1 = null;

		DocTask taskA2 = this.taskRepository.load(taskA_id);
		Integer taskA2_idHash = System.identityHashCode(taskA2);

		assertNotEquals(taskA_idHash, taskA2_idHash);
		assertNotNull(taskA2.getMeta().getModifiedByUser(), "modifiedByUser not null");
		assertNotNull(taskA2.getMeta().getModifiedAt(), "modifiedAt not null");
		assertEquals(taskA2.getMeta().getCaseStage(), StageNew);
		assertTrue(taskA2.getMeta().isInWork());

		this.checkTask1(taskA2);

		this.initTask2(taskA2, now);
		assertTrue(taskA2.getMeta().isInWork());

		this.taskRepository.store(taskA2, userId, now);
		taskA2 = null;

		DocTask taskA3 = this.taskRepository.load(taskA_id);

		assertEquals(taskA3.getMeta().getCaseStage(), StageProgress);
		assertTrue(taskA3.getMeta().isInWork());

		this.checkTask2(taskA3);

		this.initTask1(taskA3, userId, now);
		taskA3.setCaseStage(StageDone, userId, now);
		assertFalse(taskA3.getMeta().isInWork());

		this.taskRepository.store(taskA3, userId, now);
		taskA3 = null;

		DocTask taskA4 = this.taskRepository.get(taskA_id);

		assertEquals(taskA4.getMeta().getCaseStage(), StageDone);
		assertFalse(taskA4.getMeta().isInWork());

		this.checkTask1(taskA4);

	}

	private void getTestData(RequestContext requestCtx) {
		RelatedTo = this.userRepository.getByEmail(USER_EMAIL).get();
		assertNotNull(RelatedTo, "relatedTo");
		Object tenantId = requestCtx.getTenantId();
		Account = this.accountRepo.get(this.accountRepo.getAll(tenantId).get(0));
		assertNotNull(Account, "account");
		StageNew = CodeCaseStageEnum.getCaseStage("task.new");
		StageProgress = CodeCaseStageEnum.getCaseStage("task.progress");
		StageDone = CodeCaseStageEnum.getCaseStage("task.done");
		PrioNormal = CodeTaskPriority.getPriority("normal");
		PrioHigh = CodeTaskPriority.getPriority("high");
	}

	private void initTask1(DocTask task, Object userId, OffsetDateTime timestamp) {
		task.setCaseStage(CodeCaseStageEnum.getCaseStage("task.new"), userId, timestamp);
		task.relatedToId = (Integer) RelatedTo.getId();
		((FMDocBase) task).setAccountId((Integer) Account.getId());
		task.subject = "Todo";
		task.content = "content";
		task.isPrivate = false;
		task.priority = PrioNormal;
		task.dueAt = DueDate;
	}

	private void checkTask1(DocTask task) {
		assertEquals(task.relatedToId, RelatedTo.getId());
		assertEquals(Account.getId(), ((FMDocBase) task).getAccountId(), "account id");
		assertEquals(Account.getId(), task.account.getId(), "account id");
		assertEquals("Todo", task.subject);
		assertEquals("content", task.content);
		assertEquals(false, task.isPrivate);
		assertEquals(task.priority, PrioNormal);
		assertEquals(DueDate, task.dueAt);
	}

	private void initTask2(DocTask task, OffsetDateTime timestamp) {
		assertEquals(Account.getId(), ((FMDocBase) task).getAccountId(), "account id");
		assertEquals(Account.getId(), task.account.getId(), "account id");
		task.subject = "Todos";
		task.content = "contents";
		task.isPrivate = true;
		task.priority = PrioHigh;
		task.dueAt = DueDate.plusDays(4);
		task.setCaseStage(StageProgress, null, timestamp);
	}

	private void checkTask2(DocTask task) {
		assertEquals("Todos", task.subject);
		assertEquals("contents", task.content);
		assertEquals(true, task.isPrivate);
		assertEquals(task.priority, PrioHigh);
		assertEquals(task.dueAt, DueDate.plusDays(4));
		assertEquals(task.getMeta().getCaseStage(), StageProgress);
	}

}
