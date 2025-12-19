package io.zeitwert.fm.doc;

import io.dddrive.core.doc.model.enums.CodeCaseStageEnum;
import io.zeitwert.dddrive.app.model.RequestContext;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.doc.model.base.FMDocBase;
import io.zeitwert.fm.test.model.DocTest;
import io.zeitwert.fm.test.model.DocTestRepository;
import io.zeitwert.fm.test.model.ObjTest;
import io.zeitwert.fm.test.model.ObjTestRepository;
import io.zeitwert.fm.test.model.enums.CodeTestType;
import io.zeitwert.test.TestApplication;
import org.jooq.JSON;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test")
public class DocTestTest {

	private static final String TEST_JSON = "{ \"one\": \"one\", \"two\": 2 }";
	private static final String TYPE_A = "type_a";
	private static final String TYPE_B = "type_b";
	private static final String TYPE_C = "type_c";

	@Autowired
	private RequestContext requestCtx;

	@Autowired
	private DocTestRepository docTestRepo;

	@Autowired
	private ObjTestRepository objTestRepo;

	@Autowired
	private ObjAccountRepository accountRepo;

	@Autowired
	private ObjAccountRepository accountRepository;

	// CodeTestType is now a Kotlin enum with companion object Enumeration

	@Test
	public void testAggregate() throws Exception {

		assertNotNull(this.docTestRepo, "docTestRepository not null");
		assertEquals("doc_test", this.docTestRepo.getAggregateType().getId());

		Object tenantId = requestCtx.getTenantId();
		Object userId = requestCtx.getUserId();
		ObjAccount account = this.getTestAccount(this.requestCtx);
		OffsetDateTime now = requestCtx.getCurrentTime();

		DocTest testA1 = this.docTestRepo.create(tenantId, userId, now);
		this.initDocTest(testA1, "One", TYPE_A);
		// Cast to FMDocCoreBase to access accountId (Kotlin property)
		((FMDocBase) testA1).setAccountId(account.getId());
		assertNotNull(testA1, "test not null");
		assertNotNull(testA1.getId(), "id not null");
		assertNotNull(testA1.getTenantId(), "tenant not null");

		Object testA_id = testA1.getId();
		Integer testA1_idHash = System.identityHashCode(testA1);

		assertNotNull(testA1.getMeta().getCreatedByUser(), "createdByUser not null");
		assertNotNull(testA1.getMeta().getCreatedAt(), "createdAt not null");
		assertNotNull(testA1.getMeta().getCaseStage(), "caseStage not null");
		assertEquals("test.new", testA1.getMeta().getCaseStage().getId(), "caseStage.id");
		assertEquals(1, testA1.getMeta().getTransitionList().size());
		assertEquals(account.getId(), ((FMDocBase) testA1).getAccountId(), "account id");
		assertEquals(account.getId(), testA1.account.getId(), "account id");

		this.docTestRepo.store(testA1, userId, now);
		testA1 = null;

		DocTest testA2 = this.docTestRepo.get(testA_id);
		Integer testA2_idHash = System.identityHashCode(testA2);
		assertNotEquals(testA1_idHash, testA2_idHash);

		assertNotNull(testA2.getMeta().getModifiedByUser(), "modifiedByUser not null");
		assertNotNull(testA2.getMeta().getModifiedAt(), "modifiedAt not null");
		assertEquals(2, testA2.getMeta().getTransitionList().size());
		assertEquals(account.getId(), ((FMDocBase) testA2).getAccountId(), "account id");
		assertEquals(account.getId(), testA2.account.getId(), "account id");

	}

	@Test
	public void testAggregateProperties() throws Exception {

		Object tenantId = requestCtx.getTenantId();
		Object userId = requestCtx.getUserId();
		OffsetDateTime now = requestCtx.getCurrentTime();

		CodeTestType typeA = CodeTestType.getTestType(TYPE_A);
		CodeTestType typeB = CodeTestType.getTestType(TYPE_B);
		CodeTestType typeC = CodeTestType.getTestType(TYPE_C);

		DocTest testA1 = this.docTestRepo.create(tenantId, userId, now);
		Object testA_id = testA1.getId();
		this.initDocTest(testA1, "One", TYPE_A);

		assertNotNull(testA1.getMeta().getCreatedByUser(), "createdByUser not null");
		assertNotNull(testA1.getMeta().getCreatedAt(), "createdAt not null");
		assertEquals("[Short Test One, Long Test One]", testA1.getCaption());
		assertEquals("Short Test One", testA1.getShortText());
		assertEquals("Long Test One", testA1.getLongText());
		assertEquals(42, testA1.getInt());
		assertEquals(BigDecimal.valueOf(42), testA1.getNr());
		assertEquals(false, testA1.getIsDone());
		assertEquals(LocalDate.of(1966, 9, 8), testA1.getDate());
		assertEquals(JSON.valueOf(TEST_JSON), JSON.valueOf(testA1.json));
		assertEquals(typeA, testA1.getTestType());

		ObjTest refObj = this.objTestRepo.create(tenantId, userId, now);
		this.initObjTest(refObj, "Two", TYPE_B);
		Object refObj_id = refObj.getId();
		this.objTestRepo.store(refObj, userId, now);

		testA1.setRefObjId((Integer) refObj_id);
		assertEquals("[Short Test One, Long Test One] (RefObj:[Short Test Two, Long Test Two])", testA1.getCaption());

		DocTest refDoc = this.docTestRepo.create(tenantId, userId, now);
		this.initDocTest(refDoc, "Two", TYPE_B);
		Object refDoc_id = refDoc.getId();
		this.docTestRepo.store(refDoc, userId, now);

		testA1.setRefDocId((Integer) refDoc_id);
		assertEquals(
				"[Short Test One, Long Test One] (RefObj:[Short Test Two, Long Test Two]) (RefDoc:[Short Test Two, Long Test Two])",
				testA1.getCaption());

		// Test EnumSet operations with TestType
		assertFalse(testA1.hasTestType(typeA));
		testA1.addTestType(typeA);
		assertTrue(testA1.hasTestType(typeA));
		testA1.addTestType(typeB);
		assertTrue(testA1.hasTestType(typeB));
		assertEquals(2, testA1.testTypeSet.size());
		testA1.removeTestType(typeB);
		assertTrue(testA1.hasTestType(typeA));
		assertFalse(testA1.hasTestType(typeB));
		assertEquals(1, testA1.testTypeSet.size());
		testA1.addTestType(typeC);
		assertTrue(testA1.hasTestType(typeC));
		assertEquals(2, testA1.testTypeSet.size());

		this.docTestRepo.store(testA1, userId, now);
		testA1 = null;

		DocTest testA2 = this.docTestRepo.load(testA_id);

		assertEquals(
				"[Short Test One, Long Test One] (RefObj:[Short Test Two, Long Test Two]) (RefDoc:[Short Test Two, Long Test Two])",
				testA2.getCaption());
		assertEquals("Short Test One", testA2.getShortText());
		assertEquals("Long Test One", testA2.getLongText());
		assertEquals(42, testA2.getInt());
		assertEquals(BigDecimal.valueOf(42).setScale(3), testA2.nr.setScale(3));
		assertEquals(false, testA2.getIsDone());
		assertEquals(LocalDate.of(1966, 9, 8), testA2.getDate());
		assertEquals(JSON.valueOf(TEST_JSON), JSON.valueOf(testA2.json));
		assertEquals(typeA, testA2.getTestType());
		assertEquals(refObj_id, testA2.refObj.getId());
		assertEquals(refDoc_id, testA2.refDoc.getId());

		assertEquals(2, testA2.testTypeSet.size());
		assertTrue(testA2.hasTestType(typeA));
		assertTrue(testA2.hasTestType(typeC));

		testA2.setShortText("another shortText");
		testA2.setLongText("another longText");
		testA2.setInt(41);
		testA2.setNr(BigDecimal.valueOf(41));
		testA2.setIsDone(true);
		testA2.setDate(LocalDate.of(1966, 1, 5));
		testA2.setJson(null);
		testA2.setTestType(typeB);
		testA2.setRefObjId(null);
		testA2.setRefDocId(null);

		testA2.removeTestType(typeA);
		testA2.addTestType(typeB);

		assertEquals("[another shortText, another longText]", testA2.getCaption());
		assertEquals("another shortText", testA2.getShortText());
		assertEquals("another longText", testA2.getLongText());
		assertEquals(41, testA2.getInt());
		assertEquals(BigDecimal.valueOf(41).setScale(3), testA2.nr.setScale(3));
		assertEquals(true, testA2.isDone);
		assertEquals(LocalDate.of(1966, 1, 5), testA2.date);
		assertNull(testA2.json);
		assertEquals(typeB, testA2.testType);
		assertNull(testA2.refObj);

		assertEquals(2, testA2.testTypeSet.size());
		assertFalse(testA2.hasTestType(typeA));
		assertTrue(testA2.hasTestType(typeB));
		assertTrue(testA2.hasTestType(typeC));

	}

	private ObjAccount getTestAccount(RequestContext requestCtx) {
		return this.accountRepository.get(this.accountRepo.getAll(requestCtx.getTenantId()).get(0));
	}

	private void initDocTest(DocTest test, String name, String testTypeId) {
		test.setCaseStage(CodeCaseStageEnum.getCaseStage("test.new"), null, OffsetDateTime.now());
		assertEquals("[, ]", test.getCaption());
		test.setShortText("Short Test " + name);
		assertEquals("[Short Test " + name + ", ]", test.getCaption());
		test.setLongText("Long Test " + name);
		assertEquals("[Short Test " + name + ", Long Test " + name + "]", test.getCaption());
		test.setInt(42);
		test.setNr(BigDecimal.valueOf(42));
		test.setIsDone(false);
		test.setDate(LocalDate.of(1966, 9, 8));
		test.setJson(JSON.valueOf(TEST_JSON).toString());
		CodeTestType testType = CodeTestType.getTestType(testTypeId);
		test.setTestType(testType);
	}

	private void initObjTest(ObjTest test, String name, String testTypeId) {
		assertEquals("[, ]", test.getCaption());
		test.setShortText("Short Test " + name);
		assertEquals("[Short Test " + name + ", ]", test.getCaption());
		test.setLongText("Long Test " + name);
		assertEquals("[Short Test " + name + ", Long Test " + name + "]", test.getCaption());
		test.setInt(42);
		test.setNr(BigDecimal.valueOf(42));
		test.setIsDone(false);
		test.setDate(LocalDate.of(1966, 9, 8));
		test.setJson(JSON.valueOf(TEST_JSON).toString());
		CodeTestType testType = CodeTestType.getTestType(testTypeId);
		test.setTestType(testType);
	}

}
