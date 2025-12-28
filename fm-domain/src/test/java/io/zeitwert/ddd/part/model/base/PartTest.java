package io.zeitwert.ddd.part.model.base;

import io.zeitwert.dddrive.app.model.RequestContext;
import io.zeitwert.fm.test.model.ObjTest;
import io.zeitwert.fm.test.model.ObjTestPartNode;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test")
public class PartTest {

	private static final String TEST_JSON = "{ \"one\": \"one\", \"two\": 2 }";

	@Autowired
	private RequestContext requestCtx;

	// CodeTestType is now a Kotlin enum with companion object Enumeration

	@Autowired
	private ObjTestRepository testRepository;

	@Test
	public void testNodeList() throws Exception {

		assertNotNull(this.testRepository, "testRepository not null");
		assertEquals("obj_test", this.testRepository.getAggregateType().getId());

		ObjTest testA1 = this.testRepository.create();
		this.initObjTest(testA1, "One", "type_a");
		Object testA_id = testA1.getId();

		assertEquals(0, testA1.getNodeList().size());

		ObjTestPartNode testA1_n0 = testA1.getNodeList().add((Integer) null);
		this.initObjTestPartNode(testA1_n0, "First", "type_a");
		assertEquals(1, testA1.getNodeList().size());

		ObjTestPartNode testA1_n1 = testA1.getNodeList().add((Integer) null);
		this.initObjTestPartNode(testA1_n1, "Second", "type_b");
		ObjTestPartNode testA1_n2 = testA1.getNodeList().add((Integer) null);
		this.initObjTestPartNode(testA1_n2, "Third", "type_c");

		assertEquals(3, testA1.getNodeList().size());

		var testA1_nodeList = testA1.getNodeList();
		assertEquals(testA1_n0, testA1_nodeList.get(0));
		assertEquals(testA1_n1, testA1_nodeList.get(1));
		assertEquals(testA1_n2, testA1_nodeList.get(2));
		assertEquals(testA1_nodeList.stream().toList(), List.of(testA1_n0, testA1_n1, testA1_n2));
		assertEquals("Short Test Node First,Short Test Node Second,Short Test Node Third",
				String.join(",", testA1.getNodeList().stream().map(ObjTestPartNode::getShortText).toList()));
		assertEquals("Short Test Node Second", testA1.getNodeList().get(1).getShortText());

		testA1.getNodeList().remove(testA1_n1.getId());
		assertEquals(2, testA1.getNodeList().size());
		assertEquals(testA1_n2, testA1.getNodeList().get(1));
		assertEquals(testA1_n2, testA1.getNodeList().getById(testA1_n2.getId()));
		assertEquals(2, testA1.getNodeList().size());
		assertEquals(testA1_n0.getShortText(), testA1.getNodeList().get(0).getShortText());
		assertEquals(testA1_n2.getShortText(), testA1.getNodeList().get(1).getShortText());

		testA1_nodeList = testA1.getNodeList();
		assertEquals(testA1_n0.getShortText(), testA1_nodeList.get(0).getShortText());
		assertEquals(testA1_n2.getShortText(), testA1_nodeList.get(1).getShortText());
		assertEquals(testA1_nodeList.stream().toList(), List.of(testA1_n0, testA1_n2));

		this.testRepository.store(testA1);
		testA1 = null;

		ObjTest testA2 = this.testRepository.load(testA_id);

		assertEquals(2, testA2.getNodeList().size());
		assertEquals("Short Test Node First,Short Test Node Third",
				String.join(",", testA2.getNodeList().stream().map(ObjTestPartNode::getShortText).toList()));
		assertEquals("Short Test Node Third", testA2.getNodeList().get(1).getShortText());

		ObjTestPartNode testA2_n2 = testA2.getNodeList().add((Integer) null);
		this.initObjTestPartNode(testA2_n2, "Fourth", "type_b");
		testA2.getNodeList().get(1).setInt(43);

		this.testRepository.store(testA2);
		testA2 = null;

		ObjTest testA3 = this.testRepository.load(testA_id);

		assertEquals(3, testA3.getNodeList().size());
		assertEquals("Short Test Node First,Short Test Node Third,Short Test Node Fourth",
				String.join(",", testA3.getNodeList().stream().map(ObjTestPartNode::getShortText).toList()));

		testA3.getNodeList().clear();
		assertEquals(0, testA3.getNodeList().size());
		assertEquals(0, testA3.getNodeList().size());

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

	private void initObjTestPartNode(ObjTestPartNode node, String name, String testTypeId) {
		node.setShortText("Short Test Node " + name);
		node.setLongText("Long Test Node " + name);
		node.setInt(42);
		node.setNr(BigDecimal.valueOf(42));
		node.setDone(false);
		node.setDate(LocalDate.of(1966, 9, 8));
		node.setJson(JSON.valueOf(TEST_JSON).toString());
		CodeTestType testType = CodeTestType.Enumeration.getTestType(testTypeId);
		node.setTestType(testType);
	}

}
