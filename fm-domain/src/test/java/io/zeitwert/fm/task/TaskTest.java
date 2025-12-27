package io.zeitwert.fm.task;

import dddrive.app.doc.model.enums.CodeCaseStage;
import dddrive.app.doc.model.enums.CodeCaseStageEnum;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.app.model.RequestContextFM;
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
	private RequestContextFM requestCtx;

	@Autowired
	private ObjUserRepository userRepository;

	@Autowired
	private ObjAccountRepository accountRepository;

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

		assertNotNull(taskA1.getMeta().getCreatedByUserId(), "createdByUser not null");
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
		assertNotNull(taskA2.getMeta().getModifiedByUserId(), "modifiedByUser not null");
		assertNotNull(taskA2.getMeta().getModifiedAt(), "modifiedAt not null");
		assertEquals(taskA2.getMeta().getCaseStage(), StageNew);
		assertTrue(taskA2.getMeta().isInWork());

		this.checkTask1(taskA2);

		this.initTask2(taskA2, userId, now);
		assertTrue(taskA2.getMeta().isInWork());

		this.taskRepository.store(taskA2, userId, now);
		taskA2 = null;

		DocTask taskA3 = this.taskRepository.load(taskA_id);

		assertEquals(taskA3.getMeta().getCaseStage(), StageProgress);
		assertTrue(taskA3.getMeta().isInWork());

		this.checkTask2(taskA3);

		this.initTask1(taskA3, userId, now);
		taskA3.getMeta().setCaseStage(StageDone, userId, now);
		assertFalse(taskA3.getMeta().isInWork());

		this.taskRepository.store(taskA3, userId, now);
		taskA3 = null;

		DocTask taskA4 = this.taskRepository.get(taskA_id);

		assertEquals(taskA4.getMeta().getCaseStage(), StageDone);
		assertFalse(taskA4.getMeta().isInWork());

		this.checkTask1(taskA4);

	}

	private void getTestData(RequestContextFM requestCtx) {
		RelatedTo = this.userRepository.getByEmail(USER_EMAIL).get();
		assertNotNull(RelatedTo, "relatedTo");
		Object tenantId = requestCtx.getTenantId();
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

}
