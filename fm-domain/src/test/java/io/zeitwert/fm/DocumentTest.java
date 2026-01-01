package io.zeitwert.fm;

import io.zeitwert.dddrive.app.model.SessionContext;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.account.model.enums.CodeAccountType;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.dms.model.enums.CodeContentKind;
import io.zeitwert.fm.dms.model.enums.CodeContentType;
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategory;
import io.zeitwert.fm.dms.model.enums.CodeDocumentKind;
import io.zeitwert.test.TestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test")
public class DocumentTest {

	static ObjAccount Account;
	static String TEST_PNG_CONTENT = "PNG-PNG-PNG-PNG-PNG-PNG-PNG";
	static String TEST_JPG_CONTENT = "JPEG-JPEG-JPEG";

	@Autowired
	private SessionContext requestCtx;

	@Autowired
	private ObjAccountRepository accountRepo;

	@Autowired
	private ObjDocumentRepository documentRepository;

	@Test
	public void testDocument() throws Exception {

		Object tenantId = requestCtx.getTenantId();
		Object userId = requestCtx.getUser().getId();
		OffsetDateTime timestamp = requestCtx.getCurrentTime();

		this.getTestData(tenantId, userId, timestamp);

		assertNotNull(this.documentRepository, "documentRepository not null");
		assertEquals("obj_document", this.documentRepository.getAggregateType().getId());

		ObjDocument documentA1 = this.documentRepository.create();

		assertNotNull(documentA1, "test not null");
		assertNotNull(documentA1.getId(), "id not null");
		assertNotNull(documentA1.getTenantId(), "tenant not null");

		Object documentA_id = documentA1.getId();
		Integer documentA_idHash = System.identityHashCode(documentA1);

		assertNotNull(documentA1.getMeta().getCreatedByUserId(), "createdByUser not null");
		assertNotNull(documentA1.getMeta().getCreatedAt(), "createdAt not null");

		this.initDocument(documentA1);
		this.checkDocument(documentA1);

		this.documentRepository.store(documentA1);
		this.documentRepository.storeContent(documentA1, CodeContentType.PNG, TEST_PNG_CONTENT.getBytes(StandardCharsets.UTF_8), userId, timestamp);
		assertEquals(CodeContentType.PNG, this.documentRepository.getContentType(documentA1));
		assertEquals(TEST_PNG_CONTENT, new String(this.documentRepository.getContent(documentA1), StandardCharsets.UTF_8));

		documentA1 = null;

		ObjDocument documentA2 = this.documentRepository.load(documentA_id);
		Integer document1bIdHash = System.identityHashCode(documentA2);

		assertNotEquals(documentA_idHash, document1bIdHash);
		assertNotNull(documentA2.getMeta().getModifiedByUserId(), "modifiedByUser not null");
		assertNotNull(documentA2.getMeta().getModifiedAt(), "modifiedAt not null");

		assertEquals(CodeContentType.PNG, this.documentRepository.getContentType(documentA2));
		assertEquals(TEST_PNG_CONTENT, new String(this.documentRepository.getContent(documentA2), StandardCharsets.UTF_8));

		this.documentRepository.storeContent(documentA2, CodeContentType.JPG, TEST_JPG_CONTENT.getBytes(StandardCharsets.UTF_8), userId, timestamp);
		assertEquals(CodeContentType.JPG, this.documentRepository.getContentType(documentA2));
		assertEquals(TEST_JPG_CONTENT, new String(this.documentRepository.getContent(documentA2), StandardCharsets.UTF_8));

		this.checkDocument(documentA2);
	}

	private void getTestData(Object tenantId, Object userId, OffsetDateTime timestamp) throws Exception {
		Account = accountRepo.create();
		Account.setName("Test HH");
		Account.setAccountType(CodeAccountType.CLIENT);
		accountRepo.store(Account);
		assertNotNull(Account, "account");
	}

	private void initDocument(ObjDocument document) {
		document.setAccountId(Account.getId());
		document.setName("Schulhaus Isenweg");
		document.setContentKind(CodeContentKind.FOTO);
		document.setDocumentKind(CodeDocumentKind.STANDALONE);
		document.setDocumentCategory(CodeDocumentCategory.FOTO);
	}

	private void checkDocument(ObjDocument document) {
		assertEquals(Account.getId(), document.getAccountId(), "account id");
		assertEquals("Schulhaus Isenweg", document.getName());
		assertEquals(CodeContentKind.FOTO, document.getContentKind());
		assertEquals(CodeDocumentKind.STANDALONE, document.getDocumentKind());
		assertEquals(CodeDocumentCategory.FOTO, document.getDocumentCategory());
	}

}
