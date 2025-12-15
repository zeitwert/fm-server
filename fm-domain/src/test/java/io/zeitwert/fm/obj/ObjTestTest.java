package io.zeitwert.fm.obj;

import io.zeitwert.dddrive.app.model.RequestContext;
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
public class ObjTestTest {

	private static final String TEST_JSON = "{ \"one\": \"one\", \"two\": 2 }";
	private static final String TYPE_A = "type_a";
	private static final String TYPE_B = "type_b";
	private static final String TYPE_C = "type_c";

	@Autowired
	private RequestContext requestCtx;

	@Autowired
	private ObjTestRepository testRepository;

	@Test
	public void testBase() throws Exception {

		assertNotNull(this.testRepository, "objTestRepository not null");
		assertEquals("obj_test", this.testRepository.getAggregateType().getId());

		Object tenantId = requestCtx.getTenantId();
		Object userId = requestCtx.getUserId();
		OffsetDateTime now = requestCtx.getCurrentTime();

		ObjTest testA1 = this.testRepository.create(tenantId, userId, now);
		assertNotNull(testA1, "test not null");
		assertNotNull(testA1.getId(), "id not null");
		assertNotNull(testA1.getTenantId(), "tenant not null");
		this.initObjTest(testA1, "One", TYPE_A);

		Object testA_id = testA1.getId();
		Integer testA1_idHash = System.identityHashCode(testA1);

		assertFalse(testA1.getMeta().isFrozen(), "not frozen");
		assertNotNull(testA1.getMeta().getCreatedByUser(), "createdByUser not null");
		assertNotNull(testA1.getMeta().getCreatedAt(), "createdAt not null");
		assertEquals(1, testA1.getMeta().getTransitionList().size());

		this.testRepository.store(testA1, userId, now);
		testA1 = null;

		ObjTest testA2 = this.testRepository.get(testA_id);
		Integer testA2_idHash = System.identityHashCode(testA2);
		assertNotEquals(testA1_idHash, testA2_idHash);

		assertTrue(testA2.getMeta().isFrozen(), "frozen");
		assertNotNull(testA2.getMeta().getModifiedByUser(), "modifiedByUser not null");
		assertNotNull(testA2.getMeta().getModifiedAt(), "modifiedAt not null");

	}

	@Test
	public void testProperties() throws Exception {

		Object tenantId = requestCtx.getTenantId();
		Object userId = requestCtx.getUserId();
		OffsetDateTime now = requestCtx.getCurrentTime();

		CodeTestType typeA = CodeTestType.getTestType(TYPE_A);
		CodeTestType typeB = CodeTestType.getTestType(TYPE_B);

		ObjTest testA1 = this.testRepository.create(tenantId, userId, now);
		Object testA_id = testA1.getId();
		this.initObjTest(testA1, "One", TYPE_A);

		assertNotNull(testA1.getMeta().getCreatedByUser(), "createdByUser not null");
		assertNotNull(testA1.getMeta().getCreatedAt(), "createdAt not null");
		assertEquals("[Short Test One, Long Test One]", testA1.getCaption());
		assertEquals("Short Test One", testA1.getShortText());
		assertEquals("Long Test One", testA1.getLongText());
		assertEquals(42, testA1.getInt());
		assertEquals(BigDecimal.valueOf(42), testA1.getNr());
		assertEquals(false, testA1.getIsDone());
		assertEquals(LocalDate.of(1966, 9, 8), testA1.getDate());
		assertEquals(JSON.valueOf(TEST_JSON), JSON.valueOf(testA1.getJson()));
		assertEquals(typeA, testA1.getTestType());

		ObjTest testB1 = this.testRepository.create(tenantId, userId, now);
		this.initObjTest(testB1, "Two", TYPE_B);
		Object testB_id = testB1.getId();
		this.testRepository.store(testB1, userId, now);

		testA1.setRefTestId((Integer) testB_id);
		assertEquals("[Short Test One, Long Test One] ([Short Test Two, Long Test Two])", testA1.getCaption());

		this.testRepository.store(testA1, userId, now);
		testA1 = null;

		ObjTest testA2 = this.testRepository.load(testA_id);

		assertEquals("[Short Test One, Long Test One] ([Short Test Two, Long Test Two])", testA2.getCaption());
		assertEquals("Short Test One", testA2.getShortText());
		assertEquals("Long Test One", testA2.getLongText());
		assertEquals(42, testA2.getInt());
		assertEquals(BigDecimal.valueOf(42).setScale(3), testA2.getNr().setScale(3));
		assertEquals(false, testA2.getIsDone());
		assertEquals(LocalDate.of(1966, 9, 8), testA2.getDate());
		assertEquals(JSON.valueOf(TEST_JSON), JSON.valueOf(testA2.getJson()));
		assertEquals(typeA, testA2.getTestType());
		assertEquals(testB_id, testA2.getRefTest().getId());

		testA2.setShortText("another shortText");
		testA2.setLongText("another longText");
		testA2.setInt(41);
		testA2.setNr(BigDecimal.valueOf(41));
		testA2.setIsDone(true);
		testA2.setDate(LocalDate.of(1966, 1, 5));
		testA2.setJson(null);
		testA2.setTestType(typeB);
		testA2.setRefTestId(null);

		assertEquals("[another shortText, another longText]", testA2.getCaption());
		assertEquals("another shortText", testA2.getShortText());
		assertEquals("another longText", testA2.getLongText());
		assertEquals(41, testA2.getInt());
		assertEquals(BigDecimal.valueOf(41).setScale(3), testA2.getNr().setScale(3));
		assertEquals(true, testA2.getIsDone());
		assertEquals(LocalDate.of(1966, 1, 5), testA2.getDate());
		assertNull(testA2.getJson());
		assertEquals(typeB, testA2.getTestType());
		assertNull(testA2.getRefTest());

	}

	@Test
	public void testParts() throws Exception {

		Object tenantId = requestCtx.getTenantId();
		Object userId = requestCtx.getUserId();
		OffsetDateTime now = requestCtx.getCurrentTime();

		CodeTestType typeA = CodeTestType.getTestType(TYPE_A);
		CodeTestType typeB = CodeTestType.getTestType(TYPE_B);
		CodeTestType typeC = CodeTestType.getTestType(TYPE_C);

		ObjTest testA1 = this.testRepository.create(tenantId, userId, now);
		Object testA_id = testA1.getId();
		this.initObjTest(testA1, "One", TYPE_A);

		// Test EnumSet operations with TestType
		assertFalse(testA1.hasTestType(typeA));
		testA1.addTestType(typeA);
		assertTrue(testA1.hasTestType(typeA));
		testA1.addTestType(typeB);
		assertTrue(testA1.hasTestType(typeB));
		assertEquals(2, testA1.getTestTypeSet().size());
		testA1.removeTestType(typeB);
		assertTrue(testA1.hasTestType(typeA));
		assertFalse(testA1.hasTestType(typeB));
		assertEquals(1, testA1.getTestTypeSet().size());
		testA1.addTestType(typeC);
		assertTrue(testA1.hasTestType(typeC));
		assertEquals(2, testA1.getTestTypeSet().size());

		assertEquals(1, testA1.getMeta().getTransitionList().size());

		this.testRepository.store(testA1, userId, now);
		testA1 = null;

		ObjTest testA2 = this.testRepository.load(testA_id);

		assertEquals(2, testA2.getMeta().getTransitionList().size());

		assertEquals(2, testA2.getTestTypeSet().size());
		assertTrue(testA2.hasTestType(typeA));
		assertTrue(testA2.hasTestType(typeC));

		testA2.removeTestType(typeA);
		testA2.addTestType(typeB);

		assertEquals(2, testA2.getTestTypeSet().size());
		assertFalse(testA2.hasTestType(typeA));
		assertTrue(testA2.hasTestType(typeB));
		assertTrue(testA2.hasTestType(typeC));

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
