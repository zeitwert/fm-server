package io.zeitwert.dddrive.ddd.api.rest

enum class DtoDetailLevel {
	REPORT, // for orderbooks or when less detail is sufficient
	FULL, // for single-item API, where full details are necessary
}
