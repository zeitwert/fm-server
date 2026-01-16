package io.zeitwert.b_unit

import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.data.config.TestDataSetup
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.contact.model.ObjContact
import io.zeitwert.fm.contact.model.ObjContactRepository
import io.zeitwert.fm.contact.model.enums.CodeAddressChannel
import io.zeitwert.fm.contact.model.enums.CodeContactRole
import io.zeitwert.fm.contact.model.enums.CodeSalutation
import io.zeitwert.fm.contact.model.enums.CodeTitle
import io.zeitwert.fm.oe.model.enums.CodeCountry
import io.zeitwert.test.TestApplication
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest(classes = [TestApplication::class])
@ActiveProfiles("test")
class ContactTest {

	@Autowired
	lateinit var sessionContext: SessionContext

	@Autowired
	lateinit var accountRepo: ObjAccountRepository

	@Autowired
	lateinit var contactRepository: ObjContactRepository

	@Test
	@Throws(Exception::class)
	fun testContact() {
		assertEquals("obj_contact", contactRepository.aggregateType.id)

		val account = accountRepo.getByKey(TestDataSetup.TEST_ACCOUNT_KEY).get()
		val contactA1 = contactRepository.create()

		assertNotNull(contactA1, "contact not null")
		assertNotNull(contactA1.id, "id not null")
		assertNotNull(contactA1.tenantId, "tenant not null")

		val contactA_id = contactA1.id as Int
		val contactA_idHash = System.identityHashCode(contactA1)

		assertNotNull(contactA1.meta.createdByUserId, "createdByUser not null")
		assertNotNull(contactA1.meta.createdAt, "createdAt not null")

		contactA1.accountId = account.id
		initContact(contactA1)
		val contactA_key = contactA1.key!!
		assertEquals(account.id, contactA1.accountId, "account id")

		// Check initial addresses
		assertEquals(2, contactA1.mailAddressList.size, "mail address count 2")
		assertEquals(1, contactA1.electronicAddressList.size, "electronic address count 1")

		val mailAddr1 = contactA1.mailAddressList[0]
		val mailAddr1Id = mailAddr1.id

		val emailAddr1 = contactA1.electronicAddressList[0]
		val emailAddr1Id = emailAddr1.id

		checkContact(contactA1, contactA_key)

		contactRepository.store(contactA1)

		val contactA2 = contactRepository.load(contactA_id)
		val contactA2_idHash = System.identityHashCode(contactA2)
		assertNotEquals(contactA_idHash, contactA2_idHash)
		assertNotNull(contactA2.meta.modifiedByUserId, "modifiedByUser not null")
		assertNotNull(contactA2.meta.modifiedAt, "modifiedAt not null")
		assertEquals(account.id, contactA2.accountId, "account id")

		checkContact(contactA2, contactA_key)

		// Verify addresses persisted
		assertEquals(2, contactA2.mailAddressList.size, "mail address count 2 after reload")
		assertEquals(1, contactA2.electronicAddressList.size, "electronic address count 1 after reload")

		// Find the mail address we created
		val loadedMailAddr1 = contactA2.mailAddressList.getById(mailAddr1Id)
		assertNotNull(loadedMailAddr1, "mail address should exist")
		assertEquals("Home", loadedMailAddr1.name, "mail address name")
		assertEquals("Teststrasse 10", loadedMailAddr1.street, "mail address street")
		assertEquals("1111", loadedMailAddr1.zip, "mail address zip")
		assertEquals("Testingen", loadedMailAddr1.city, "mail address city")
		assertEquals(CodeCountry.CH, loadedMailAddr1.country, "mail address country")

		// Find the email address we created
		val loadedEmailAddr1 = contactA2.electronicAddressList.getById(emailAddr1Id)
		assertNotNull(loadedEmailAddr1, "email address should exist")
		assertEquals("Work Email", loadedEmailAddr1.name, "email address name")
		assertEquals(CodeAddressChannel.EMAIL, loadedEmailAddr1.addressChannel, "email address channel")

		// Add another mail address
		val mailAddr3 = contactA2.mailAddressList.add(null)
		mailAddr3.addressChannel = CodeAddressChannel.MAIL
		mailAddr3.name = "Office"
		mailAddr3.street = "Büroweg 5"
		mailAddr3.zip = "2222"
		mailAddr3.city = "Arbeitstadt"
		mailAddr3.country = CodeCountry.CH
		val mailAddr3Id = mailAddr3.id

		// Remove first mail address
		contactA2.mailAddressList.remove(mailAddr1Id)

		assertEquals(2, contactA2.mailAddressList.size, "mail address count 2 after add/remove")

		contactRepository.store(contactA2)

		val contactA3 = contactRepository.get(contactA_id)

		assertEquals(2, contactA3.mailAddressList.size, "mail address count 2 after reload")
		assertEquals(1, contactA3.electronicAddressList.size, "electronic address count 1 after reload")

		// Verify the new address exists
		val loadedMailAddr3 = contactA3.mailAddressList.getById(mailAddr3Id)
		assertNotNull(loadedMailAddr3, "new mail address should exist")
		assertEquals("Office", loadedMailAddr3.name, "new mail address name")
		assertEquals("Büroweg 5", loadedMailAddr3.street, "new mail address street")

		// Verify the deleted address is gone
		val hasException = try {
			contactA3.mailAddressList.getById(mailAddr1Id)
			false
		} catch (e: RuntimeException) {
			true
		}
		assertTrue(hasException, "deleted mail address should not exist")
	}

	private fun initContact(contact: ObjContact) {
		contact.key = "contact-key-${UUID.randomUUID().toString().substring(0, 8)}"
		contact.contactRole = CodeContactRole.CARETAKER
		contact.salutation = CodeSalutation.MR
		contact.title = CodeTitle.DR
		contact.firstName = "Max"
		contact.lastName = "Mustermann"
		contact.birthDate = LocalDate.of(1980, 5, 15)
		contact.phone = "+41 44 123 45 67"
		contact.mobile = "+41 79 123 45 67"
		contact.email = "max.mustermann@example.com"
		contact.description = "Test contact description"

		// Add mail addresses
		val mailAddr1 = contact.mailAddressList.add(null)
		mailAddr1.addressChannel = CodeAddressChannel.MAIL
		mailAddr1.name = "Home"
		mailAddr1.street = "Teststrasse 10"
		mailAddr1.zip = "1111"
		mailAddr1.city = "Testingen"
		mailAddr1.country = CodeCountry.CH

		val mailAddr2 = contact.mailAddressList.add(null)
		mailAddr2.addressChannel = CodeAddressChannel.MAIL
		mailAddr2.name = "Work"
		mailAddr2.street = "Arbeitsweg 20"
		mailAddr2.zip = "2222"
		mailAddr2.city = "Arbeitsort"
		mailAddr2.country = CodeCountry.CH

		// Add electronic address
		val emailAddr = contact.electronicAddressList.add(null)
		emailAddr.addressChannel = CodeAddressChannel.EMAIL
		emailAddr.name = "Work Email"
	}

	private fun checkContact(
		contact: ObjContact,
		key: String,
	) {
		assertEquals(key, contact.key)
		assertEquals(CodeContactRole.CARETAKER, contact.contactRole)
		assertEquals(CodeSalutation.MR, contact.salutation)
		assertEquals(CodeTitle.DR, contact.title)
		assertEquals("Max", contact.firstName)
		assertEquals("Mustermann", contact.lastName)
		assertEquals(LocalDate.of(1980, 5, 15), contact.birthDate)
		assertEquals("+41 44 123 45 67", contact.phone)
		assertEquals("+41 79 123 45 67", contact.mobile)
		assertEquals("max.mustermann@example.com", contact.email)
		assertEquals("Test contact description", contact.description)
	}

}
