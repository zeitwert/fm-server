
package io.zeitwert.fm.building.adapter.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.building.service.api.DocumentGenerationService;
import io.zeitwert.fm.dms.adapter.api.rest.DocumentContentController;

import java.io.ByteArrayOutputStream;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import com.aspose.words.SaveFormat;

@RestController("buildingDocumentController")
@RequestMapping("/rest/building/buildings")
public class BuildingDocumentController {

	static final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");

	@Autowired
	private ObjBuildingRepository repo;

	@Autowired
	private DocumentContentController documentController;

	@Autowired
	private DocumentGenerationService documentGeneration;

	@GetMapping(value = "/{id}/coverFoto")
	public ResponseEntity<byte[]> getCoverFoto(@PathVariable Integer id) {
		Integer documentId = this.repo.get(id).getCoverFotoId();
		return this.documentController.getContent(documentId);
	}

	@GetMapping("/{id}/evaluation/{viewerFileName}")
	protected ResponseEntity<byte[]> exportBuildingWithFileName(
			@PathVariable("id") Integer id,
			@RequestParam(required = false, name = "format") String format,
			@RequestParam(required = false, name = "inline") Boolean isInline) {
		return this.exportBuilding(id, format, isInline);
	}

	@GetMapping("/{id}/evaluation")
	protected ResponseEntity<byte[]> exportBuilding(
			@PathVariable("id") Integer id,
			@RequestParam(required = false, name = "format") String format,
			@RequestParam(required = false, name = "inline") Boolean isInline) {
		ObjBuilding building = this.repo.get(id);
		if (building == null) {
			return ResponseEntity.notFound().build();
		}
		try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
			documentGeneration.generateEvaluationReport(building, stream, this.getSaveFormat(format));
			// mark file for download
			HttpHeaders headers = new HttpHeaders();
			if (isInline != null && isInline) {
				return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).headers(headers)
						.body(stream.toByteArray());
			} else {
				String fileName = building.getAccount().getName() + " " + building.getName();
				fileName += " " + monthFormatter.format(OffsetDateTime.now());
				fileName = this.getFileName(fileName, this.getSaveFormat(format));
				ContentDisposition contentDisposition = ContentDisposition.builder("attachment").filename(fileName).build();
				headers.setContentDisposition(contentDisposition);
				return ResponseEntity.ok().headers(headers).body(stream.toByteArray());
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	private String getFileName(String fileName, int format) {
		return fileName + (format == SaveFormat.DOCX ? ".docx" : ".pdf");
	}

	private int getSaveFormat(String format) {
		return format != null && "docx".equals(format) ? SaveFormat.DOCX : SaveFormat.PDF;
	}

}
