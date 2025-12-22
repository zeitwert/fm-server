package io.zeitwert.dddrive.config

import org.springframework.context.annotation.Configuration

/**
 * Spring configuration for the jOOQ persistence layer supporting the new dddrive framework.
 *
 * This configuration enables jOOQ-based persistence for aggregates using the new dddrive
 * (io.dddrive.*) framework. It coexists with the existing persistence infrastructure (which
 * uses the old io.dddrive.* packages) to allow gradual migration.
 *
 * The persistence providers themselves are registered as Spring components via
 * @Component annotations on the concrete repository implementations.
 *
 * Key components:
 * - JooqAggregatePersistenceProviderBase: Base class for all persistence providers
 * - JooqObjPersistenceProviderBase: For Obj aggregates
 * - JooqDocPersistenceProviderBase: For Doc aggregates
 *
 * Migration strategy:
 * - New domain entities extending FMObjCoreBase/FMDocCoreBase will use the new persistence
 * - Old entities continue using existing persistence until migrated
 */
@Configuration
open class JooqPersistenceConfig {
  // Configuration beans will be added here as needed during domain migration
  // For now, this serves as a marker configuration that enables component scanning
  // for the persist package
}
