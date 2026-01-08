package io.zeitwert.config.data

import dddrive.ddd.core.model.RepositoryDirectory
import io.zeitwert.config.DataSetup
import io.zeitwert.config.dsl.Account
import io.zeitwert.config.dsl.AccountContext
import io.zeitwert.config.dsl.Building
import io.zeitwert.config.dsl.Tenant
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
) : DataSetup {

	companion object {

		const val DEMO_TENANT_KEY = "demo"
		const val DEMO_ADMIN_EMAIL = "admin@zeitwert.io"
	}

	override val name = "DEMO"

	override fun setup() {
		println("\nDEMO DATA SETUP")
		println("  Setting up demo tenant and users...")

		Tenant.init(dslContext, directory)
		Account.init(dslContext, directory)
		Building.init(dslContext, directory)

		Tenant(DEMO_TENANT_KEY, "Demo", "advisor") {
			adminUser(DEMO_ADMIN_EMAIL, "Admin", "admin", "demo") {
				user("hannes@zeitwert.io", "Hannes Brunner", "super_user", "demo")
				user("martin@zeitwert.io", "Martin Frey", "super_user", "demo")
				user("robert@zeitwert.io", "Robert Reader", "read_only", "demo")
				println("  Setting up demo accounts, contacts, and buildings...")
				account("3032", "Hinterkappelen 3032", "client") {
					randomContacts(2)
					buildings3032()
				}
				account("3033", "Wohlen 3033", "client") {
					randomContacts(2)
					buildings3033()
				}
				account("3034", "Murzelen 3034", "client") {
					randomContacts(2)
					buildings3034()
				}
				account("3043", "Uettligen 3043", "client") {
					randomContacts(2)
					buildings3043()
				}
				account("8556", "Wigoltingen 8556", "client") {
					randomContacts(2)
					buildings8556()
				}
				println("  Demo data setup complete.\n")
			}
		}

		Tenant("DH", "Diessenhofen", "community") {
			adminUser("", "Admin", "admin", "demo") {
				user("hannes@diessenhofen.ch", "Hannes Brunner", "super_user", "demo")
				user("martin@diessenhofen.ch", "Martin Frey", "super_user", "demo")
				println("  Setting up demo accounts, contacts, and buildings...")
				account("8253", "Diessenhofen 8253", "client") {
					randomContacts(2)
					buildings8253()
				}
				println("  Demo data setup complete.\n")
			}
		}

		Tenant("SB", "Steckborn", "community") {
			adminUser("", "Admin", "admin", "demo") {
				user("hannes@steckborn.ch", "Hannes Brunner", "super_user", "demo")
				user("martin@steckborn.ch", "Martin Frey", "super_user", "demo")
				println("  Setting up demo accounts, contacts, and buildings...")
				account("8266", "Steckborn 8266", "client") {
					randomContacts(3)
					buildings8266()
				}
				println("  Demo data setup complete.\n")
			}
		}

		Tenant("US", "Unterstammheim", "community") {
			adminUser("", "Admin", "admin", "demo") {
				user("hannes@unterstammheim.ch", "Hannes Brunner", "super_user", "demo")
				user("martin@unterstammheim.ch", "Martin Frey", "super_user", "demo")
				println("  Setting up demo accounts, contacts, and buildings...")
				account("8476", "Unterstammheim 8476", "client") {
					randomContacts(2)
					buildings8476()
				}
				println("  Demo data setup complete.\n")
			}
		}

	}

	/**
	 * Generate random contacts for this account.
	 *
	 * @param count Number of contacts to generate
	 */
	fun AccountContext.randomContacts(count: Int) {
		repeat(count) { index ->
			val (firstName, lastName, salutation) = randomName()
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
