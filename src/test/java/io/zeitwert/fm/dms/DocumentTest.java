
package io.zeitwert.fm.dms;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

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
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.server.Application;

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

		ObjDocument document1a = this.documentRepository.create(this.requestCtx.getTenantId());

		assertNotNull(document1a, "test not null");
		assertNotNull(document1a.getId(), "id not null");
		assertNotNull(document1a.getTenant(), "tenant not null");

		Integer document1Id = document1a.getId();
		Integer document1aIdHash = System.identityHashCode(document1a);

		assertNotNull(document1a.getMeta().getCreatedByUser(), "createdByUser not null");
		assertNotNull(document1a.getMeta().getCreatedAt(), "createdAt not null");

		this.initDocument(document1a);
		this.checkDocument(document1a);

		this.documentRepository.store(document1a);

		this.documentRepository.storeContent(this.requestCtx, document1a, PNG,
				TEST_PNG_CONTENT.getBytes(StandardCharsets.UTF_8));
		assertEquals(PNG, this.documentRepository.getContentType(document1a));
		assertEquals(TEST_PNG_CONTENT, new String(this.documentRepository.getContent(document1a), StandardCharsets.UTF_8));

		document1a = null;

		ObjDocument document1b = this.documentRepository.get(document1Id);
		Integer document1bIdHash = System.identityHashCode(document1b);

		assertNotEquals(document1aIdHash, document1bIdHash);
		assertNotNull(document1b.getMeta().getModifiedByUser(), "modifiedByUser not null");
		assertNotNull(document1b.getMeta().getModifiedAt(), "modifiedAt not null");

		assertEquals(PNG, this.documentRepository.getContentType(document1b));
		assertEquals(TEST_PNG_CONTENT, new String(this.documentRepository.getContent(document1b), StandardCharsets.UTF_8));

		this.documentRepository.storeContent(this.requestCtx, document1b, JPG,
				TEST_JPG_CONTENT.getBytes(StandardCharsets.UTF_8));
		assertEquals(JPG, this.documentRepository.getContentType(document1b));
		assertEquals(TEST_JPG_CONTENT, new String(this.documentRepository.getContent(document1b), StandardCharsets.UTF_8));

		this.checkDocument(document1b);
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
