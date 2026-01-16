package io.zeitwert.fm

import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.account.model.enums.CodeAccountType
import io.zeitwert.fm.dms.model.ObjDocument
import io.zeitwert.fm.dms.model.ObjDocumentRepository
import io.zeitwert.fm.dms.model.enums.CodeContentKind
import io.zeitwert.fm.dms.model.enums.CodeContentType
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategory
import io.zeitwert.fm.dms.model.enums.CodeDocumentKind
import io.zeitwert.test.TestApplication
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.nio.charset.StandardCharsets
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

@SpringBootTest(classes = [TestApplication::class])
@ActiveProfiles("test")
class DocumentTest {

	@Autowired
	lateinit var accountRepo: ObjAccountRepository

	@Autowired
	lateinit var documentRepository: ObjDocumentRepository

	@Autowired
	lateinit var sessionContext: SessionContext

	@Test
	@Throws(Exception::class)
	fun testDocument() {
		sessionContext.tenantId
		val userId = sessionContext.userId
		val timestamp = sessionContext.currentTime

		genTestData()

		var documentA1 = documentRepository.create()

		assertNotNull(documentA1.id, "id not null")
		assertNotNull(documentA1.tenantId, "tenant not null")

		val documentA_id = documentA1.id
		val documentA_idHash = System.identityHashCode(documentA1)

		assertNotNull(documentA1.meta.createdByUserId, "createdByUser not null")
		assertNotNull(documentA1.meta.createdAt, "createdAt not null")

		initDocument(documentA1)
		checkDocument(documentA1)

		documentRepository.store(documentA1)
		documentRepository.storeContent(
			documentA1,
			CodeContentType.PNG,
			TEST_PNG_CONTENT.toByteArray(
				StandardCharsets.UTF_8,
			),
			userId,
			timestamp,
		)
		assertEquals(CodeContentType.PNG, documentRepository.getContentType(documentA1))
		assertEquals(
			TEST_PNG_CONTENT,
			String(documentRepository.getContent(documentA1)!!, StandardCharsets.UTF_8),
		)

		val documentA2 = documentRepository.load(documentA_id)
		val document1bIdHash = System.identityHashCode(documentA2)

		assertNotEquals(documentA_idHash, document1bIdHash)
		assertNotNull(documentA2.meta.modifiedByUserId, "modifiedByUser not null")
		assertNotNull(documentA2.meta.modifiedAt, "modifiedAt not null")

		assertEquals(CodeContentType.PNG, documentRepository.getContentType(documentA2))
		assertEquals(
			TEST_PNG_CONTENT,
			String(documentRepository.getContent(documentA2)!!, StandardCharsets.UTF_8),
		)

		documentRepository.storeContent(
			documentA2,
			CodeContentType.JPG,
			TEST_JPG_CONTENT.toByteArray(
				StandardCharsets.UTF_8,
			),
			userId,
			timestamp,
		)
		assertEquals(CodeContentType.JPG, documentRepository.getContentType(documentA2))
		assertEquals(
			TEST_JPG_CONTENT,
			String(documentRepository.getContent(documentA2)!!, StandardCharsets.UTF_8),
		)

		checkDocument(documentA2)
	}

	private fun genTestData() {
		Account = accountRepo.create()
		Account.name = "Test HH"
		Account.accountType = CodeAccountType.CLIENT
		accountRepo.transaction {
			accountRepo.store(Account)
		}
		assertNotNull(Account, "account")
	}

	private fun initDocument(document: ObjDocument) {
		document.accountId = Account.id
		document.name = "Schulhaus Isenweg"
		document.contentKind = CodeContentKind.FOTO
		document.documentKind = CodeDocumentKind.STANDALONE
		document.documentCategory = CodeDocumentCategory.FOTO
	}

	private fun checkDocument(document: ObjDocument) {
		assertEquals(Account.id, document.accountId, "account id")
		assertEquals("Schulhaus Isenweg", document.name)
		assertEquals(CodeContentKind.FOTO, document.contentKind)
		assertEquals(CodeDocumentKind.STANDALONE, document.documentKind)
		assertEquals(CodeDocumentCategory.FOTO, document.documentCategory)
	}

	companion object {

		lateinit var Account: ObjAccount
		const val TEST_PNG_CONTENT: String = "PNG-PNG-PNG-PNG-PNG-PNG-PNG"
		const val TEST_JPG_CONTENT: String = "JPEG-JPEG-JPEG"

	}

}
