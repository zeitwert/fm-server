package io.zeitwert.fm.portfolio.service.api;

import io.zeitwert.fm.portfolio.model.ObjPortfolio;

import java.io.ByteArrayOutputStream;

public interface DocumentGenerationService {

	/**
	 * Generate evaluation report for given building into provided byte stream
	 * 
	 * @param building the building
	 * @param stream   the byte stream
	 * @param format   the SaveFormat format (e.g. SaveFormat.PDF, SaveFormat.DOCX)
	 */
	void generateEvaluationReport(ObjPortfolio portfolio, ByteArrayOutputStream stream, int format);

}
