package io.zeitwert.config

import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

/**
 * ApplicationRunner that executes data setup after the Spring context is fully initialized.
 *
 * This runs after Flyway migrations complete and all beans are created, which means repositories
 * and their jOOQ DSLContext dependencies are available. This solves the circular dependency issue
 * that occurred when trying to run data setup during Flyway migration.
 *
 * The ApplicationRunner approach is deterministic for tests because:
 * - Spring Boot waits for all ApplicationRunner beans to complete before the app is "started"
 * - Spring Boot's test framework (@SpringBootTest) waits for full startup
 * - Tests won't start until data setup is complete
 */
@Component
class DataSetupRunner(
	private val dataSetupsProvider: ObjectProvider<DataSetup>,
) : ApplicationRunner {

	override fun run(args: ApplicationArguments) {
		val dataSetups = dataSetupsProvider.orderedStream().toList()
		if (dataSetups.isEmpty()) {
			return
		}

		val dataSetup = dataSetups.first()
		println("\n=== ${dataSetup.name} DATA SETUP ===")

		DelegatingSessionContext.enterSetupMode()
		try {
			dataSetup.setup()
		} finally {
			DelegatingSessionContext.clearSetupContext()
			DelegatingSessionContext.exitSetupMode()
		}

		println("=== ${dataSetup.name} DATA SETUP: Complete ===\n")
	}
}

