package io.zeitwert.a_smoke;

import io.domain.test.model.ObjTest;
import io.domain.test.model.ObjTestRepository;
import io.domain.test.model.enums.CodeTestType;
import io.zeitwert.test.TestApplication;
import org.jooq.JSON;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test")
public class AggregateTest {

	private static final String TEST_JSON = "{ \"one\": \"one\", \"two\": 2 }";

	@Autowired
	private ObjTestRepository testRepo;

	// CodeTestType is now a Kotlin enum with companion object Enumeration

	@Test
	public void testSessionHandling() throws Exception {

		ObjTest testA1 = this.testRepo.create();
		this.initObjTest(testA1, "One", "type_a");
		Object testA1_id = testA1.getId();
		Integer testA1_idHash = System.identityHashCode(testA1);
		this.testRepo.store(testA1);
		testA1 = null;

		ObjTest testA2 = this.testRepo.get(testA1_id);
		Integer testA2_idHash = System.identityHashCode(testA2);
		assertNotEquals(testA1_idHash, testA2_idHash);
		// Note: getRequestContext() is not available in the new API
		// The repository is accessible via aggregate.getMeta().getRepository()

	}

	private void initObjTest(ObjTest test, String name, String testTypeId) {
		assertEquals("[, ]", test.getCaption());
		test.setShortText("Short Test " + name);
		assertEquals("[Short Test " + name + ", ]", test.getCaption());
		test.setLongText("Long Test " + name);
		assertEquals("[Short Test " + name + ", Long Test " + name + "]", test.getCaption());
		test.setInt(42);
		test.setNr(BigDecimal.valueOf(42));
		test.setDone(false);
		test.setDate(LocalDate.of(1966, 9, 8));
		test.setJson(JSON.valueOf(TEST_JSON).toString());
		CodeTestType testType = CodeTestType.Enumeration.getTestType(testTypeId);
		test.setTestType(testType);
	}

}
