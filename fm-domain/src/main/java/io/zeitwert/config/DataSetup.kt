package io.zeitwert.config

/**
 * Interface for data setup implementations.
 *
 * Implementations provide repository-based data setup via Kotlin DSL after Flyway schema migrations.
 * The repositories are injected via constructor into the implementing classes.
 */
interface DataSetup {

	/** The name of this data setup (e.g., "TEST", "DEMO") */
	val name: String

	/** Called to set up data via repositories using Kotlin DSL. */
	fun setup()

}
