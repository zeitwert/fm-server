
package io.zeitwert.ddd.part.model.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jooq.JSON;
import org.jooq.UpdatableRecord;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.dddrive.app.model.RequestContext;
import io.dddrive.ddd.model.PartPersistenceStatus;
import io.dddrive.ddd.model.base.PartSPI;
import io.dddrive.ddd.model.enums.CodePartListType;
import io.dddrive.ddd.model.enums.CodePartListTypeEnum;
import io.dddrive.jooq.ddd.PartFields;
import io.dddrive.jooq.ddd.PartState;
import io.dddrive.oe.model.ObjUser;
import io.dddrive.oe.model.enums.CodeCountry;
import io.dddrive.oe.model.enums.CodeCountryEnum;
import io.dddrive.oe.service.api.ObjUserCache;
import io.zeitwert.fm.server.Application;
import io.zeitwert.fm.test.model.ObjTest;
import io.zeitwert.fm.test.model.ObjTestPartNode;
import io.zeitwert.fm.test.model.ObjTestPartNodeRepository;
import io.zeitwert.fm.test.model.ObjTestRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class PartTest {

	private static final String USER_EMAIL = "tt@zeitwert.io";
	private static final String TEST_JSON = "{ \"one\": \"one\", \"two\": 2 }";

	@Autowired
	private RequestContext requestCtx;

	@Autowired
	private ObjUserCache userCache;

	@Autowired
	private CodeCountryEnum countryEnum;

	@Autowired
	private ObjTestRepository testRepository;

	@Test
	public void testNodeList() throws Exception {

		assertTrue(this.testRepository != null, "testRepository not null");
		assertEquals("obj_test", this.testRepository.getAggregateType().getId());

		ObjTestPartNodeRepository testNodeRepository = testRepository.getNodeRepository();
		assertTrue(testNodeRepository != null, "testNodeRepository not null");
		CodePartListType nodeListType = ObjTestRepository.nodeListType();
		assertTrue(CodePartListTypeEnum.getPartListType("test.nodeList").equals(nodeListType), "nodeListType");

		ObjTest testA1 = this.testRepository.create(this.requestCtx.getTenantId());
		// assertTrue(((PartRepositoryBase<ObjTest, ?>)
		// testNodeRepository).isInitialised(test1a));
		this.initObjTest(testA1, "One", USER_EMAIL, "ch");
		Integer testA_id = testA1.getId();

		assertEquals(0, testA1.getNodeList().size());
		assertEquals(0, testNodeRepository.getParts(testA1, nodeListType).size());
		// assertEquals(0, ((PartRepositoryBase<ObjTest, ?>)
		// testNodeRepository).getParts(test1a).size());

		ObjTestPartNode testA1_n0 = testA1.addNode();
		this.initObjTestPartNode(testA1_n0, "First", "ch");
		assertEquals(PartPersistenceStatus.CREATED, testA1_n0.getMeta().getPersistenceStatus());
		UpdatableRecord<?> dbRecord = ((PartState) ((PartSPI<?>) testA1_n0).getPartState()).dbRecord();
		assertNotNull(dbRecord.getValue(PartFields.ID));
		assertTrue(dbRecord.changed(PartFields.ID));
		assertNull(dbRecord.original(PartFields.ID));
		assertEquals(1, testA1.getNodeList().size());
		assertEquals(1, testNodeRepository.getParts(testA1, nodeListType).size());
		// assertEquals(1, ((PartRepositoryBase<ObjTest, ?>)
		// testNodeRepository).getParts(test1a).size());

		ObjTestPartNode testA1_n1 = testA1.addNode();
		this.initObjTestPartNode(testA1_n1, "Second", "de");
		assertEquals(PartPersistenceStatus.CREATED, testA1_n1.getMeta().getPersistenceStatus());
		ObjTestPartNode testA1_n2 = testA1.addNode();
		this.initObjTestPartNode(testA1_n2, "Third", "es");
		assertEquals(PartPersistenceStatus.CREATED, testA1_n2.getMeta().getPersistenceStatus());

		assertEquals(3, testA1.getNodeList().size());
		assertEquals(3, testNodeRepository.getParts(testA1, nodeListType).size());
		// assertEquals(3, ((PartRepositoryBase<ObjTest, ?>)
		// testNodeRepository).getParts(test1a).size());

		List<ObjTestPartNode> testA1_nodeList = testA1.getNodeList();
		assertEquals(testA1_n0, testA1_nodeList.get(0));
		assertEquals(testA1_n1, testA1_nodeList.get(1));
		assertEquals(testA1_n2, testA1_nodeList.get(2));
		assertEquals(testA1_nodeList, List.of(testA1_n0, testA1_n1, testA1_n2));
		assertEquals("Short Test Node First,Short Test Node Second,Short Test Node Third",
				String.join(",", testA1.getNodeList().stream().map(n -> n.getShortText()).toList()));
		assertEquals("Short Test Node Second", testA1.getNodeList().get(1).getShortText());
		assertEquals(3, testNodeRepository.getParts(testA1, nodeListType).size());

		testA1.removeNode(testA1_n1.getId());
		assertEquals(2, testA1.getNodeCount());
		assertEquals(testA1_n2, testA1.getNode(1));
		assertEquals(testA1_n2, testA1.getNodeById(testA1_n2.getId()));
		assertEquals(PartPersistenceStatus.DELETED, testA1_n1.getMeta().getPersistenceStatus());
		assertEquals(2, testA1.getNodeList().size());
		assertEquals(3, testNodeRepository.getParts(testA1, nodeListType).size());
		// assertEquals(3, ((PartRepositoryBase<ObjTest, ?>)
		// testNodeRepository).getParts(test1a).size());
		assertEquals(testA1_n0.getShortText(), testA1.getNode(0).getShortText());
		assertEquals(testA1_n2.getShortText(), testA1.getNode(1).getShortText());

		testA1_nodeList = testA1.getNodeList();
		assertEquals(testA1_n0.getShortText(), testA1_nodeList.get(0).getShortText());
		assertEquals(testA1_n2.getShortText(), testA1_nodeList.get(1).getShortText());
		assertEquals(testA1_nodeList, List.of(testA1_n0, testA1_n2));
		assertEquals(List.of(PartPersistenceStatus.CREATED, PartPersistenceStatus.DELETED, PartPersistenceStatus.CREATED),
				testNodeRepository.getParts(testA1, nodeListType).stream().map(p -> p.getMeta().getPersistenceStatus())
						.toList());

		this.testRepository.store(testA1);
		// assertFalse(((PartRepositoryBase<ObjTest, ?>)
		// testNodeRepository).isInitialised(test1a));
		testA1 = null;

		ObjTest testA2 = this.testRepository.load(testA_id);

		assertEquals(2, testA2.getNodeList().size());
		assertEquals(2, testNodeRepository.getParts(testA2, nodeListType).size());
		// assertEquals(2, ((PartRepositoryBase<ObjTest, ?>)
		// testNodeRepository).getParts(test1b).size());
		assertEquals(List.of(PartPersistenceStatus.READ, PartPersistenceStatus.READ),
				testNodeRepository.getParts(testA2, nodeListType).stream().map(p -> p.getMeta().getPersistenceStatus())
						.toList());
		assertEquals("Short Test Node First,Short Test Node Third",
				String.join(",", testA2.getNodeList().stream().map(n -> n.getShortText()).toList()));
		assertEquals("Short Test Node Third", testA2.getNodeList().get(1).getShortText());

		ObjTestPartNode testA2_n2 = testA2.addNode();
		this.initObjTestPartNode(testA2_n2, "Fourth", "de");
		assertEquals(PartPersistenceStatus.CREATED, testA2_n2.getMeta().getPersistenceStatus());
		testA2.getNode(1).setInt(43);
		assertEquals(List.of(PartPersistenceStatus.READ, PartPersistenceStatus.UPDATED, PartPersistenceStatus.CREATED),
				testNodeRepository.getParts(testA2, nodeListType).stream().map(p -> p.getMeta().getPersistenceStatus())
						.toList());

		this.testRepository.store(testA2);
		// assertFalse(((PartRepositoryBase<ObjTest, ?>)
		// testNodeRepository).isInitialised(test1b));
		testA2 = null;

		ObjTest testA3 = this.testRepository.load(testA_id);

		assertEquals(3, testA3.getNodeList().size());
		assertEquals(3, testNodeRepository.getParts(testA3, nodeListType).size());
		// assertEquals(3, ((PartRepositoryBase<ObjTest, ?>)
		// testNodeRepository).getParts(test1c).size());
		assertEquals(List.of(PartPersistenceStatus.READ, PartPersistenceStatus.READ, PartPersistenceStatus.READ),
				testNodeRepository.getParts(testA3, nodeListType).stream().map(p -> p.getMeta().getPersistenceStatus())
						.toList());
		assertEquals("Short Test Node First,Short Test Node Third,Short Test Node Fourth",
				String.join(",", testA3.getNodeList().stream().map(n -> n.getShortText()).toList()));

		testA3.clearNodeList();
		assertEquals(0, testA3.getNodeCount());
		assertEquals(0, testA3.getNodeList().size());

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

	private void initObjTestPartNode(ObjTestPartNode node, String name, String countryId) {
		node.setShortText("Short Test Node " + name);
		node.setLongText("Long Test Node " + name);
		node.setInt(42);
		node.setNr(BigDecimal.valueOf(42));
		node.setIsDone(false);
		node.setDate(LocalDate.of(1966, 9, 8));
		node.setJson(JSON.valueOf(TEST_JSON).toString());
		CodeCountry country = this.countryEnum.getItem(countryId);
		node.setCountry(country);
	}

}
