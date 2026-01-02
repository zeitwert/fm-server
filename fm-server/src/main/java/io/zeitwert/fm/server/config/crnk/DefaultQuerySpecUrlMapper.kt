package io.zeitwert.fm.server.config.crnk

import io.crnk.core.queryspec.mapper.DefaultQuerySpecUrlMapper

/**
 * Custom QuerySpecUrlMapper that extends crnk's default implementation.
 * Allows for custom filter operators and path resolution.
 *
 * Configured to not map JSON names in filters (respects field names instead of @JsonProperty).
 */
class DefaultQuerySpecUrlMapper : DefaultQuerySpecUrlMapper() {

	init {
		// Configure to not map JSON names - filters will use field names
		this.mapJsonNames = false
	}

}
