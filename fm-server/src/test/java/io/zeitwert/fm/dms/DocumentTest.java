
package io.zeitwert.fm.dms;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.dddrive.app.model.RequestContext;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.account.service.api.ObjAccountCache;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.dms.model.enums.CodeContentKindEnum;
import io.zeitwert.fm.dms.model.enums.CodeContentType;
import io.zeitwert.fm.dms.model.enums.CodeContentTypeEnum;
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategoryEnum;
import io.zeitwert.fm.dms.model.enums.CodeDocumentKindEnum;
import io.zeitwert.fm.server.Application;

import java.nio.charset.StandardCharsets;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class DocumentTest {

	static ObjAccount Account;
	static CodeContentType PNG;
	static String TEST_PNG_CONTENT = "PNG-PNG-PNG-PNG-PNG-PNG-PNG";
	static CodeContentType JPG;
	static String TEST_JPG_CONTENT = "JPEG-JPEG-JPEG";

	@Autowired
	private RequestContext requestCtx;

	@Autowired
	private ObjAccountRepository accountRepo;

	@Autowired
	private ObjAccountCache accountCache;

	@Autowired
	private ObjDocumentRepository documentRepository;

	@Test
	public void testDocument() throws Exception {

		this.getTestData();

		assertTrue(this.documentRepository != null, "documentRepository not null");
		assertEquals("obj_document", this.documentRepository.getAggregateType().getId());

		ObjDocument documentA1 = this.documentRepository.create(this.requestCtx.getTenantId());

		assertNotNull(documentA1, "test not null");
		assertNotNull(documentA1.getId(), "id not null");
		assertNotNull(documentA1.getTenant(), "tenant not null");

		Integer documentA_id = documentA1.getId();
		Integer documentA_idHash = System.identityHashCode(documentA1);

		assertNotNull(documentA1.getMeta().getCreatedByUser(), "createdByUser not null");
		assertNotNull(documentA1.getMeta().getCreatedAt(), "createdAt not null");

		this.initDocument(documentA1);
		this.checkDocument(documentA1);

		this.documentRepository.store(documentA1);

		this.documentRepository.storeContent(this.requestCtx, documentA1, PNG,
				TEST_PNG_CONTENT.getBytes(StandardCharsets.UTF_8));
		assertEquals(PNG, this.documentRepository.getContentType(documentA1));
		assertEquals(TEST_PNG_CONTENT, new String(this.documentRepository.getContent(documentA1), StandardCharsets.UTF_8));

		documentA1 = null;

		ObjDocument documentA2 = this.documentRepository.load(documentA_id);
		Integer document1bIdHash = System.identityHashCode(documentA2);

		assertNotEquals(documentA_idHash, document1bIdHash);
		assertNotNull(documentA2.getMeta().getModifiedByUser(), "modifiedByUser not null");
		assertNotNull(documentA2.getMeta().getModifiedAt(), "modifiedAt not null");

		assertEquals(PNG, this.documentRepository.getContentType(documentA2));
		assertEquals(TEST_PNG_CONTENT, new String(this.documentRepository.getContent(documentA2), StandardCharsets.UTF_8));

		this.documentRepository.storeContent(this.requestCtx, documentA2, JPG,
				TEST_JPG_CONTENT.getBytes(StandardCharsets.UTF_8));
		assertEquals(JPG, this.documentRepository.getContentType(documentA2));
		assertEquals(TEST_JPG_CONTENT, new String(this.documentRepository.getContent(documentA2), StandardCharsets.UTF_8));

		this.checkDocument(documentA2);
	}

	private void getTestData() {
		PNG = CodeContentTypeEnum.getContentType("png");
		JPG = CodeContentTypeEnum.getContentType("jpg");
		Account = this.accountCache.get(this.accountRepo.find(null).get(0).getId());
		assertNotNull(Account, "account");
	}

	private void initDocument(ObjDocument document) {
		document.setAccountId(Account.getId());
		document.setName("Schulhaus Isenweg");
		document.setContentKind(CodeContentKindEnum.getContentKind("foto"));
		document.setDocumentKind(CodeDocumentKindEnum.getDocumentKind("standalone"));
		document.setDocumentCategory(CodeDocumentCategoryEnum.getDocumentCategory("foto"));
	}

	private void checkDocument(ObjDocument document) {
		assertEquals(Account.getId(), document.getAccountId(), "account id");
		assertEquals(Account.getId(), document.getAccount().getId(), Account.getId(), "account id");
		assertEquals(document.getName(), "Schulhaus Isenweg");
		assertEquals(document.getContentKind(), CodeContentKindEnum.getContentKind("foto"));
		assertEquals(document.getDocumentKind(), CodeDocumentKindEnum.getDocumentKind("standalone"));
		assertEquals(document.getDocumentCategory(), CodeDocumentCategoryEnum.getDocumentCategory("foto"));
	}

}
