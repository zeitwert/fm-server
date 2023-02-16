
package io.zeitwert.ddd.session;

import static org.junit.jupiter.api.Assertions.*;

import org.jooq.JSON;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.dddrive.app.model.RequestContext;
import io.dddrive.oe.model.ObjUser;
import io.zeitwert.fm.oe.model.enums.CodeCountry;
import io.zeitwert.fm.oe.model.enums.CodeCountryEnum;
import io.dddrive.oe.service.api.ObjUserCache;
import io.zeitwert.fm.server.Application;
import io.zeitwert.fm.test.model.ObjTest;
import io.zeitwert.fm.test.model.ObjTestRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class SessionTest {

	private static final String USER_EMAIL = "tt@zeitwert.io";
	private static final String TEST_JSON = "{ \"one\": \"one\", \"two\": 2 }";

	@Autowired
	private RequestContext requestCtx;

	@Autowired
	private ObjTestRepository testRepo;

	@Autowired
	private ObjUserCache userCache;

	@Autowired
	private CodeCountryEnum countryEnum;

	@Test
	public void testSessionHandling() throws Exception {

		ObjTest testA1 = this.testRepo.create(this.requestCtx.getTenantId());
		this.initObjTest(testA1, "One", USER_EMAIL, "ch");
		Integer testA1_id = testA1.getId();
		Integer testA1_idHash = System.identityHashCode(testA1);
		this.testRepo.store(testA1);
		testA1 = null;

		ObjTest testA2 = this.testRepo.get(testA1_id);
		Integer testA2_idHash = System.identityHashCode(testA2);
		assertNotEquals(testA1_idHash, testA2_idHash);
		assertEquals(this.requestCtx, testA2.getMeta().getRequestContext());

		// ObjUser user = userRepository.getByEmail(requestCtx, USER_EMAIL).get();

		// ObjTest test1c = testRepository.get(session2, test1Id);
		// Integer test1cIdHash = System.identityHashCode(test1c);
		// assertNotEquals(test1bIdHash, test1cIdHash);
		// assertEquals(session2, test1c.getMeta().getRequestContext());

		// test1b.setShortText("another description");
		// assertEquals("another description", test1b.getShortText());
		// assertEquals("Short Test One", test1c.getShortText());

		// assertEquals(false, ((AggregateBase) test1b).isStale());
		// assertEquals(false, ((AggregateBase) test1c).isStale());

		// testRepository.store(test1b);
		// assertTrue(((AggregateBase) test1b).isStale());
		// test1b = null;

		// // assertTrue(((AggregateBase) test1c).isStale());
		// assertEquals("Short Test One", test1c.getShortText());
		// test1c = testRepository.get(session2, test1Id);
		// assertEquals("another description", test1c.getShortText());
		// assertNotEquals(System.identityHashCode(test1c), test1cIdHash, "different
		// aggregate");

		// test1b = testRepository.get(test1Id);
		// assertNotEquals(System.identityHashCode(test1b), test1bIdHash, "different
		// aggregate");
		// assertEquals(false, ((AggregateBase) test1b).isStale());

		// assertEquals("another description", test1b.getShortText());
	}

	private void initObjTest(ObjTest test, String name, String userEmail, String countryId) {
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
		ObjUser user = this.userCache.getByEmail(userEmail).get();
		test.setOwner(user);
		CodeCountry country = this.countryEnum.getItem(countryId);
		test.setCountry(country);
	}

}
