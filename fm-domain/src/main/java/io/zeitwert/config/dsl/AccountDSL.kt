package io.zeitwert.config.dsl

import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.account.model.enums.CodeAccountType
import io.zeitwert.fm.contact.model.ObjContactRepository
import io.zeitwert.fm.contact.model.enums.CodeContactRole
import io.zeitwert.fm.contact.model.enums.CodeSalutation
import java.time.LocalDate

/**
 * DSL for creating accounts with nested contacts using Spring repositories.
 *
 * This DSL uses the repository layer to create accounts and contacts, which ensures proper domain
 * logic is executed (e.g., creating logo images, setting account associations, etc.).
 *
 * Usage:
 * ```
 * Account.init(accountRepository, contactRepository)
 * Account("TA", "Testlingen", "client") {
 *     contact("Max", "Muster", "max.muster@test.ch", "councilor") {
 *         salutation = "mr"
 *         phone = "+41 44 123 45 67"
 *     }
 * }
 * ```
 */
object Account {

	lateinit var accountRepository: ObjAccountRepository
	lateinit var contactRepository: ObjContactRepository

	fun init(
		accountRepo: ObjAccountRepository,
		contactRepo: ObjContactRepository,
	) {
		accountRepository = accountRepo
		contactRepository = contactRepo
	}

	operator fun invoke(
		key: String,
		name: String,
		accountType: String,
		init: AccountContext.() -> Unit = {},
	): Int {
		val context = AccountContext(key, name, accountType).apply(init)
		return createOrGetAccount(context)
	}

	private fun createOrGetAccount(ctx: AccountContext): Int {
		// Check if account already exists
		val existingAccount = accountRepository.getByKey(ctx.key)

		if (existingAccount.isPresent) {
			val account = existingAccount.get()
			val accountId = account.id as Int
			println("    Account ${ctx.key} already exists (id=$accountId)")
			// Still create contacts that don't exist
			ctx.contacts.forEach { contactCtx -> createContact(account, contactCtx) }
			return accountId
		}

		// Create new account via repository
		val account = accountRepository.create()
		account.key = ctx.key
		account.name = ctx.name
		account.accountType = CodeAccountType.getAccountType(ctx.accountType)
		accountRepository.store(account)

		val accountId = account.id as Int
		println("    Created account ${ctx.key} - ${ctx.name} (id=$accountId)")

		// Create contacts for this account
		ctx.contacts.forEach { contactCtx -> createContact(account, contactCtx) }

		return accountId
	}

	private fun createContact(
		account: ObjAccount,
		ctx: ContactContext,
	): Int {
		// Create new contact via repository
		val contact = contactRepository.create()
		contact.accountId = account.id
		contact.firstName = ctx.firstName
		contact.lastName = ctx.lastName
		contact.email = ctx.email
		contact.contactRole = CodeContactRole.getContactRole(ctx.contactRole)
		ctx.salutation?.let { contact.salutation = CodeSalutation.getSalutation(it) }
		ctx.phone?.let { contact.phone = it }
		ctx.mobile?.let { contact.mobile = it }
		ctx.birthDate?.let { contact.birthDate = it }
		contactRepository.store(contact)

		val contactId = contact.id as Int
		println("      Created contact ${ctx.firstName} ${ctx.lastName} (id=$contactId)")

		return contactId
	}

}

@DslMarker
annotation class AccountDslMarker

@AccountDslMarker
class AccountContext(
	val key: String,
	val name: String,
	val accountType: String,
) {

	internal val contacts = mutableListOf<ContactContext>()

	fun contact(
		firstName: String,
		lastName: String,
		email: String,
		contactRole: String,
		init: ContactContext.() -> Unit = {},
	) {
		contacts += ContactContext(firstName, lastName, email, contactRole).apply(init)
	}

}

@AccountDslMarker
class ContactContext(
	val firstName: String,
	val lastName: String,
	val email: String,
	val contactRole: String,
) {

	var salutation: String? = null
	var phone: String? = null
	var mobile: String? = null
	var birthDate: LocalDate? = null
}
