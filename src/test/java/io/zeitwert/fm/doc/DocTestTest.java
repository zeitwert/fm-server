
package io.zeitwert.fm.doc;

import static org.junit.jupiter.api.Assertions.*;

import org.jooq.JSON;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.account.service.api.ObjAccountCache;
import io.zeitwert.fm.test.model.DocTest;
import io.zeitwert.fm.test.model.DocTestRepository;
import io.zeitwert.fm.test.model.ObjTest;
import io.zeitwert.fm.test.model.ObjTestRepository;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.oe.model.enums.CodeCountry;
import io.zeitwert.ddd.oe.model.enums.CodeCountryEnum;
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.server.Application;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class DocTestTest {

	private static final String USER_EMAIL = "tt@zeitwert.io";
	private static final String TEST_JSON = "{ \"one\": \"one\", \"two\": 2 }";
	private static final String CH = "ch";
	private static final String DE = "de";
	private static final String ES = "es";

	@Autowired
	private RequestContext requestCtx;

	@Autowired
	private DocTestRepository docTestRepository;

	@Autowired
	private ObjTestRepository objTestRepository;

	@Autowired
	private ObjAccountRepository accountRepo;

	@Autowired
	private ObjAccountCache accountCache;

	@Autowired
	private ObjUserRepository userRepository;

	@Autowired
	private CodeCountryEnum countryEnum;

	@Test
	public void testAggregate() throws Exception {

		assertTrue(this.docTestRepository != null, "docTestRepository not null");
		assertEquals("doc_test", this.docTestRepository.getAggregateType().getId());

		ObjAccount account = this.getTestAccount(this.requestCtx);
		DocTest testA1 = this.docTestRepository.create(this.requestCtx.getTenantId());
		testA1.setAccountId(account.getId());
		this.initDocTest(testA1, "One", USER_EMAIL, CH);
		assertNotNull(testA1, "test not null");
		assertNotNull(testA1.getId(), "id not null");
		assertNotNull(testA1.getTenant(), "tenant not null");

		Integer testA_id = testA1.getId();
		Integer testA1_idHash = System.identityHashCode(testA1);

		assertNotNull(testA1.getMeta().getCreatedByUser(), "createdByUser not null");
		assertNotNull(testA1.getMeta().getCreatedAt(), "createdAt not null");
		assertNotNull(testA1.getCaseStage(), "caseStage not null");
		assertEquals("test.new", testA1.getCaseStage().getId(), "caseStage.id");
		assertEquals(1, testA1.getMeta().getTransitionList().size());
		assertEquals(account.getId(), testA1.getAccountId(), "account id");
		assertEquals(account.getId(), testA1.getAccount().getId(), account.getId(), "account id");

		this.docTestRepository.store(testA1);
		testA1 = null;

		DocTest testA2 = this.docTestRepository.get(testA_id);
		Integer testA2_idHash = System.identityHashCode(testA2);
		assertNotEquals(testA1_idHash, testA2_idHash);

		assertNotNull(testA2.getMeta().getModifiedByUser(), "modifiedByUser not null");
		assertNotNull(testA2.getMeta().getModifiedAt(), "modifiedAt not null");
		assertEquals(2, testA2.getMeta().getTransitionList().size());
		assertEquals(account.getId(), testA2.getAccountId(), "account id");
		assertEquals(account.getId(), testA2.getAccount().getId(), account.getId(), "account id");

	}

	@Test
	public void testAggregateProperties() throws Exception {

		ObjUser user = this.userRepository.getByEmail(USER_EMAIL).get();
		CodeCountry ch = this.countryEnum.getItem(CH);
		CodeCountry de = this.countryEnum.getItem(DE);
		CodeCountry es = this.countryEnum.getItem(ES);

		DocTest testA1 = this.docTestRepository.create(this.requestCtx.getTenantId());
		Integer testA_id = testA1.getId();
		this.initDocTest(testA1, "One", USER_EMAIL, CH);

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
		assertEquals(user.getId(), testA1.getOwner().getId());
		assertEquals(ch, testA1.getCountry());

		ObjTest refObj = this.objTestRepository.create(this.requestCtx.getTenantId());
		this.initObjTest(refObj, "Two", USER_EMAIL, DE);
		Integer refObj_id = refObj.getId();
		this.objTestRepository.store(refObj);

		testA1.setRefObjId(refObj_id);
		assertEquals("[Short Test One, Long Test One] (RefObj:[Short Test Two, Long Test Two])", testA1.getCaption());

		DocTest refDoc = this.docTestRepository.create(this.requestCtx.getTenantId());
		this.initDocTest(refDoc, "Two", USER_EMAIL, DE);
		Integer refDoc_id = refDoc.getId();
		this.docTestRepository.store(refDoc);

		testA1.setRefDocId(refDoc_id);
		assertEquals(
				"[Short Test One, Long Test One] (RefObj:[Short Test Two, Long Test Two]) (RefDoc:[Short Test Two, Long Test Two])",
				testA1.getCaption());

		assertFalse(testA1.hasCountry(ch));
		testA1.addCountry(ch);
		assertTrue(testA1.hasCountry(ch));
		testA1.addCountry(de);
		assertTrue(testA1.hasCountry(de));
		assertEquals(2, testA1.getCountrySet().size());
		testA1.removeCountry(de);
		assertTrue(testA1.hasCountry(ch));
		assertFalse(testA1.hasCountry(de));
		assertEquals(1, testA1.getCountrySet().size());
		testA1.addCountry(es);
		assertTrue(testA1.hasCountry(es));
		assertEquals(2, testA1.getCountrySet().size());

		this.docTestRepository.store(testA1);
		testA1 = null;

		DocTest testA2 = this.docTestRepository.get(testA_id);

		assertEquals(
				"[Short Test One, Long Test One] (RefObj:[Short Test Two, Long Test Two]) (RefDoc:[Short Test Two, Long Test Two])",
				testA2.getCaption());
		assertEquals("Short Test One", testA2.getShortText());
		assertEquals("Long Test One", testA2.getLongText());
		assertEquals(42, testA2.getInt());
		assertEquals(BigDecimal.valueOf(42).setScale(3), testA2.getNr().setScale(3));
		assertEquals(false, testA2.getIsDone());
		assertEquals(LocalDate.of(1966, 9, 8), testA2.getDate());
		assertEquals(JSON.valueOf(TEST_JSON), JSON.valueOf(testA2.getJson()));
		assertEquals(user.getId(), testA2.getOwner().getId());
		assertEquals(ch, testA2.getCountry());
		assertEquals(refObj_id, testA2.getRefObj().getId());
		assertEquals(refDoc_id, testA2.getRefDoc().getId());

		assertEquals(2, testA2.getCountrySet().size());
		assertTrue(testA2.hasCountry(ch));
		assertTrue(testA2.hasCountry(es));

		testA2.setShortText("another shortText");
		testA2.setLongText("another longText");
		testA2.setInt(41);
		testA2.setNr(BigDecimal.valueOf(41));
		testA2.setIsDone(true);
		testA2.setDate(LocalDate.of(1966, 1, 5));
		testA2.setJson(null);
		testA2.setOwner(null);
		testA2.setCountry(de);
		testA2.setRefObjId(null);
		testA2.setRefDocId(null);

		testA2.removeCountry(ch);
		testA2.addCountry(de);

		assertEquals("[another shortText, another longText]", testA2.getCaption());
		assertEquals("another shortText", testA2.getShortText());
		assertEquals("another longText", testA2.getLongText());
		assertEquals(41, testA2.getInt());
		assertEquals(BigDecimal.valueOf(41).setScale(3), testA2.getNr().setScale(3));
		assertEquals(true, testA2.getIsDone());
		assertEquals(LocalDate.of(1966, 1, 5), testA2.getDate());
		assertNull(testA2.getJson());
		assertNull(testA2.getOwner());
		assertEquals(de, testA2.getCountry());
		assertNull(testA2.getRefObj());

		assertEquals(2, testA2.getCountrySet().size());
		assertFalse(testA2.hasCountry(ch));
		assertTrue(testA2.hasCountry(de));
		assertTrue(testA2.hasCountry(es));

	}

	private ObjAccount getTestAccount(RequestContext requestCtx) {
		return this.accountCache.get(this.accountRepo.find(null).get(0).getId());
	}

	private void initDocTest(DocTest test, String name, String userEmail, String countryId) {
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
		ObjUser user = this.userRepository.getByEmail(userEmail).get();
		test.setOwner(user);
		CodeCountry country = this.countryEnum.getItem(countryId);
		test.setCountry(country);
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
		ObjUser user = this.userRepository.getByEmail(userEmail).get();
		test.setOwner(user);
		CodeCountry country = this.countryEnum.getItem(countryId);
		test.setCountry(country);
	}

}
