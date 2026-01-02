package io.zeitwert.fm.server.config.crnk

import io.crnk.core.queryspec.internal.DefaultQueryPathResolver

class DefaultQueryPathResolver : DefaultQueryPathResolver() {
	init {
		// This is done because filter does not respect @JsonProperty annotation
		this.mapJsonNames = false
	}
}
