package io.zeitwert.fm.building.service.api;

import io.zeitwert.fm.building.model.ObjBuilding;

import java.io.ByteArrayOutputStream;

public interface DocumentGenerationService {

	/**
	 * Generate evaluation report for given building into provided byte stream
	 * 
	 * @param building the building
	 * @param stream   the byte stream
	 * @param format   the format (e.g. SaveFormat.PDF, SaveFormat.DOCX)
	 */
	void generateEvaluationReport(ObjBuilding building, ByteArrayOutputStream stream, int format);

}
