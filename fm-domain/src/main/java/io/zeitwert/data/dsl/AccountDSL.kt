package io.zeitwert.data.dsl

import dddrive.ddd.model.RepositoryDirectory
import io.zeitwert.data.DelegatingSessionContext
import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.account.model.enums.CodeAccountType
import io.zeitwert.fm.contact.model.ObjContact
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
 * Account.init(accountRepository, contactRepository, documentRepository)
 * Account("TA", "Testlingen", "client", "demo/account/logo-TA.jpg") {
 *     contact("Max", "Muster", "max.muster@test.ch", "councilor") {
 *         salutation = "mr"
 *         phone = "+41 44 123 45 67"
 *     }
 * }
 * ```
 */
object Account {

	lateinit var directory: RepositoryDirectory

	val accountRepository: ObjAccountRepository
		get() = directory.getRepository(ObjAccount::class.java) as ObjAccountRepository

	val contactRepository: ObjContactRepository
		get() = directory.getRepository(ObjContact::class.java) as ObjContactRepository

	fun init(
		directory: RepositoryDirectory,
	) {
		this.directory = directory
	}

	operator fun invoke(
		userId: Any,
		key: String,
		name: String,
		accountType: String,
		logoPath: String? = null,
		init: AccountContext.() -> Unit = {},
	): Int {
		val context = AccountContext(userId, key, name, accountType, logoPath).apply(init)
		return createOrGetAccount(context)
	}

	private fun createOrGetAccount(ctx: AccountContext): Int {
		// Check if account already exists
		val account = accountRepository.getByKey(ctx.key)

		if (account.isPresent) {
			val account = account.get()
			val accountId = account.id as Int
			println("    Account ${ctx.key} already exists (id=$accountId)")
			// Still create contacts that don't exist
			// set DelegatingSessionContext account ID for nested operations
			// DelegatingSessionContext.setAccountId(accountId)
			// ctx.contacts.forEach { contactCtx -> createContact(account, contactCtx) }
			return accountId
		}

		// Create new account via repository
		val newAccount = accountRepository.create()
		newAccount.key = ctx.key
		newAccount.name = ctx.name
		newAccount.accountType = CodeAccountType.getAccountType(ctx.accountType)

		// Set account ID in session context for nested operations
		DelegatingSessionContext.setAccountId(newAccount.id as Int)

		accountRepository.transaction {
			accountRepository.store(newAccount)
		}
		check(newAccount.logoImageId != null) { "Tenant logoImageId is created in domain logic" }

		val accountId = newAccount.id as Int
		println("    Created account ${ctx.key} - ${ctx.name} (id=$accountId)")

		// Upload account logo from resource if path is provided
		if (ctx.logoPath != null) {
			Tenant.uploadLogoFromResource(newAccount.logoImageId!!, ctx.logoPath, ctx.userId)
		}

		// Create contacts for this account
		ctx.contacts.forEach { contactCtx -> createContact(newAccount, contactCtx) }

		// Create buildings for this account
		ctx.buildings.forEach { buildingCtx ->
			Building(
				ctx.userId,
				newAccount,
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

		contactRepository.transaction {
			contactRepository.store(contact)
		}

		val contactId = contact.id as Int
		println("      Created contact ${ctx.firstName} ${ctx.lastName} (id=$contactId)")

		return contactId
	}

}

@TenantDslMarker
class AccountContext(
	val userId: Any,
	val key: String,
	val name: String,
	val accountType: String,
	val logoPath: String? = null,
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
		buildings += BuildingContext(userId, name, street, zip, city).apply(init)
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
