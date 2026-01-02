package io.zeitwert.fm.server.config.crnk

import io.crnk.core.engine.document.ErrorData
import io.crnk.core.engine.error.ErrorResponse
import io.crnk.core.engine.error.ExceptionMapper
import io.crnk.core.engine.http.HttpStatus
import io.crnk.core.engine.internal.utils.PropertyException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class PropertyExceptionMapper : ExceptionMapper<PropertyException> {

	private val log = LoggerFactory.getLogger(PropertyExceptionMapper::class.java)

	override fun toErrorResponse(exception: PropertyException): ErrorResponse {
		val resourceClass = exception.resourceClass?.simpleName ?: "unknown"
		val field = exception.field ?: "unknown"
		val message = "Property error on $resourceClass.$field: ${exception.message}"

		log.error(message, exception)

		val errorData = ErrorData
			.builder()
			.setStatus(HttpStatus.BAD_REQUEST_400.toString())
			.setTitle("Property Error")
			.setDetail(message)
			.build()

		return ErrorResponse
			.builder()
			.setStatus(HttpStatus.BAD_REQUEST_400)
			.setSingleErrorData(errorData)
			.build()
	}

	override fun fromErrorResponse(errorResponse: ErrorResponse): PropertyException? = null

	override fun accepts(errorResponse: ErrorResponse): Boolean = false

}
