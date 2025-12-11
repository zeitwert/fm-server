package io.zeitwert.dddrive.ddd.api.rest.dto

import java.time.OffsetDateTime

interface AggregateMetaDto {
	// Meta from Server
	fun getItemType(): EnumeratedDto?

	fun getOwner(): EnumeratedDto?

	fun getVersion(): Int?

	fun getCreatedByUser(): EnumeratedDto?

	fun getCreatedAt(): OffsetDateTime?

	fun getModifiedByUser(): EnumeratedDto?

	fun getModifiedAt(): OffsetDateTime?

	fun getValidations(): List<AggregatePartValidationDto>?

	// Meta from Client
	fun getClientVersion(): Int?

	fun getOperations(): List<String>?

	fun hasOperation(operation: String): Boolean {
		val operations = getOperations()
		return operations != null && operations.contains(operation)
	}
}
