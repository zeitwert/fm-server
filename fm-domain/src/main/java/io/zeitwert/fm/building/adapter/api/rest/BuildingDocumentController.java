package io.zeitwert.fm.building.adapter.api.rest;

import com.aspose.words.SaveFormat;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.building.service.api.DocumentGenerationService;
import io.zeitwert.fm.building.service.api.ProjectionService;
import io.zeitwert.fm.building.service.api.dto.ProjectionResult;
import io.zeitwert.fm.dms.adapter.api.rest.DocumentContentController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController("buildingDocumentController")
@RequestMapping("/rest/building/buildings")
public class BuildingDocumentController {

	static final MediaType ZIP_CONTENT = new MediaType(MimeType.valueOf("application/zip"));
	static final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

	@Autowired
	private ObjBuildingRepository cache;

	@Autowired
	private ProjectionService projectionService;

	@Autowired
	private DocumentContentController documentController;

	@Autowired
	private DocumentGenerationService documentGeneration;

	@GetMapping(value = "/{id}/coverFoto")
	public ResponseEntity<byte[]> getCoverFoto(@PathVariable Integer id) {
		Integer documentId = this.cache.get(id).coverFotoId;
		if (documentId == null) {
			return ResponseEntity.noContent().build();
		}
		return this.documentController.getContent(documentId);
	}

	@GetMapping("/{id}/projection")
	public ResponseEntity<ProjectionResult> getBuildingProjection(@PathVariable Integer id) {
		Set<ObjBuilding> buildings = Set.of(this.cache.get(id));
		return ResponseEntity.ok(this.projectionService.getProjection(buildings, ProjectionService.DefaultDuration));
	}

	@GetMapping("/{ids}/evaluation/{title}")
	public ResponseEntity<byte[]> getBuildingEvaluationWithTitle(
			@PathVariable("ids") String ids,
			@RequestParam(required = false, name = "format") String format,
			@RequestParam(required = false, name = "inline") Boolean isInline) {
		return this.getBuildingEvaluation(ids, format, isInline);
	}

	@GetMapping("/{ids}/evaluation")
	public ResponseEntity<byte[]> getBuildingEvaluation(
			@PathVariable("ids") String ids,
			@RequestParam(required = false, name = "format") String format,
			@RequestParam(required = false, name = "inline") Boolean isInline) {
		String[] idList = ids.split(",");
		if (idList.length == 1) {
			return this.getBuildingEvaluation(Integer.parseInt(idList[0]), format, isInline);
		} else {
			return this.getBuildingEvaluation(idList, format);
		}
	}

	private ResponseEntity<byte[]> getBuildingEvaluation(Integer id, String format, Boolean isInline) {
		ObjBuilding building = this.cache.get(id);
		if (building == null) {
			return ResponseEntity.notFound().build();
		}
		try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
			this.documentGeneration.generateEvaluationReport(building, stream, this.getSaveFormat(format));
			String fileName = building.account.getName() + " - " + building.name;
			fileName += " - " + monthFormatter.format(OffsetDateTime.now());
			fileName = this.getFileName(fileName, this.getSaveFormat(format));
			// mark file for download
			HttpHeaders headers = new HttpHeaders();
			if (isInline != null && isInline) {
				headers.setContentDisposition(ContentDisposition.builder("inline").filename(fileName).build());
			} else {
				headers.setContentDisposition(ContentDisposition.builder("attachment").filename(fileName).build());
			}
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).headers(headers)
					.body(stream.toByteArray());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage().getBytes());
		}
	}

	private ResponseEntity<byte[]> getBuildingEvaluation(String[] ids, String format) {
		for (String id : ids) {
			ObjBuilding building = this.cache.get(Integer.parseInt(id));
			if (building == null) {
				return ResponseEntity.notFound().build();
			}
		}
		String dateTimeNow = monthFormatter.format(OffsetDateTime.now());
		try (
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ZipOutputStream zos = new ZipOutputStream(baos)) {
			for (String id : ids) {
				ObjBuilding building = this.cache.get(Integer.parseInt(id));
				try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
					this.documentGeneration.generateEvaluationReport(building, stream, this.getSaveFormat(format));
					String fileName = building.account.getName() + " - " + building.name;
					fileName += " - " + dateTimeNow;
					fileName = this.getFileName(fileName, this.getSaveFormat(format));
					fileName = fileName.replace("/", " ");
					ZipEntry entry = new ZipEntry(fileName);
					entry.setSize(stream.size());
					zos.putNextEntry(entry);
					zos.write(stream.toByteArray());
					zos.closeEntry();
				} catch (Exception e) {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage().getBytes());
				}
			}
			zos.close();
			// mark file for download
			String zipFileName = "Gebäudeauswertungen - " + dateTimeNow + ".zip";
			HttpHeaders headers = new HttpHeaders();
			headers.setContentDisposition(ContentDisposition.builder("attachment").filename(zipFileName).build());
			return ResponseEntity.ok().contentType(ZIP_CONTENT).headers(headers)
					.body(baos.toByteArray());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage().getBytes());
		}
	}

	private String getFileName(String fileName, int format) {
		return fileName + (format == SaveFormat.DOCX ? ".docx" : ".pdf");
	}

	private int getSaveFormat(String format) {
		return "docx".equals(format) ? SaveFormat.DOCX : SaveFormat.PDF;
	}

}
