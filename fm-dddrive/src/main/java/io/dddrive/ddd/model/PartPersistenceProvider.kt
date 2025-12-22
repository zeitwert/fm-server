package io.dddrive.ddd.model

/**
 * This is the base interface for part persistence, for those storage strategies that need them.
 *
 * This allows registering and retrieval from the RepositoryDirectory.
 */
interface PartPersistenceProvider<P : Part<*>>
