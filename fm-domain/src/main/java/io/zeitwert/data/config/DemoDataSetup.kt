package io.zeitwert.data.config

import dddrive.ddd.model.RepositoryDirectory
import io.zeitwert.app.session.model.KernelContext
import io.zeitwert.config.session.TestSessionContext
import io.zeitwert.data.DataSetup
import io.zeitwert.data.dsl.Account
import io.zeitwert.data.dsl.AccountContext
import io.zeitwert.data.dsl.Building
import io.zeitwert.data.dsl.DslUtil
import io.zeitwert.data.dsl.Note
import io.zeitwert.data.dsl.Task
import io.zeitwert.data.dsl.Tenant
import io.zeitwert.fm.building.api.BuildingService
import io.zeitwert.fm.oe.model.ObjTenant
import io.zeitwert.fm.oe.model.ObjTenantRepository
import io.zeitwert.fm.oe.model.ObjUser
import io.zeitwert.fm.oe.model.ObjUserRepository
import io.zeitwert.fm.oe.model.enums.CodeTenantType
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.OffsetDateTime
import kotlin.random.Random

/** Demo data setup - provides demo tenant, users, accounts, and contacts via repositories. */
@Lazy
@Component
@ConditionalOnProperty(name = ["zeitwert.install_demo_data"], havingValue = "true")
class DemoDataSetup(
	val directory: RepositoryDirectory,
	val buildingService: BuildingService,
	val kernelContext: KernelContext,
) : DataSetup {

	companion object {

		const val DEMO_TENANT_KEY = "demo"
		const val DEMO_TENANT_NAME = "Demo"
		const val DEMO_TENANT_LOGO = "demo/tenant/logo-demo.png"

		const val DEMO_PASSWORD = "demo"

	}

	override val name = "DEMO"

	val userTemplates =
		listOf(
			Triple("admin", "Admin", "admin"),
			Triple("hannes", "Hannes Brunner", "super_user"),
			Triple("martin", "Martin Frey", "super_user"),
		)

	private fun getUsersInDomain(domain: String): List<Triple<String, String, String>> =
		userTemplates.map { (mail, name, role) ->
			Triple("$mail@$domain", name, role)
		}

	override fun setup() {
		DslUtil.init(directory)
		Tenant.init(directory)
		Account.init(directory)
		Building.init(directory, buildingService)
		Note.init(directory)
		Task.init(directory)

		// Upload kernel tenant logo if empty
		setupKernelTenantLogo()

		val allTenantIds = mutableListOf<Int>()

		DslUtil.startIndent()
		Tenant(DEMO_TENANT_KEY, DEMO_TENANT_NAME, CodeTenantType.ADVISOR.id, DEMO_TENANT_LOGO) {
			val allUsers = getUsersInDomain("zeitwert.io")
			val adminUser = allUsers[0]
			val stdUsers = allUsers.subList(1, allUsers.size)
			adminUser(adminUser.first, adminUser.second, adminUser.third, DEMO_PASSWORD) {
				stdUsers.forEach { (email, name, role) -> user(email, name, role, DEMO_PASSWORD) }
				account("3032", "Hinterkappelen 3032", "client", "demo/account/logo-3032.jpg") {
					genRandomContacts("hinterkappelen.ch")
					buildings3032()
				}
				account("3033", "Wohlen 3033", "client", "demo/account/logo-3033.jpg") {
					genRandomContacts("wohlen.ch")
					buildings3033()
				}
				account("3034", "Murzelen 3034", "client", "demo/account/logo-3034.jpg") {
					genRandomContacts("murzelen.ch")
					buildings3034()
				}
				account("3043", "Uettligen 3043", "client", "demo/account/logo-3043.jpg") {
					genRandomContacts("uettligen.ch")
					buildings3043()
				}
				account("8556", "Wigoltingen 8556", "client", "demo/account/logo-8556.jpg") {
					genRandomContacts("wigoltingen.ch")
					buildings8556()
				}
			}
		}.also { (tenantId, userId) ->
			allTenantIds.add(tenantId)
			attachRandomContactsToAllBuildings(tenantId, userId)
			attachRandomNotesToAllEntities(tenantId, userId)
			attachRandomTasksToAllEntities(tenantId, userId)
		}

		Tenant("8253", "Diessenhofen", "community", "demo/tenant/logo-8253.jpg") {
			val allUsers = getUsersInDomain("diessenhofen.ch")
			val adminUser = allUsers[0]
			val stdUsers = allUsers.subList(1, allUsers.size)
			adminUser(adminUser.first, adminUser.second, adminUser.third, DEMO_PASSWORD) {
				stdUsers.forEach { (email, fullName, role) -> user(email, fullName, role, DEMO_PASSWORD) }
				account("8253", "Diessenhofen 8253", "client", "demo/account/logo-8253.jpg") {
					genRandomContacts("diessenhofen.ch")
					buildings8253()
				}
			}
		}.also { (tenantId, userId) ->
			allTenantIds.add(tenantId)
			attachRandomContactsToAllBuildings(tenantId, userId)
			attachRandomNotesToAllEntities(tenantId, userId)
			attachRandomTasksToAllEntities(tenantId, userId)
		}

		Tenant("8266", "Steckborn", "community", "demo/tenant/logo-8266.jpg") {
			val allUsers = getUsersInDomain("steckborn.ch")
			val adminUser = allUsers[0]
			val stdUsers = allUsers.subList(1, allUsers.size)
			adminUser(adminUser.first, adminUser.second, adminUser.third, DEMO_PASSWORD) {
				stdUsers.forEach { (email, fullName, role) -> user(email, fullName, role, DEMO_PASSWORD) }
				account("8266", "Steckborn 8266", "client", "demo/account/logo-8266.jpg") {
					genRandomContacts("steckborn.ch")
					buildings8266()
				}
			}
		}.also { (tenantId, userId) ->
			allTenantIds.add(tenantId)
			attachRandomContactsToAllBuildings(tenantId, userId)
			attachRandomNotesToAllEntities(tenantId, userId)
			attachRandomTasksToAllEntities(tenantId, userId)
		}

		Tenant("8476", "Unterstammheim", "community", "demo/tenant/logo-8476.jpg") {
			val allUsers = getUsersInDomain("unterstammheim.ch")
			val adminUser = allUsers[0]
			val stdUsers = allUsers.subList(1, allUsers.size)
			adminUser(adminUser.first, adminUser.second, adminUser.third, DEMO_PASSWORD) {
				stdUsers.forEach { (email, fullName, role) -> user(email, fullName, role, DEMO_PASSWORD) }
				account("8476", "Unterstammheim 8476", "client", "demo/account/logo-8476.jpg") {
					genRandomContacts("unterstammheim.ch")
					buildings8476()
				}
			}
		}.also { (tenantId, userId) ->
			allTenantIds.add(tenantId)
			attachRandomContactsToAllBuildings(tenantId, userId)
			attachRandomNotesToAllEntities(tenantId, userId)
			attachRandomTasksToAllEntities(tenantId, userId)
		}

		// Add all tenants to zeitwert.io users so they can access all tenants
		addTenantsToZeitwertUsers(allTenantIds)
	}

	/** Check if the kernel tenant's logo document is empty and upload the logo if needed. */
	private fun setupKernelTenantLogo() {
		val tenantRepository = directory.getRepository(ObjTenant::class.java) as ObjTenantRepository
		val userRepository = directory.getRepository(ObjUser::class.java) as ObjUserRepository
		// Get the kernel tenant - use get() for read-only access
		val kernelTenant = tenantRepository.get(kernelContext.kernelTenantId)
		val logoImageId = kernelTenant.logoImageId
		if (logoImageId == null) {
			DslUtil.logger.info("${DslUtil.indent}Kernel tenant has no logo document, skipping logo upload")
			return
		}
		// Check if logo document already has content by loading it
		val logoImage = Tenant.documentRepository.load(logoImageId)
		if (logoImage.content != null && logoImage.content!!.isNotEmpty()) {
			DslUtil.logger.debug("${DslUtil.indent}Kernel tenant logo already has content, skipping")
			return
		}
		// Get kernel user for the upload
		val kernelUser = userRepository.getByEmail(ObjUserRepository.KERNEL_USER_EMAIL)
		if (kernelUser.isEmpty) {
			DslUtil.logger.warn("${DslUtil.indent}Kernel user not found, skipping kernel logo upload")
			return
		}
		// Upload the kernel logo (uploadLogoFromResource will load the document for writing)
		DslUtil.uploadLogoFromResource(logoImageId, "demo/tenant/logo-kernel.jpg", kernelUser.get().id)
		DslUtil.logger.debug("${DslUtil.indent}Uploaded kernel tenant logo")
	}

	/**
	 * Generate a random number (3-6) of contacts for this account. Ensures no duplicate first name +
	 * last name combinations.
	 */
	fun AccountContext.genRandomContacts(domain: String) {
		val count = (3..6).random()
		val usedNames = mutableSetOf<Pair<String, String>>()
		repeat(count) {
			var firstName: String
			var lastName: String
			var salutation: String
			do {
				val name = randomName()
				firstName = name.first
				lastName = name.second
				salutation = name.third
			} while (!usedNames.add(firstName to lastName))
			val email = "${firstName.lowercase()}.${lastName.lowercase()}@$domain"
			val role = randomContactRole()
			val phone = randomPhone()
			val mobile = randomMobile()
			val birthDate = randomBirthDate()
			contact(firstName, lastName, email, role) {
				this.salutation = salutation
				this.phone = phone
				this.mobile = mobile
				this.birthDate = birthDate
			}
		}
	}

	/** Add all tenants to the tenant sets of the zeitwert.io users. */
	private fun addTenantsToZeitwertUsers(tenantIds: List<Int>) {
		val userRepository = directory.getRepository(ObjUser::class.java) as ObjUserRepository
		val zeitwertEmails = getUsersInDomain("zeitwert.io").map { it.first }

		for (email in zeitwertEmails) {
			val userOpt = userRepository.getByEmail(email)
			if (userOpt.isEmpty) continue

			val user = userRepository.load(userOpt.get().id)
			for (tenantId in tenantIds) {
				user.tenantSet.add(tenantId)
			}
			userRepository.transaction {
				userRepository.store(user)
			}
			DslUtil.logger.info("${DslUtil.indent}Added ${tenantIds.size} tenants to user $email")
		}
	}

	/**
	 * Post-processing: attach random contacts to all buildings across all tenants. Each building gets
	 * 1 to N contacts (where N = total contacts on its account).
	 */
	private fun attachRandomContactsToAllBuildings(
		tenantId: Any,
		userId: Any,
	) {
		DslUtil.logger.info(
			"{}Attaching random contacts to buildings of tenant {} (user: {})",
			DslUtil.indent,
			tenantId,
			userId,
		)

		// Set tenant context for security
		TestSessionContext.overrideTenantId(tenantId as Int)
		TestSessionContext.overrideUserId(userId as Int)

		// Get all accounts for this tenant
		val accountIds = Account.accountRepository.find(null)
		check(accountIds.isNotEmpty()) { "Tenant $tenantId has no accounts to process." }

		for (accountId in accountIds) {
			TestSessionContext.overrideAccountId(accountId as Int)
			// Query all contacts for this account
			val contactIds = Account.contactRepository.find(null)
			check(contactIds.isNotEmpty()) {
				"Account $accountId has no contacts to attach to buildings."
			}

			// Query all buildings for this account
			val buildingIds = Building.buildingRepository.find(null)
			check(buildingIds.isNotEmpty()) {
				"Account $accountId has no buildings to attach contacts to."
			}

			// Attach random contacts to each building
			for (buildingId in buildingIds) {
				val building = Building.buildingRepository.load(buildingId)
				val numContacts = (1..contactIds.size).random()
				val selectedContacts = contactIds.shuffled().take(numContacts)

				for (contactId in selectedContacts) {
					building.contactSet.add(contactId as Int)
				}

				Building.buildingRepository.transaction {
					Building.buildingRepository.store(building)
				}
			}
		}
	}

	/**
	 * Post-processing: attach random notes (1-5) to all buildings, accounts, and contacts.
	 * Notes are standalone aggregates with their own backpointer, so we only need to store the notes.
	 */
	private fun attachRandomNotesToAllEntities(
		tenantId: Any,
		userId: Any,
	) {
		DslUtil.logger.info(
			"{}Attaching random notes to entities of tenant {} (user: {})",
			DslUtil.indent,
			tenantId,
			userId,
		)

		// Set tenant context for security
		TestSessionContext.overrideTenantId(tenantId as Int)
		TestSessionContext.overrideUserId(userId as Int)

		// Get all accounts for this tenant
		val accountIds = Account.accountRepository.find(null)
		if (accountIds.isEmpty()) {
			DslUtil.logger.warn("{}Tenant {} has no accounts to process for notes.", DslUtil.indent, tenantId)
			return
		}

		for (accountId in accountIds) {
			TestSessionContext.overrideAccountId(accountId as Int)

			// Attach notes to the account
			val account = Account.accountRepository.get(accountId)
			Note.attachRandomNotes(account, userId)

			// Attach notes to all contacts in this account
			val contactIds = Account.contactRepository.find(null)
			for (contactId in contactIds) {
				val contact = Account.contactRepository.get(contactId)
				Note.attachRandomNotes(contact, userId)
			}

			// Attach notes to all buildings in this account
			val buildingIds = Building.buildingRepository.find(null)
			for (buildingId in buildingIds) {
				val building = Building.buildingRepository.get(buildingId)
				Note.attachRandomNotes(building, userId)
			}
		}
	}

	/**
	 * Post-processing: attach random tasks (1-5) to all buildings, accounts, and contacts.
	 * Tasks are standalone aggregates with their own backpointer, so we only need to store the tasks.
	 */
	private fun attachRandomTasksToAllEntities(
		tenantId: Any,
		userId: Any,
	) {
		DslUtil.logger.info(
			"{}Attaching random tasks to entities of tenant {} (user: {})",
			DslUtil.indent,
			tenantId,
			userId,
		)

		// Set tenant context for security
		TestSessionContext.overrideTenantId(tenantId as Int)
		TestSessionContext.overrideUserId(userId as Int)

		val timestamp = OffsetDateTime.now()

		// Get all accounts for this tenant
		val accountIds = Account.accountRepository.find(null)
		if (accountIds.isEmpty()) {
			DslUtil.logger.warn("{}Tenant {} has no accounts to process for tasks.", DslUtil.indent, tenantId)
			return
		}

		for (accountId in accountIds) {
			TestSessionContext.overrideAccountId(accountId as Int)

			// Attach tasks to the account
			val account = Account.accountRepository.get(accountId)
			Task.attachRandomTasks(account, userId, timestamp)

			// Attach tasks to all buildings in this account
			val buildingIds = Building.buildingRepository.find(null)
			for (buildingId in buildingIds) {
				val building = Building.buildingRepository.get(buildingId)
				Task.attachRandomTasks(building, userId, timestamp)
			}
		}
	}

	private fun randomName(): Triple<String, String, String> {
		val maleFirstNames =
			listOf(
				"Max",
				"Peter",
				"Hans",
				"Thomas",
				"Stefan",
				"Michael",
				"Andreas",
				"Daniel",
				"Martin",
				"Christian",
			)
		val femaleFirstNames =
			listOf(
				"Anna",
				"Maria",
				"Sandra",
				"Nicole",
				"Claudia",
				"Monika",
				"Barbara",
				"Susanne",
				"Christine",
				"Petra",
			)
		val lastNames =
			listOf(
				"Müller",
				"Meier",
				"Schmid",
				"Keller",
				"Weber",
				"Huber",
				"Schneider",
				"Meyer",
				"Steiner",
				"Fischer",
			)

		val isMale = Random.nextBoolean()
		val firstName = if (isMale) maleFirstNames.random() else femaleFirstNames.random()
		val lastName = lastNames.random()
		val salutation = if (isMale) "mr" else "mrs"

		return Triple(firstName, lastName, salutation)
	}

	private fun randomContactRole(): String {
		val roles = listOf("councilor", "caretaker", "other")
		return roles.random()
	}

	private fun randomPhone(): String {
		val areaCode = listOf("044", "043", "031", "061", "041", "052").random()
		val number = (1000000..9999999).random()
		return "+41 ${areaCode.drop(1)} ${number.toString().chunked(3).joinToString(" ")}"
	}

	private fun randomMobile(): String {
		val prefix = listOf("076", "077", "078", "079").random()
		val number = (1000000..9999999).random()
		return "+41 ${prefix.drop(1)} ${number.toString().chunked(3).joinToString(" ")}"
	}

	private fun randomBirthDate(): LocalDate {
		val year = (1960..2000).random()
		val month = (1..12).random()
		val day = (1..28).random()
		return LocalDate.of(year, month, day)
	}

}
