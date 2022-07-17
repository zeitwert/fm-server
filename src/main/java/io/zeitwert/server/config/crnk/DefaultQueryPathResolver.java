
package io.zeitwert.server.config.crnk;

public class DefaultQueryPathResolver extends io.crnk.core.queryspec.internal.DefaultQueryPathResolver {

	public DefaultQueryPathResolver() {
		super();
		// This is done because filter does not respect @JsonProperty annotation
		this.setMapJsonNames(false);
	}
}
