package io.zeitwert.config.dsl

import dddrive.ddd.core.model.RepositoryDirectory
import io.zeitwert.config.DelegatingSessionContext
import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.account.model.enums.CodeAccountType
import io.zeitwert.fm.contact.model.ObjContact
import io.zeitwert.fm.contact.model.ObjContactRepository
import io.zeitwert.fm.contact.model.enums.CodeContactRole
import io.zeitwert.fm.contact.model.enums.CodeSalutation
import org.jooq.DSLContext
import java.time.LocalDate

/**
 * DSL for creating accounts with nested contacts using Spring repositories.
 *
 * This DSL uses the repository layer to create accounts and contacts, which ensures proper domain
 * logic is executed (e.g., creating logo images, setting account associations, etc.).
 *
 * Usage:
 * ```
 * Account.init(accountRepository, contactRepository, documentRepository)
 * Account("TA", "Testlingen", "client") {
 *     contact("Max", "Muster", "max.muster@test.ch", "councilor") {
 *         salutation = "mr"
 *         phone = "+41 44 123 45 67"
 *     }
 * }
 * ```
 */
object Account {

	lateinit var dslContext: DSLContext
	lateinit var directory: RepositoryDirectory

	val accountRepository: ObjAccountRepository
		get() = directory.getRepository(ObjAccount::class.java) as ObjAccountRepository

	val contactRepository: ObjContactRepository
		get() = directory.getRepository(ObjContact::class.java) as ObjContactRepository

	fun init(
		dslContext: DSLContext,
		directory: RepositoryDirectory,
	) {
		this.dslContext = dslContext
		this.directory = directory
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
			// ctx.contacts.forEach { contactCtx -> createContact(account, contactCtx) }
			return accountId
		}

		// Create new account via repository
		val account = accountRepository.create()
		account.key = ctx.key
		account.name = ctx.name
		account.accountType = CodeAccountType.getAccountType(ctx.accountType)

		// Set account ID in session context for nested operations
		DelegatingSessionContext.setSetupAccountId(account.id as Int)

		dslContext.transaction { _ ->
			accountRepository.store(account)
		}

		val accountId = account.id as Int
		println("    Created account ${ctx.key} - ${ctx.name} (id=$accountId)")

		// Create contacts for this account
		ctx.contacts.forEach { contactCtx -> createContact(account, contactCtx) }

		// Create buildings for this account
		ctx.buildings.forEach { buildingCtx ->
			Building(
				account,
				buildingCtx.name,
				buildingCtx.street,
				buildingCtx.zip,
				buildingCtx.city,
			) { copyFrom(buildingCtx) }
		}

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

		dslContext.transaction { _ ->
			contactRepository.store(contact)
		}

		val contactId = contact.id as Int
		println("      Created contact ${ctx.firstName} ${ctx.lastName} (id=$contactId)")

		return contactId
	}

}

@TenantDslMarker
class AccountContext(
	val key: String,
	val name: String,
	val accountType: String,
) {

	internal val contacts = mutableListOf<ContactContext>()
	internal val buildings = mutableListOf<BuildingContext>()

	fun contact(
		firstName: String,
		lastName: String,
		email: String,
		contactRole: String,
		init: ContactContext.() -> Unit = {},
	) {
		contacts += ContactContext(firstName, lastName, email, contactRole).apply(init)
	}

	/**
	 * Create a building within this account.
	 *
	 * @param name Building name
	 * @param street Street address
	 * @param zip Postal code
	 * @param city City name
	 * @param init Lambda to configure the building
	 */
	fun building(
		name: String,
		street: String,
		zip: String,
		city: String,
		init: BuildingContext.() -> Unit = {},
	) {
		buildings += BuildingContext(name, street, zip, city).apply(init)
	}

}

@TenantDslMarker
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
