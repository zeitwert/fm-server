
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

import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.part.model.enums.CodePartListTypeEnum;
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.fm.account.model.enums.CodeCountry;
import io.zeitwert.fm.account.model.enums.CodeCountryEnum;
import io.zeitwert.fm.test.model.ObjTest;
import io.zeitwert.fm.test.model.ObjTestPartNode;
import io.zeitwert.fm.test.model.ObjTestPartNodeRepository;
import io.zeitwert.fm.test.model.ObjTestRepository;
import io.zeitwert.fm.test.model.base.ObjTestFields;
import io.zeitwert.server.Application;

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
	private ObjUserRepository userRepository;

	@Autowired
	private CodeCountryEnum countryEnum;

	@Autowired
	private ObjTestRepository testRepository;

	@Test
	public void testNodeList() throws Exception {

		assertTrue(this.testRepository != null, "testRepository not null");
		assertEquals("obj_test", this.testRepository.getAggregateType().getId());

		ObjTestPartNodeRepository testNodeRepository = this.testRepository.getNodeRepository();
		assertTrue(testNodeRepository != null, "testNodeRepository not null");
		CodePartListType nodeListType = CodePartListTypeEnum.getPartListType(ObjTestFields.NODE_LIST);
		assertTrue(CodePartListTypeEnum.getPartListType(ObjTestFields.NODE_LIST).equals(nodeListType), "nodeListType");

		ObjTest test1a = this.testRepository.create(this.requestCtx.getTenantId());
		// assertTrue(((PartRepositoryBase<ObjTest, ?>)
		// testNodeRepository).isInitialised(test1a));
		this.initObjTest(test1a, "One", USER_EMAIL, "ch");
		Integer test1Id = test1a.getId();

		assertEquals(0, test1a.getNodeList().size());
		assertEquals(0, testNodeRepository.getParts(test1a, nodeListType).size());
		// assertEquals(0, ((PartRepositoryBase<ObjTest, ?>)
		// testNodeRepository).getParts(test1a).size());

		ObjTestPartNode node1a0 = test1a.addNode();
		this.initObjTestPartNode(node1a0, "First", "ch");
		assertEquals(PartStatus.CREATED, node1a0.getMeta().getStatus());
		UpdatableRecord<?> dbRecord = ((PartBase<?>) node1a0).getDbRecord();
		assertNotNull(dbRecord.getValue(PartFields.ID));
		assertTrue(dbRecord.changed(PartFields.ID));
		assertNull(dbRecord.original(PartFields.ID));
		assertEquals(1, test1a.getNodeList().size());
		assertEquals(1, testNodeRepository.getParts(test1a, nodeListType).size());
		// assertEquals(1, ((PartRepositoryBase<ObjTest, ?>)
		// testNodeRepository).getParts(test1a).size());

		ObjTestPartNode node1a1 = test1a.addNode();
		this.initObjTestPartNode(node1a1, "Second", "de");
		assertEquals(PartStatus.CREATED, node1a1.getMeta().getStatus());
		ObjTestPartNode node1a2 = test1a.addNode();
		this.initObjTestPartNode(node1a2, "Third", "es");
		assertEquals(PartStatus.CREATED, node1a2.getMeta().getStatus());

		assertEquals(3, test1a.getNodeList().size());
		assertEquals(3, testNodeRepository.getParts(test1a, nodeListType).size());
		// assertEquals(3, ((PartRepositoryBase<ObjTest, ?>)
		// testNodeRepository).getParts(test1a).size());

		List<ObjTestPartNode> test1aNodeList = test1a.getNodeList();
		assertEquals(node1a0, test1aNodeList.get(0));
		assertEquals(node1a1, test1aNodeList.get(1));
		assertEquals(node1a2, test1aNodeList.get(2));
		assertEquals(test1aNodeList, List.of(node1a0, node1a1, node1a2));
		assertEquals("Short Test Node First,Short Test Node Second,Short Test Node Third",
				String.join(",", test1a.getNodeList().stream().map(n -> n.getShortText()).toList()));
		assertEquals("Short Test Node Second", test1a.getNodeList().get(1).getShortText());
		assertEquals(3, testNodeRepository.getParts(test1a, nodeListType).size());

		test1a.removeNode(node1a1.getId());
		assertEquals(2, test1a.getNodeCount());
		assertEquals(node1a2, test1a.getNode(1));
		assertEquals(node1a2, test1a.getNodeById(node1a2.getId()));
		assertEquals(PartStatus.DELETED, node1a1.getMeta().getStatus());
		assertEquals(2, test1a.getNodeList().size());
		assertEquals(3, testNodeRepository.getParts(test1a, nodeListType).size());
		// assertEquals(3, ((PartRepositoryBase<ObjTest, ?>)
		// testNodeRepository).getParts(test1a).size());
		assertEquals(node1a0.getShortText(), test1a.getNode(0).getShortText());
		assertEquals(node1a2.getShortText(), test1a.getNode(1).getShortText());

		test1aNodeList = test1a.getNodeList();
		assertEquals(node1a0.getShortText(), test1aNodeList.get(0).getShortText());
		assertEquals(node1a2.getShortText(), test1aNodeList.get(1).getShortText());
		assertEquals(test1aNodeList, List.of(node1a0, node1a2));
		assertEquals(List.of(PartStatus.CREATED, PartStatus.DELETED, PartStatus.CREATED),
				testNodeRepository.getParts(test1a, nodeListType).stream().map(p -> p.getMeta().getStatus()).toList());

		this.testRepository.store(test1a);
		// assertFalse(((PartRepositoryBase<ObjTest, ?>)
		// testNodeRepository).isInitialised(test1a));
		test1a = null;

		ObjTest test1b = this.testRepository.get(test1Id);

		assertEquals(2, test1b.getNodeList().size());
		assertEquals(2, testNodeRepository.getParts(test1b, nodeListType).size());
		// assertEquals(2, ((PartRepositoryBase<ObjTest, ?>)
		// testNodeRepository).getParts(test1b).size());
		assertEquals(List.of(PartStatus.READ, PartStatus.READ),
				testNodeRepository.getParts(test1b, nodeListType).stream().map(p -> p.getMeta().getStatus()).toList());
		assertEquals("Short Test Node First,Short Test Node Third",
				String.join(",", test1b.getNodeList().stream().map(n -> n.getShortText()).toList()));
		assertEquals("Short Test Node Third", test1b.getNodeList().get(1).getShortText());

		ObjTestPartNode node1b2 = test1b.addNode();
		this.initObjTestPartNode(node1b2, "Fourth", "de");
		assertEquals(PartStatus.CREATED, node1b2.getMeta().getStatus());
		test1b.getNode(1).setInt(43);
		assertEquals(List.of(PartStatus.READ, PartStatus.UPDATED, PartStatus.CREATED),
				testNodeRepository.getParts(test1b, nodeListType).stream().map(p -> p.getMeta().getStatus()).toList());

		this.testRepository.store(test1b);
		// assertFalse(((PartRepositoryBase<ObjTest, ?>)
		// testNodeRepository).isInitialised(test1b));
		test1b = null;

		ObjTest test1c = this.testRepository.get(test1Id);

		assertEquals(3, test1c.getNodeList().size());
		assertEquals(3, testNodeRepository.getParts(test1c, nodeListType).size());
		// assertEquals(3, ((PartRepositoryBase<ObjTest, ?>)
		// testNodeRepository).getParts(test1c).size());
		assertEquals(List.of(PartStatus.READ, PartStatus.READ, PartStatus.READ),
				testNodeRepository.getParts(test1c, nodeListType).stream().map(p -> p.getMeta().getStatus()).toList());
		assertEquals("Short Test Node First,Short Test Node Third,Short Test Node Fourth",
				String.join(",", test1c.getNodeList().stream().map(n -> n.getShortText()).toList()));

		test1c.clearNodeList();
		assertEquals(0, test1c.getNodeCount());
		assertEquals(0, test1c.getNodeList().size());

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
