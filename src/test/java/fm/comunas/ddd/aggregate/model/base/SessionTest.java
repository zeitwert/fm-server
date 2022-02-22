
package fm.comunas.ddd.aggregate.model.base;

import static org.junit.jupiter.api.Assertions.*;

import org.jooq.JSON;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import fm.comunas.ddd.common.model.enums.CodeCountry;
import fm.comunas.ddd.common.model.enums.CodeCountryEnum;
import fm.comunas.ddd.oe.model.ObjUser;
import fm.comunas.ddd.oe.model.ObjUserRepository;
import fm.comunas.ddd.session.model.SessionInfo;
import fm.comunas.ddd.session.service.api.SessionService;
import fm.comunas.fm.test.model.ObjTest;
import fm.comunas.fm.test.model.ObjTestRepository;
import fm.comunas.server.Application;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class SessionTest {

	private static final String USER_EMAIL = "martin@comunas.fm";
	private static final String TEST_JSON = "{ \"one\": \"one\", \"two\": 2 }";

	@Autowired
	private SessionInfo sessionInfo;

	@Autowired
	private SessionService sessionService;

	@Autowired
	private ObjTestRepository testRepository;

	@Autowired
	private ObjUserRepository userRepository;

	@Autowired
	private CodeCountryEnum countryEnum;

	@Test
	public void testSessionHandling() throws Exception {

		ObjTest test1a = testRepository.create(sessionInfo);
		this.initObjTest(test1a, "One", USER_EMAIL, "ch");
		Integer test1Id = test1a.getId();
		Integer test1aIdHash = System.identityHashCode(test1a);
		testRepository.store(test1a);
		test1a = null;

		ObjTest test1b = testRepository.get(sessionInfo, test1Id).get();
		Integer test1bIdHash = System.identityHashCode(test1b);
		assertNotEquals(test1aIdHash, test1bIdHash);
		assertEquals(sessionInfo, test1b.getMeta().getSessionInfo());

		ObjUser user = userRepository.getByEmail(USER_EMAIL).get();
		SessionInfo session2 = sessionService.openSession(user);

		ObjTest test1c = testRepository.get(session2, test1Id).get();
		Integer test1cIdHash = System.identityHashCode(test1c);
		assertNotEquals(test1bIdHash, test1cIdHash);
		assertEquals(session2, test1c.getMeta().getSessionInfo());

		test1b.setShortText("another description");
		assertEquals("another description", test1b.getShortText());
		assertEquals("Short Test One", test1c.getShortText());

		assertEquals(false, ((AggregateBase) test1b).isStale());
		assertEquals(false, ((AggregateBase) test1c).isStale());

		testRepository.store(test1b);
		assertEquals(true, ((AggregateBase) test1b).isStale());
		test1b = null;

		test1c = testRepository.get(session2, test1Id).get();
		assertEquals(System.identityHashCode(test1c), test1cIdHash, "still the same aggregate");
		assertEquals(true, ((AggregateBase) test1c).isStale());

		test1b = testRepository.get(sessionInfo, test1Id).get();
		assertNotEquals(System.identityHashCode(test1b), test1bIdHash, "different aggregate");
		assertEquals(false, ((AggregateBase) test1b).isStale());

		assertEquals("another description", test1b.getShortText());
		assertEquals("Short Test One", test1c.getShortText());
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
		ObjUser user = userRepository.getByEmail(userEmail).get();
		test.setOwner(user);
		CodeCountry country = countryEnum.getItem(countryId);
		test.setCountry(country);
	}

}
