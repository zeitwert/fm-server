package io.zeitwert.config

/**
 * Interface for data setup implementations.
 *
 * Implementations provide repository-based data setup that runs between Flyway schema migrations
 * and data migrations. The repositories are injected via constructor into the implementing classes.
 */
interface DataSetup {

	/** The name of this data setup (e.g., "TEST", "DEMO") */
	val name: String

	/** The Flyway location for data migrations (e.g., "classpath:db/V1.0/5-test") */
	val location: String

	/** Called to set up data via repositories. */
	fun setup()

}
