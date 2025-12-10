
package io.zeitwert.fm.task;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.account.service.api.ObjAccountCache;
import io.zeitwert.fm.server.Application;
import io.zeitwert.fm.task.model.DocTask;
import io.zeitwert.fm.task.model.DocTaskRepository;
import io.zeitwert.fm.task.model.enums.CodeTaskPriority;
import io.zeitwert.fm.task.model.enums.CodeTaskPriorityEnum;
import io.dddrive.app.model.RequestContext;
import io.dddrive.doc.model.enums.CodeCaseStage;
import io.dddrive.doc.model.enums.CodeCaseStageEnum;
import io.dddrive.oe.model.ObjUser;
import io.dddrive.oe.service.api.ObjUserCache;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@SpringBootTest(classes = Application.class)
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
	private ObjUserCache userCache;

	@Autowired
	private ObjAccountRepository accountRepo;

	@Autowired
	private ObjAccountCache accountCache;

	@Autowired
	private DocTaskRepository taskRepository;

	@Test
	public void testTask() throws Exception {

		this.getTestData();

		assertTrue(this.taskRepository != null, "taskRepository not null");
		assertEquals("doc_task", this.taskRepository.getAggregateType().getId());

		DocTask taskA1 = this.taskRepository.create(this.requestCtx.getTenantId());

		assertNotNull(taskA1, "task not null");
		assertNotNull(taskA1.getId(), "id not null");
		assertNotNull(taskA1.getTenant(), "tenant not null");

		Integer taskA_id = taskA1.getId();
		Integer taskA_idHash = System.identityHashCode(taskA1);

		assertNotNull(taskA1.getMeta().getCreatedByUser(), "createdByUser not null");
		assertNotNull(taskA1.getMeta().getCreatedAt(), "createdAt not null");

		this.initTask1(taskA1);
		assertEquals(taskA1.getMeta().getCaseStage(), StageNew);
		assertEquals(taskA1.getMeta().isInWork(), true);
		this.checkTask1(taskA1);

		this.taskRepository.store(taskA1);
		taskA1 = null;

		DocTask taskA2 = this.taskRepository.load(taskA_id);
		Integer taskA2_idHash = System.identityHashCode(taskA2);

		assertNotEquals(taskA_idHash, taskA2_idHash);
		assertNotNull(taskA2.getMeta().getModifiedByUser(), "modifiedByUser not null");
		assertNotNull(taskA2.getMeta().getModifiedAt(), "modifiedAt not null");
		assertEquals(taskA2.getMeta().getCaseStage(), StageNew);
		assertEquals(taskA2.getMeta().isInWork(), true);

		this.checkTask1(taskA2);

		this.initTask2(taskA2);
		assertEquals(taskA2.getMeta().isInWork(), true);

		this.taskRepository.store(taskA2);
		taskA2 = null;

		DocTask taskA3 = this.taskRepository.load(taskA_id);

		assertEquals(taskA3.getMeta().getCaseStage(), StageProgress);
		assertEquals(taskA3.getMeta().isInWork(), true);

		this.checkTask2(taskA3);

		this.initTask1(taskA3);
		taskA3.setCaseStage(StageDone);
		assertEquals(taskA3.getMeta().isInWork(), false);

		this.taskRepository.store(taskA3);
		taskA3 = null;

		DocTask taskA4 = this.taskRepository.get(taskA_id);

		assertEquals(taskA4.getMeta().getCaseStage(), StageDone);
		assertEquals(taskA4.getMeta().isInWork(), false);

		this.checkTask1(taskA4);

	}

	private void getTestData() {
		RelatedTo = this.userCache.getByEmail(USER_EMAIL).get();
		assertNotNull(RelatedTo, "relatedTo");
		Account = this.accountCache.get(this.accountRepo.find(null).get(0).getId());
		assertNotNull(Account, "account");
		StageNew = CodeCaseStageEnum.getCaseStage("task.new");
		StageProgress = CodeCaseStageEnum.getCaseStage("task.progress");
		StageDone = CodeCaseStageEnum.getCaseStage("task.done");
		PrioNormal = CodeTaskPriorityEnum.getPriority("normal");
		PrioHigh = CodeTaskPriorityEnum.getPriority("high");
	}

	private void initTask1(DocTask task) {
		task.setCaseStage(CodeCaseStageEnum.getCaseStage("task.new"));
		task.setRelatedToId(RelatedTo.getId());
		task.setAccountId(Account.getId());
		task.setSubject("Todo");
		task.setContent("content");
		task.setIsPrivate(false);
		task.setPriority(PrioNormal);
		task.setDueAt(DueDate);
	}

	private void checkTask1(DocTask task) {
		assertEquals(task.getRelatedToId(), RelatedTo.getId());
		assertEquals(Account.getId(), task.getAccountId(), "account id");
		assertEquals(Account.getId(), task.getAccount().getId(), Account.getId(), "account id");
		assertEquals(task.getSubject(), "Todo");
		assertEquals(task.getContent(), "content");
		assertEquals(task.getIsPrivate(), false);
		assertEquals(task.getPriority(), PrioNormal);
		assertEquals(task.getDueAt(), DueDate);
	}

	private void initTask2(DocTask task) {
		assertEquals(Account.getId(), task.getAccountId(), "account id");
		assertEquals(Account.getId(), task.getAccount().getId(), "account id");
		task.setSubject("Todos");
		task.setContent("contents");
		task.setIsPrivate(true);
		task.setPriority(PrioHigh);
		task.setDueAt(DueDate.plusDays(4));
		task.setCaseStage(StageProgress);
	}

	private void checkTask2(DocTask task) {
		assertEquals(task.getSubject(), "Todos");
		assertEquals(task.getContent(), "contents");
		assertEquals(task.getIsPrivate(), true);
		assertEquals(task.getPriority(), PrioHigh);
		assertEquals(task.getDueAt(), DueDate.plusDays(4));
		assertEquals(task.getMeta().getCaseStage(), StageProgress);
	}

}
