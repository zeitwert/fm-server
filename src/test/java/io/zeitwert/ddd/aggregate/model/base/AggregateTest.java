
package io.zeitwert.ddd.aggregate.model.base;

import static org.junit.jupiter.api.Assertions.*;

import org.jooq.JSON;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.zeitwert.fm.test.model.ObjTest;
import io.zeitwert.fm.test.model.ObjTestRepository;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.oe.model.enums.CodeCountry;
import io.zeitwert.ddd.oe.model.enums.CodeCountryEnum;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.server.Application;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class AggregateTest {

	private static final String USER_EMAIL = "k@zeitwert.io";
	private static final String TEST_JSON = "{ \"one\": \"one\", \"two\": 2 }";

	@Autowired
	private SessionInfo sessionInfo;

	@Autowired
	private ObjTestRepository testRepository;

	@Autowired
	private ObjUserRepository userRepository;

	@Autowired
	private CodeCountryEnum countryEnum;

	// @Autowired
	// private CodeAreaEnum areaEnum;

	@Test
	public void testAggregate() throws Exception {

		assertTrue(testRepository != null, "objTestRepository not null");
		assertEquals("obj_test", testRepository.getAggregateType().getId());

		ObjTest test1a = testRepository.create(sessionInfo.getTenant().getId(), sessionInfo);
		this.initObjTest(test1a, "One", USER_EMAIL, "ch");
		assertNotNull(test1a, "test not null");
		assertNotNull(test1a.getId(), "id not null");
		assertNotNull(test1a.getTenant(), "tenant not null");

		Integer test1Id = test1a.getId();
		Integer test1aIdHash = System.identityHashCode(test1a);

		assertNotNull(test1a.getMeta().getCreatedByUser(), "createdByUser not null");
		assertNotNull(test1a.getMeta().getCreatedAt(), "createdAt not null");
		assertEquals(1, test1a.getMeta().getTransitionList().size());

		testRepository.store(test1a);
		test1a = null;

		ObjTest test1b = testRepository.get(sessionInfo, test1Id);
		Integer test1bIdHash = System.identityHashCode(test1b);
		assertNotEquals(test1aIdHash, test1bIdHash);

		assertNotNull(test1b.getMeta().getModifiedByUser(), "modifiedByUser not null");
		assertNotNull(test1b.getMeta().getModifiedAt(), "modifiedAt not null");
		assertEquals(2, test1b.getMeta().getTransitionList().size());

	}

	@Test
	public void testAggregateProperties() throws Exception {

		ObjUser user = userRepository.getByEmail(sessionInfo, USER_EMAIL).get();
		CodeCountry ch = countryEnum.getItem("ch");
		CodeCountry de = countryEnum.getItem("de");

		ObjTest test1a = testRepository.create(sessionInfo.getTenant().getId(), sessionInfo);
		Integer test1Id = test1a.getId();
		this.initObjTest(test1a, "One", USER_EMAIL, "ch");

		assertNotNull(test1a.getMeta().getCreatedByUser(), "createdByUser not null");
		assertNotNull(test1a.getMeta().getCreatedAt(), "createdAt not null");
		assertEquals("[Short Test One, Long Test One]", test1a.getCaption());
		assertEquals("Short Test One", test1a.getShortText());
		assertEquals("Long Test One", test1a.getLongText());
		assertEquals(42, test1a.getInt());
		assertEquals(BigDecimal.valueOf(42), test1a.getNr());
		assertEquals(false, test1a.getIsDone());
		assertEquals(LocalDate.of(1966, 9, 8), test1a.getDate());
		assertEquals(JSON.valueOf(TEST_JSON), JSON.valueOf(test1a.getJson()));
		assertEquals(user.getId(), test1a.getOwner().getId());
		assertEquals(ch, test1a.getCountry());

		ObjTest test2a = testRepository.create(sessionInfo.getTenant().getId(), sessionInfo);
		this.initObjTest(test2a, "Two", USER_EMAIL, "de");
		Integer test2Id = test2a.getId();
		testRepository.store(test2a);

		test1a.setRefTestId(test2Id);
		assertEquals("[Short Test One, Long Test One] ([Short Test Two, Long Test Two])", test1a.getCaption());

		testRepository.store(test1a);
		test1a = null;

		ObjTest test1b = testRepository.get(sessionInfo, test1Id);

		assertEquals("[Short Test One, Long Test One] ([Short Test Two, Long Test Two])", test1b.getCaption());
		assertEquals("Short Test One", test1b.getShortText());
		assertEquals("Long Test One", test1b.getLongText());
		assertEquals(42, test1b.getInt());
		assertEquals(BigDecimal.valueOf(42).setScale(3), test1b.getNr().setScale(3));
		assertEquals(false, test1b.getIsDone());
		assertEquals(LocalDate.of(1966, 9, 8), test1b.getDate());
		assertEquals(JSON.valueOf(TEST_JSON), JSON.valueOf(test1b.getJson()));
		assertEquals(user.getId(), test1b.getOwner().getId());
		assertEquals(ch, test1b.getCountry());
		assertEquals(test2Id, test1b.getRefTest().getId());

		test1b.setShortText("another shortText");
		test1b.setLongText("another longText");
		test1b.setInt(41);
		test1b.setNr(BigDecimal.valueOf(41));
		test1b.setIsDone(true);
		test1b.setDate(LocalDate.of(1966, 1, 5));
		test1b.setJson(null);
		test1b.setOwner(null);
		test1b.setCountry(de);
		test1b.setRefTestId(null);

		assertEquals("[another shortText, another longText]", test1b.getCaption());
		assertEquals("another shortText", test1b.getShortText());
		assertEquals("another longText", test1b.getLongText());
		assertEquals(41, test1b.getInt());
		assertEquals(BigDecimal.valueOf(41).setScale(3), test1b.getNr().setScale(3));
		assertEquals(true, test1b.getIsDone());
		assertEquals(LocalDate.of(1966, 1, 5), test1b.getDate());
		assertNull(test1b.getJson());
		assertNull(test1b.getOwner());
		assertEquals(de, test1b.getCountry());
		assertNull(test1b.getRefTest());

	}

	// @Test
	// public void testAreas() throws Exception {

	// ObjTest test1a = testRepository.create(sessionInfo);
	// Integer test1Id = test1a.getId();

	// test1a.addArea(areaEnum.getItem("safety_net"));
	// assertEquals(1, test1a.getAreaSet().size());

	// test1a.addArea(areaEnum.getItem("real_estate"));
	// assertEquals(2, test1a.getAreaSet().size());

	// test1a.addArea(areaEnum.getItem("investment"));
	// assertEquals(3, test1a.getAreaSet().size());

	// test1a.removeArea(areaEnum.getItem("real_estate"));
	// assertEquals(2, test1a.getAreaSet().size());
	// assertTrue(test1a.getAreaSet().contains(areaEnum.getItem("safety_net")));
	// assertTrue(test1a.getAreaSet().contains(areaEnum.getItem("investment")));

	// testRepository.store(test1a);
	// test1a = null;

	// ObjTest test1b = testRepository.get(sessionInfo, test1Id);

	// assertEquals(2, test1b.getAreaSet().size());
	// assertTrue(test1b.getAreaSet().contains(areaEnum.getItem("safety_net")));
	// assertTrue(test1b.getAreaSet().contains(areaEnum.getItem("investment")));

	// }

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
		ObjUser user = userRepository.getByEmail(sessionInfo, userEmail).get();
		test.setOwner(user);
		CodeCountry country = countryEnum.getItem(countryId);
		test.setCountry(country);
	}

}
