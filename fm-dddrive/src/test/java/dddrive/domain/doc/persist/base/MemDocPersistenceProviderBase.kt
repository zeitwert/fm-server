package dddrive.domain.doc.persist.base

import dddrive.app.doc.model.Doc
import dddrive.domain.ddd.persist.map.base.MemAggregatePersistenceProviderBase

/**
 * Base class for map-based Doc persistence providers.
 *
 * Adds docTypeId to the serialized map for foreign key lookups.
 */
abstract class MemDocPersistenceProviderBase<D : Doc>(
	intfClass: Class<D>,
) : MemAggregatePersistenceProviderBase<D>(intfClass)
