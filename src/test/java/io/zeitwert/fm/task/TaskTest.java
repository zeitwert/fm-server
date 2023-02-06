
package io.zeitwert.fm.task;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.task.model.DocTask;
import io.zeitwert.fm.task.model.DocTaskRepository;
import io.zeitwert.fm.task.model.enums.CodeTaskPriority;
import io.zeitwert.fm.task.model.enums.CodeTaskPriorityEnum;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStage;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStageEnum;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.service.api.ObjUserCache;
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.server.Application;

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
	private DocTaskRepository taskRepository;

	@Test
	public void testTask() throws Exception {

		this.getTestData();

		assertTrue(this.taskRepository != null, "taskRepository not null");
		assertEquals("doc_task", this.taskRepository.getAggregateType().getId());

		DocTask task1a = this.taskRepository.create(this.requestCtx.getTenantId());

		assertNotNull(task1a, "task not null");
		assertNotNull(task1a.getId(), "id not null");
		assertNotNull(task1a.getTenant(), "tenant not null");
		assertEquals(task1a.getCaseStage(), StageNew);
		assertEquals(task1a.isInWork(), true);

		Integer task1Id = task1a.getId();
		Integer task1aIdHash = System.identityHashCode(task1a);

		assertNotNull(task1a.getMeta().getCreatedByUser(), "createdByUser not null");
		assertNotNull(task1a.getMeta().getCreatedAt(), "createdAt not null");

		this.initTask1(task1a);
		this.checkTask1(task1a);

		this.taskRepository.store(task1a);
		task1a = null;

		DocTask task1b = this.taskRepository.get(task1Id);
		Integer task1bIdHash = System.identityHashCode(task1b);

		assertNotEquals(task1aIdHash, task1bIdHash);
		assertNotNull(task1b.getMeta().getModifiedByUser(), "modifiedByUser not null");
		assertNotNull(task1b.getMeta().getModifiedAt(), "modifiedAt not null");
		assertEquals(task1b.getCaseStage(), StageNew);
		assertEquals(task1b.isInWork(), true);

		this.checkTask1(task1b);

		this.initTask2(task1b);
		assertEquals(task1b.isInWork(), true);

		this.taskRepository.store(task1b);
		task1b = null;

		DocTask task1c = this.taskRepository.get(task1Id);

		assertEquals(task1c.getCaseStage(), StageProgress);
		assertEquals(task1c.isInWork(), true);

		this.checkTask2(task1c);

		this.initTask1(task1c);
		task1c.setCaseStage(StageDone);
		assertEquals(task1c.isInWork(), false);

		this.taskRepository.store(task1c);
		task1c = null;

		DocTask task1d = this.taskRepository.get(task1Id);

		assertEquals(task1d.getCaseStage(), StageDone);
		assertEquals(task1d.isInWork(), false);

		this.checkTask1(task1d);

	}

	private void getTestData() {
		RelatedTo = this.userCache.getByEmail(USER_EMAIL).get();
		assertNotNull(RelatedTo, "relatedTo");
		Account = this.accountRepo.get(this.accountRepo.find(null).get(0).getId());
		assertNotNull(Account, "account");
		StageNew = CodeCaseStageEnum.getCaseStage("task.new");
		StageProgress = CodeCaseStageEnum.getCaseStage("task.progress");
		StageDone = CodeCaseStageEnum.getCaseStage("task.done");
		PrioNormal = CodeTaskPriorityEnum.getPriority("normal");
		PrioHigh = CodeTaskPriorityEnum.getPriority("high");
	}

	private void initTask1(DocTask task) {
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
		assertEquals(task.getAccountId(), Account.getId());
		assertEquals(task.getAccount().getId(), Account.getId());
		assertEquals(task.getSubject(), "Todo");
		assertEquals(task.getContent(), "content");
		assertEquals(task.getIsPrivate(), false);
		assertEquals(task.getPriority(), PrioNormal);
		assertEquals(task.getDueAt(), DueDate);
	}

	private void initTask2(DocTask task) {
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
		assertEquals(task.getCaseStage(), StageProgress);
	}

}
