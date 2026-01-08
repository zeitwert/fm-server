package io.zeitwert.config.data

import dddrive.ddd.core.model.RepositoryDirectory
import io.zeitwert.config.DataSetup
import io.zeitwert.config.dsl.Account
import io.zeitwert.config.dsl.AccountContext
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

		Tenant(DEMO_TENANT_KEY, "Demo", "advisor") {
			adminUser(DEMO_ADMIN_EMAIL, "Admin", "admin", "demo") {
				user("hannes@zeitwert.io", "Hannes Brunner", "super_user", "demo")
				user("martin@zeitwert.io", "Martin Frey", "super_user", "demo")
				user("urs@zeitwert.io", "Urs Muster", "user", "demo")

				println("  Setting up demo accounts and contacts...")

				// Real account data from R__2903_account_data.sql
				account("8355", "Aadorf 8355", "client") { randomContacts(2) }
				account("8479", "Altikon 8479", "client") { randomContacts(2) }
				account("9220", "Bischofszell 9220", "client") { randomContacts(3) }
				account("5620", "Bremgarten 5620", "client") { randomContacts(2) }
				account("9470", "Buchs SG 9470", "client") { randomContacts(2) }
				account("8253", "Diessenhofen 8253", "client") { randomContacts(2) }
				account("9651", "Ennetbühl 9651", "client") { randomContacts(2) }
				account("9225", "Gotthaus 9225", "client") { randomContacts(2) }
				account("9223", "Halden 9223", "client") { randomContacts(2) }
				account("9213", "Hauptwil 9213", "client") { randomContacts(3) }
				account("8444", "Henggart 8444", "client") { randomContacts(2) }
				account("5626", "Hermetschwil 5626", "client") { randomContacts(2) }
				account("3032", "Hinterkappelen 3032", "client") { randomContacts(2) }
				account("8335", "Hittnau 8335", "client") { randomContacts(2) }
				account("9216", "Hohentannen 9216", "client") { randomContacts(2) }
				account("4950", "Huttwil 4950", "client") { randomContacts(3) }
				account("4303", "Kaiseraugst 4303", "client") { randomContacts(2) }
				account("7304", "Maienfeld 7304", "client") { randomContacts(2) }
				account("5318", "Mandach 5318", "client") { randomContacts(2) }
				account("3034", "Murzelen 3034", "client") { randomContacts(2) }
				account("1111", "Musterstadt 1111", "client") { randomContacts(3) }
				account("8477", "Oberstammheim 8477", "client") { randomContacts(2) }
				account("8558", "Raperswilen 8558", "client") { randomContacts(2) }
				account("9471", "Rheinau SG 9471", "client") { randomContacts(2) }
				account("8153", "Rümlang 8153", "client") { randomContacts(2) }
				account("3049", "Säriswil 3049", "client") { randomContacts(2) }
				account("8255", "Schlattingen 8255", "client") { randomContacts(2) }
				account("5425", "Schneisingen 5425", "client") { randomContacts(2) }
				account("8589", "Sitterdorf 8589", "client") { randomContacts(2) }
				account("8564", "Sonterswil 8564", "client") { randomContacts(2) }
				account("8266", "Steckborn 8266", "client") { randomContacts(3) }
				account("3043", "Uettligen 3043", "client") { randomContacts(2) }
				account("8476", "Unterstammheim 8476", "client") { randomContacts(2) }
				account("8468", "Waltalingen 8468", "client") { randomContacts(2) }
				account("8104", "Weiningen 8104", "client") { randomContacts(2) }
				account("8620", "Wetzikon 8620", "client") { randomContacts(3) }
				account("8556", "Wigoltingen 8556", "client") { randomContacts(2) }
				account("3033", "Wohlen 3033", "client") { randomContacts(2) }
				account("8588", "Zihlschlacht 8588", "client") { randomContacts(2) }
				account("8006", "Zürich 8006", "client") { randomContacts(3) }
				account("8022", "Zürich 8022", "client") { randomContacts(2) }
			}
		}

		println("  Demo data setup complete.\n")
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
			listOf("Max", "Peter", "Hans", "Thomas", "Stefan", "Michael", "Andreas", "Daniel", "Martin", "Christian")
		val femaleFirstNames =
			listOf("Anna", "Maria", "Sandra", "Nicole", "Claudia", "Monika", "Barbara", "Susanne", "Christine", "Petra")
		val lastNames =
			listOf("Müller", "Meier", "Schmid", "Keller", "Weber", "Huber", "Schneider", "Meyer", "Steiner", "Fischer")

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
