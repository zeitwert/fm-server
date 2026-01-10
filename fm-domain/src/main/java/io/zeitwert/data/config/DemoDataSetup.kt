package io.zeitwert.data.config

import dddrive.ddd.model.RepositoryDirectory
import io.zeitwert.data.DataSetup
import io.zeitwert.data.DelegatingSessionContext
import io.zeitwert.data.dsl.Account
import io.zeitwert.data.dsl.AccountContext
import io.zeitwert.data.dsl.Building
import io.zeitwert.data.dsl.Tenant
import io.zeitwert.fm.building.service.api.BuildingService
import io.zeitwert.fm.oe.model.ObjTenant
import io.zeitwert.fm.oe.model.ObjTenantRepository
import io.zeitwert.fm.oe.model.ObjUser
import io.zeitwert.fm.oe.model.ObjUserRepository
import org.jooq.DSLContext
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import java.time.LocalDate
import kotlin.random.Random

/** Demo data setup - provides demo tenant, users, accounts, and contacts via repositories. */
@Lazy
@Component
@ConditionalOnProperty(name = ["zeitwert.install_demo_data"], havingValue = "true")
class DemoDataSetup(
	val directory: RepositoryDirectory,
	val dslContext: DSLContext,
	val buildingService: BuildingService,
) : DataSetup {

	companion object {

		const val DEMO_TENANT_KEY = "demo"
		const val DEMO_PASSWORD = "demo"
	}

	override val name = "DEMO"

	val userTemplates =
		listOf(
			Triple("admin", "Admin", "admin"),
			Triple("hannes", "Hannes Brunner", "super_user"),
			Triple("martin", "Martin Frey", "super_user"),
		)

	private fun getUsers(domain: String): List<Triple<String, String, String>> = userTemplates.map { (mail, name, role) -> Triple("$mail@$domain", name, role) }

	override fun setup() {
		println("\nDEMO DATA SETUP")
		println("  Setting up demo tenant and users...")

		Tenant.init(dslContext, directory)
		Account.init(dslContext, directory)
		Building.init(dslContext, directory, buildingService)

		// Upload kernel tenant logo if empty
		setupKernelTenantLogo()

		Tenant(DEMO_TENANT_KEY, "Demo", "advisor", "demo/tenant/logo-demo.png") {
			val allUsers = getUsers("zeitwert.io")
			val adminUser = allUsers[0]
			val stdUsers = allUsers.subList(1, allUsers.size)
			adminUser(adminUser.first, adminUser.second, adminUser.third, DEMO_PASSWORD) {
				stdUsers.forEach { (email, name, role) -> user(email, name, role, DEMO_PASSWORD) }
				println("  Setting up demo accounts, contacts, and buildings...")
				account("3032", "Hinterkappelen 3032", "client", "demo/account/logo-3032.jpg") {
					genRandomContacts()
					buildings3032()
				}
				account("3033", "Wohlen 3033", "client", "demo/account/logo-3033.jpg") {
					genRandomContacts()
					buildings3033()
				}
				account("3034", "Murzelen 3034", "client", "demo/account/logo-3034.jpg") {
					genRandomContacts()
					buildings3034()
				}
				account("3043", "Uettligen 3043", "client", "demo/account/logo-3043.jpg") {
					genRandomContacts()
					buildings3043()
				}
				account("8556", "Wigoltingen 8556", "client", "demo/account/logo-8556.jpg") {
					genRandomContacts()
					buildings8556()
				}
				println("  Demo data setup complete.\n")
			}
		}.also { (tenantId, userId) -> attachRandomContactsToAllBuildings(tenantId, userId) }

		Tenant("8253", "Diessenhofen", "community", "demo/tenant/logo-8253.jpg") {
			val allUsers = getUsers("diessenhofen.ch")
			val adminUser = allUsers[0]
			val stdUsers = allUsers.subList(1, allUsers.size)
			adminUser(adminUser.first, adminUser.second, adminUser.third, DEMO_PASSWORD) {
				stdUsers.forEach { (email, fullName, role) -> user(email, fullName, role, DEMO_PASSWORD) }
				println("  Setting up demo accounts, contacts, and buildings...")
				account("8253", "Diessenhofen 8253", "client", "demo/account/logo-8253.jpg") {
					genRandomContacts()
					buildings8253()
				}
				println("  Demo data setup complete.\n")
			}
		}.also { (tenantId, userId) -> attachRandomContactsToAllBuildings(tenantId, userId) }

		Tenant("8266", "Steckborn", "community", "demo/tenant/logo-8266.jpg") {
			val allUsers = getUsers("steckborn.ch")
			val adminUser = allUsers[0]
			val stdUsers = allUsers.subList(1, allUsers.size)
			adminUser(adminUser.first, adminUser.second, adminUser.third, DEMO_PASSWORD) {
				stdUsers.forEach { (email, fullName, role) -> user(email, fullName, role, DEMO_PASSWORD) }
				println("  Setting up demo accounts, contacts, and buildings...")
				account("8266", "Steckborn 8266", "client", "demo/account/logo-8266.jpg") {
					genRandomContacts()
					buildings8266()
				}
				println("  Demo data setup complete.\n")
			}
		}.also { (tenantId, userId) -> attachRandomContactsToAllBuildings(tenantId, userId) }

		Tenant("8476", "Unterstammheim", "community", "demo/tenant/logo-8476.jpg") {
			val allUsers = getUsers("unterstammheim.ch")
			val adminUser = allUsers[0]
			val stdUsers = allUsers.subList(1, allUsers.size)
			adminUser(adminUser.first, adminUser.second, adminUser.third, DEMO_PASSWORD) {
				stdUsers.forEach { (email, fullName, role) -> user(email, fullName, role, DEMO_PASSWORD) }
				println("  Setting up demo accounts, contacts, and buildings...")
				account("8476", "Unterstammheim 8476", "client", "demo/account/logo-8476.jpg") {
					genRandomContacts()
					buildings8476()
				}
				println("  Demo data setup complete.\n")
			}
		}.also { (tenantId, userId) -> attachRandomContactsToAllBuildings(tenantId, userId) }
	}

	/** Check if the kernel tenant's logo document is empty and upload the logo if needed. */
	private fun setupKernelTenantLogo() {
		val tenantRepository = directory.getRepository(ObjTenant::class.java) as ObjTenantRepository
		val userRepository = directory.getRepository(ObjUser::class.java) as ObjUserRepository

		// Get the kernel tenant (id=1) - use get() for read-only access
		val kernelTenant = tenantRepository.get(ObjTenantRepository.KERNEL_TENANT_ID)
		val logoImageId = kernelTenant.logoImageId

		if (logoImageId == null) {
			println("    Kernel tenant has no logo document, skipping logo upload")
			return
		}

		// Check if logo document already has content by loading it
		val logoImage = Tenant.documentRepository.load(logoImageId)
		if (logoImage.content != null && logoImage.content!!.isNotEmpty()) {
			println("    Kernel tenant logo already has content, skipping")
			return
		}

		// Get kernel user for the upload
		val kernelUser = userRepository.getByEmail("k@zeitwert.io")
		if (kernelUser.isEmpty) {
			println("    Warning: Kernel user not found, skipping kernel logo upload")
			return
		}

		// Upload the kernel logo (uploadLogoFromResource will load the document for writing)
		Tenant.uploadLogoFromResource(logoImageId, "demo/tenant/logo-kernel.jpg", kernelUser.get().id!!)
		println("    Uploaded kernel tenant logo")
	}

	/**
	 * Generate a random number (3-6) of contacts for this account. Ensures no duplicate first name +
	 * last name combinations.
	 */
	fun AccountContext.genRandomContacts() {
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
			val email = "${firstName.lowercase()}.${lastName.lowercase()}@example.ch"
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

	/**
	 * Post-processing: attach random contacts to all buildings across all tenants. Each building gets
	 * 1 to N contacts (where N = total contacts on its account).
	 */
	private fun attachRandomContactsToAllBuildings(
		tenantId: Any,
		userId: Any,
	) {
		println("  Attaching random contacts to buildings of tenant $tenantId (user: $userId)")

		// Set tenant context for security
		DelegatingSessionContext.setTenantId(tenantId as Int)
		DelegatingSessionContext.setUserId(userId as Int)

		// Get all accounts for this tenant
		val accountIds = Account.accountRepository.find(null)
		println("tenant($tenantId) accounts: $accountIds")
		check(accountIds.isNotEmpty()) { "Tenant $tenantId has no accounts to process." }

		for (accountId in accountIds) {
			DelegatingSessionContext.setAccountId(accountId as Int)
			// Query all contacts for this account
			val contactIds = Account.contactRepository.find(null)
			println("account($accountId) contacts: $contactIds")
			check(contactIds.isNotEmpty()) {
				"Account $accountId has no contacts to attach to buildings."
			}

			// Query all buildings for this account
			val buildingIds = Building.buildingRepository.find(null)
			println("account($accountId) buildings: $buildingIds")
			check(buildingIds.isNotEmpty()) {
				"Account $accountId has no buildings to attach contacts to."
			}

			// Attach random contacts to each building
			for (buildingId in buildingIds) {
				val building = Building.buildingRepository.load(buildingId)
				val numContacts = (1..contactIds.size).random()
				val selectedContacts = contactIds.shuffled().take(numContacts)

				for (contactId in selectedContacts) {
					println("attaching contact $contactId to building $buildingId")
					building.contactSet.add(contactId as Int)
				}

				dslContext.transaction { _ -> Building.buildingRepository.store(building) }
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
