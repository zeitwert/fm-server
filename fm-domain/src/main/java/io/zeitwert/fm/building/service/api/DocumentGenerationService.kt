package io.zeitwert.fm.building.service.api

import io.zeitwert.fm.building.model.ObjBuilding
import java.io.ByteArrayOutputStream

interface DocumentGenerationService {

	/**
	 * Generate evaluation report for given building into provided byte stream
	 *
	 * @param building the building
	 * @param stream   the byte stream
	 * @param format   the format (e.g. SaveFormat.PDF, SaveFormat.DOCX)
	 */
	fun generateEvaluationReport(building: ObjBuilding, stream: ByteArrayOutputStream, format: Int)

}
