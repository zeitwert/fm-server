package io.zeitwert.fm.server.config.crnk;

/**
 * Custom QuerySpecUrlMapper that extends crnk's default implementation.
 * Allows for custom filter operators and path resolution.
 * 
 * Configured to not map JSON names in filters (respects field names instead of @JsonProperty).
 */
public class DefaultQuerySpecUrlMapper extends io.crnk.core.queryspec.mapper.DefaultQuerySpecUrlMapper {

	public DefaultQuerySpecUrlMapper() {
		super();
		// Configure to not map JSON names - filters will use field names
		this.setMapJsonNames(false);
	}
}
