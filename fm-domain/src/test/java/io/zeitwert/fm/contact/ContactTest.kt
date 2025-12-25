package io.zeitwert.fm.contact

import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.app.model.RequestContextFM
import io.zeitwert.fm.contact.model.ObjContact
import io.zeitwert.fm.contact.model.ObjContactRepository
import io.zeitwert.fm.contact.model.enums.CodeAddressChannel
import io.zeitwert.fm.contact.model.enums.CodeContactRole
import io.zeitwert.fm.contact.model.enums.CodeSalutation
import io.zeitwert.fm.contact.model.enums.CodeTitle
import io.zeitwert.fm.oe.model.enums.CodeCountry
import io.zeitwert.test.TestApplication
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

@SpringBootTest(classes = [TestApplication::class])
@ActiveProfiles("test")
class ContactTest {

	@Autowired
	lateinit var requestCtx: RequestContextFM

	@Autowired
	lateinit var accountRepo: ObjAccountRepository

	@Autowired
	lateinit var contactRepository: ObjContactRepository

	@Test
	@Throws(Exception::class)
	fun testContact() {
		Assertions.assertEquals("obj_contact", this.contactRepository.aggregateType.id)

		val account = this.getTestAccount(requestCtx)
		val contactA1 = this.contactRepository.create(
			requestCtx.getTenantId(),
			requestCtx.getUserId(),
			requestCtx.getCurrentTime(),
		)

		Assertions.assertNotNull(contactA1, "contact not null")
		Assertions.assertNotNull(contactA1.id, "id not null")
		Assertions.assertNotNull(contactA1.tenant, "tenant not null")

		val contactA_id = contactA1.id as Int
		val contactA_idHash = System.identityHashCode(contactA1)

		Assertions.assertNotNull(contactA1.meta.createdByUser, "createdByUser not null")
		Assertions.assertNotNull(contactA1.meta.createdAt, "createdAt not null")

		contactA1.accountId = account.id
		this.initContact(contactA1)
		Assertions.assertEquals(account.id, contactA1.accountId, "account id")

		// Check initial addresses
		Assertions.assertEquals(2, contactA1.mailAddressList.size, "mail address count 2")
		Assertions.assertEquals(1, contactA1.electronicAddressList.size, "electronic address count 1")

		val mailAddr1 = contactA1.mailAddressList[0]
		val mailAddr1Id = mailAddr1.id

		val emailAddr1 = contactA1.electronicAddressList[0]
		val emailAddr1Id = emailAddr1.id

		this.checkContact(contactA1)

		this.contactRepository.store(contactA1, requestCtx.getUserId(), requestCtx.getCurrentTime())

		val contactA2 = this.contactRepository.load(contactA_id)
		val contactA2_idHash = System.identityHashCode(contactA2)
		Assertions.assertNotEquals(contactA_idHash, contactA2_idHash)
		Assertions.assertNotNull(contactA2.meta.modifiedByUser, "modifiedByUser not null")
		Assertions.assertNotNull(contactA2.meta.modifiedAt, "modifiedAt not null")
		Assertions.assertEquals(account.id, contactA2.accountId, "account id")

		this.checkContact(contactA2)

		// Verify addresses persisted
		Assertions.assertEquals(2, contactA2.mailAddressList.size, "mail address count 2 after reload")
		Assertions.assertEquals(1, contactA2.electronicAddressList.size, "electronic address count 1 after reload")

		// Find the mail address we created
		val loadedMailAddr1 = contactA2.mailAddressList.getById(mailAddr1Id)
		Assertions.assertNotNull(loadedMailAddr1, "mail address should exist")
		Assertions.assertEquals("Home", loadedMailAddr1.name, "mail address name")
		Assertions.assertEquals("Teststrasse 10", loadedMailAddr1.street, "mail address street")
		Assertions.assertEquals("1111", loadedMailAddr1.zip, "mail address zip")
		Assertions.assertEquals("Testingen", loadedMailAddr1.city, "mail address city")
		Assertions.assertEquals(CodeCountry.CH, loadedMailAddr1.country, "mail address country")

		// Find the email address we created
		val loadedEmailAddr1 = contactA2.electronicAddressList.getById(emailAddr1Id)
		Assertions.assertNotNull(loadedEmailAddr1, "email address should exist")
		Assertions.assertEquals("Work Email", loadedEmailAddr1.name, "email address name")
		Assertions.assertEquals(CodeAddressChannel.EMAIL, loadedEmailAddr1.addressChannel, "email address channel")

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

		Assertions.assertEquals(2, contactA2.mailAddressList.size, "mail address count 2 after add/remove")

		this.contactRepository.store(contactA2, requestCtx.getUserId(), requestCtx.getCurrentTime())

		val contactA3 = this.contactRepository.get(contactA_id)

		Assertions.assertEquals(2, contactA3.mailAddressList.size, "mail address count 2 after reload")
		Assertions.assertEquals(1, contactA3.electronicAddressList.size, "electronic address count 1 after reload")

		// Verify the new address exists
		val loadedMailAddr3 = contactA3.mailAddressList.getById(mailAddr3Id)
		Assertions.assertNotNull(loadedMailAddr3, "new mail address should exist")
		Assertions.assertEquals("Office", loadedMailAddr3.name, "new mail address name")
		Assertions.assertEquals("Büroweg 5", loadedMailAddr3.street, "new mail address street")

		// Verify the deleted address is gone
		val hasException = try {
			contactA3.mailAddressList.getById(mailAddr1Id)
			false
		} catch (e: RuntimeException) {
			true
		}
		Assertions.assertTrue(hasException, "deleted mail address should not exist")
	}

	private fun getTestAccount(requestCtx: RequestContextFM): ObjAccount = this.accountRepo.get(this.accountRepo.find(null)[0])

	private fun initContact(contact: ObjContact) {
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

	private fun checkContact(contact: ObjContact) {
		Assertions.assertEquals(CodeContactRole.CARETAKER, contact.contactRole)
		Assertions.assertEquals(CodeSalutation.MR, contact.salutation)
		Assertions.assertEquals(CodeTitle.DR, contact.title)
		Assertions.assertEquals("Max", contact.firstName)
		Assertions.assertEquals("Mustermann", contact.lastName)
		Assertions.assertEquals(LocalDate.of(1980, 5, 15), contact.birthDate)
		Assertions.assertEquals("+41 44 123 45 67", contact.phone)
		Assertions.assertEquals("+41 79 123 45 67", contact.mobile)
		Assertions.assertEquals("max.mustermann@example.com", contact.email)
		Assertions.assertEquals("Test contact description", contact.description)
	}

}
