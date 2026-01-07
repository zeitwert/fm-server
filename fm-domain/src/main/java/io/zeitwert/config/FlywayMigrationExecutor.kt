package io.zeitwert.config

import org.flywaydb.core.Flyway
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy
import org.springframework.context.annotation.Lazy
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import javax.sql.DataSource

/**
 * Central Flyway migration executor that handles the multi-phase migration.
 *
 * This is the single FlywayMigrationStrategy that Spring Boot uses. It delegates to registered
 * DataSetup implementations for the repository-based data setup phase.
 *
 * Migration phases:
 * 1. Schema, config, and migration setup
 * 2. Repository-based data setup (delegated to DataSetup implementations)
 * 3. Data migrations (from DataSetup locations)
 * 4. Migration teardown
 *
 * Note: DataSetups are marked @Lazy and injected via @Lazy ObjectProvider. They are only resolved
 * after Phase 1 migration completes, when the database schema exists and repositories can be
 * created.
 */
@Component
class FlywayMigrationExecutor(
	private val dataSource: DataSource,
	@param:Lazy private val dataSetupsProvider: ObjectProvider<DataSetup>,
	private val environment: Environment,
) : FlywayMigrationStrategy {

	override fun migrate(flyway: Flyway) {
		// Check if any data setup is configured via properties
		// We can't resolve the DataSetup beans yet (they're lazy), but we can check the properties
		val hasDataSetup =
			environment.getProperty("zeitwert.install_test_data", Boolean::class.java, false) ||
				environment.getProperty(
					"zeitwert.install_demo_data",
					Boolean::class.java,
					false,
				)

		if (!hasDataSetup) {
			// No data setup configured, just run standard Flyway migration
			println("\n=== STANDARD FLYWAY MIGRATION ===\n")
			flyway.migrate()
			return
		}

		println("\n=== Multi-Phase Flyway Migration ===\n")

		// Clean the database first
		println("Cleaning database...")
		val cleanFlyway =
			Flyway
				.configure()
				.dataSource(dataSource)
				.cleanDisabled(false)
				.schemas("public")
				.load()
		cleanFlyway.clean()

		// Phase 1: Schema + config + migration setup (but NOT teardown)
		println("Phase 1: Running schema, config, and migration setup...")
		val schemaFlyway =
			Flyway
				.configure()
				.dataSource(dataSource)
				.locations(
					"classpath:db/V1.0/1-baseline",
					"classpath:db/V1.0/2-upgrade",
					"classpath:db/V1.0/3-config",
					"classpath:db/V1.0/4-migr/1-setup",
				).load()
		val phase1Result = schemaFlyway.migrate()
		println("Phase 1 complete: ${phase1Result.migrationsExecuted} migrations executed")

		// Now that Phase 1 is complete, we can safely resolve the lazy DataSetup beans
		// The database schema exists, so repositories and persistence providers can be created
		val dataSetups = dataSetupsProvider.orderedStream().toList()
		if (dataSetups.isEmpty()) {
			println("\nNo DataSetup beans found, skipping data setup phase.")
			return
		}

		val dataSetup = dataSetups.first()
		println("\n=== ${dataSetup.name} DATA SETUP ===")

		// Phase 2: Repository-based data setup
		// Enter setup mode so DelegatingSessionContext returns kernel user context
		println("\nPhase DSL: Creating data via repositories...")
		DelegatingSessionContext.enterSetupMode()
		try {
			dataSetup.setup()
		} finally {
			DelegatingSessionContext.exitSetupMode()
		}

		// Phase 3: Data migrations
		println("\nPhase 2: Running data migrations from ${dataSetup.location}...")
		val dataFlyway =
			Flyway
				.configure()
				.dataSource(dataSource)
				.locations(dataSetup.location)
				.ignoreMigrationPatterns("*:*")
				.load()
		val phase2Result = dataFlyway.migrate()
		println("Phase 2 complete: ${phase2Result.migrationsExecuted} migrations executed")

		// Phase 4: Migration teardown
		println("\nPhase 3: Running migration teardown...")
		val teardownFlyway =
			Flyway
				.configure()
				.dataSource(dataSource)
				.locations("classpath:db/V1.0/4-migr/2-teardown")
				.ignoreMigrationPatterns("*:*")
				.load()
		val phase3Result = teardownFlyway.migrate()
		println("Phase 3 complete: ${phase3Result.migrationsExecuted} migrations executed")

		println("\n=== ${dataSetup.name} DATA SETUP: Complete ===\n")
	}

}
