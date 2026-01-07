package io.zeitwert.fm

import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.account.model.enums.CodeAccountType
import io.zeitwert.test.TestApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.*

@SpringBootTest(classes = [TestApplication::class])
@ActiveProfiles("test")
class AccountTest {

	@Autowired
	private lateinit var sessionContext: SessionContext

	@Autowired
	private lateinit var accountRepository: ObjAccountRepository

	@Test
	fun testAccountBase() {
		assertNotNull(accountRepository, "accountRepository not null")
		assertEquals("obj_account", accountRepository.aggregateType.id)

		val account = accountRepository.create()
		assertNotNull(account, "account not null")
		assertNotNull(account.id, "id not null")
		assertNotNull(account.tenantId, "tenantId not null")

		account.name = "Test Account"
		account.accountType = CodeAccountType.CLIENT
		account.description = "Test description"

		accountRepository.store(account)

		val accountId = account.id
		val loadedAccount = accountRepository.load(accountId)

		assertEquals("Test Account", loadedAccount.name)
		assertEquals(CodeAccountType.CLIENT, loadedAccount.accountType)
		assertEquals("Test description", loadedAccount.description)
	}

	@Test
	fun testAccountGetByKey() {
		val uniqueKey = "account-key-${UUID.randomUUID().toString().substring(0, 8)}"

		// Create account with unique key
		val account = accountRepository.create()
		account.key = uniqueKey
		account.name = "Account with key"
		account.accountType = CodeAccountType.CLIENT
		accountRepository.store(account)

		val accountId = account.id

		// Verify getByKey returns the correct account
		val foundAccount = accountRepository.getByKey(uniqueKey)
		assertTrue(foundAccount.isPresent, "account should be found by key")
		assertEquals(accountId, foundAccount.get().id, "found account should have correct id")
		assertEquals(uniqueKey, foundAccount.get().key, "found account should have correct key")

		// Verify getByKey returns empty for non-existent key
		val notFound = accountRepository.getByKey("non-existent-key-${UUID.randomUUID()}")
		assertFalse(notFound.isPresent, "non-existent key should return empty")
	}

	@Test
	fun testAccountKeyPersistence() {
		val uniqueKey = "persist-key-${UUID.randomUUID().toString().substring(0, 8)}"

		// Create and store account with key
		val account = accountRepository.create()
		account.key = uniqueKey
		account.name = "Account for persistence test"
		account.accountType = CodeAccountType.CLIENT
		accountRepository.store(account)

		val accountId = account.id

		// Load account and verify key is persisted
		val loadedAccount = accountRepository.load(accountId)
		assertEquals(uniqueKey, loadedAccount.key, "key should be persisted")

		// Update key
		val newKey = "updated-key-${UUID.randomUUID().toString().substring(0, 8)}"
		loadedAccount.key = newKey
		accountRepository.store(loadedAccount)

		// Verify updated key
		val reloadedAccount = accountRepository.load(accountId)
		assertEquals(newKey, reloadedAccount.key, "updated key should be persisted")

		// Verify old key no longer works
		val oldKeySearch = accountRepository.getByKey(uniqueKey)
		assertFalse(oldKeySearch.isPresent, "old key should not find account")

		// Verify new key works
		val newKeySearch = accountRepository.getByKey(newKey)
		assertTrue(newKeySearch.isPresent, "new key should find account")
		assertEquals(accountId, newKeySearch.get().id, "new key should find correct account")
	}

}
