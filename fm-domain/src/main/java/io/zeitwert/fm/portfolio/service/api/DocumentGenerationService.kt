package io.zeitwert.fm.portfolio.service.api

import io.zeitwert.fm.portfolio.model.ObjPortfolio
import java.io.ByteArrayOutputStream

interface DocumentGenerationService {

	/**
	 * Generate evaluation report for given building into provided byte stream
	 *
	 * @param stream   the byte stream
	 * @param format   the SaveFormat format (e.g. SaveFormat.PDF, SaveFormat.DOCX)
	 */
	fun generateEvaluationReport(
		portfolio: ObjPortfolio,
		stream: ByteArrayOutputStream,
		format: Int,
	)

}
