package io.zeitwert.fm.building.adapter.api.rest

import com.aspose.words.SaveFormat
import io.zeitwert.fm.building.model.ObjBuildingRepository
import io.zeitwert.fm.building.service.api.DocumentGenerationService
import io.zeitwert.fm.building.service.api.ProjectionService
import io.zeitwert.fm.building.service.api.dto.ProjectionResult
import io.zeitwert.fm.dms.adapter.api.rest.DocumentContentController
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.MimeType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.ByteArrayOutputStream
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@RestController("buildingDocumentController")
@RequestMapping("/rest/building/buildings")
class BuildingDocumentController(
	val buildingRepo: ObjBuildingRepository,
	val projectionService: ProjectionService,
	val documentController: DocumentContentController,
	val documentGeneration: DocumentGenerationService,
) {

	@GetMapping(value = ["/{id}/coverFoto"])
	fun getCoverFoto(
		@PathVariable id: Int,
	): ResponseEntity<ByteArray> {
		val documentId = this.buildingRepo.get(id).coverFotoId
		if (documentId == null) {
			return ResponseEntity.noContent().build<ByteArray?>()
		}
		return this.documentController.getContent(documentId as Int)
	}

	@GetMapping("/{id}/projection")
	fun getBuildingProjection(
		@PathVariable id: Int,
	): ResponseEntity<ProjectionResult?> {
		val buildings = setOf(this.buildingRepo.get(id))
		return ResponseEntity.ok<ProjectionResult?>(
			this.projectionService.getProjection(
				buildings,
				ProjectionService.DefaultDuration,
			),
		)
	}

	@GetMapping("/{ids}/evaluation/{title}")
	fun getBuildingEvaluationWithTitle(
		@PathVariable("ids") ids: String,
		@RequestParam(required = false, name = "format") format: String?,
		@RequestParam(required = false, name = "inline") isInline: Boolean?,
	): ResponseEntity<ByteArray?> = this.getBuildingEvaluation(ids, format, isInline)

	@GetMapping("/{ids}/evaluation")
	fun getBuildingEvaluation(
		@PathVariable("ids") ids: String,
		@RequestParam(required = false, name = "format") format: String?,
		@RequestParam(required = false, name = "inline") isInline: Boolean?,
	): ResponseEntity<ByteArray?> {
		val idList = ids.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
		if (idList.size == 1) {
			return this.getBuildingEvaluation(idList[0].toInt(), format, isInline)
		} else {
			return this.getBuildingEvaluation(idList, format)
		}
	}

	private fun getBuildingEvaluation(
		id: Int,
		format: String?,
		isInline: Boolean?,
	): ResponseEntity<ByteArray?> {
		val building = this.buildingRepo.get(id)
		try {
			ByteArrayOutputStream().use { stream ->
				this.documentGeneration.generateEvaluationReport(building, stream, this.getSaveFormat(format))
				var fileName = building.account!!.name + " - " + building.name
				fileName += " - " + monthFormatter.format(OffsetDateTime.now())
				fileName = this.getFileName(fileName, this.getSaveFormat(format))
				// mark file for download
				val headers = HttpHeaders()
				if (isInline != null && isInline) {
					headers.contentDisposition = ContentDisposition.builder("inline").filename(fileName).build()
				} else {
					headers.contentDisposition = ContentDisposition.builder("attachment").filename(fileName).build()
				}
				return ResponseEntity
					.ok()
					.contentType(MediaType.APPLICATION_PDF)
					.headers(headers)
					.body<ByteArray?>(stream.toByteArray())
			}
		} catch (e: Exception) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body<ByteArray?>(e.message!!.toByteArray())
		}
	}

	private fun getBuildingEvaluation(
		ids: Array<String>,
		format: String?,
	): ResponseEntity<ByteArray?> {
		for (id in ids) {
			this.buildingRepo.get(id.toInt())
		}
		val dateTimeNow = monthFormatter.format(OffsetDateTime.now())
		try {
			ByteArrayOutputStream().use { baos ->
				ZipOutputStream(baos).use { zos ->
					for (id in ids) {
						val building = this.buildingRepo.get(id.toInt())
						try {
							ByteArrayOutputStream().use { stream ->
								this.documentGeneration.generateEvaluationReport(building, stream, this.getSaveFormat(format))
								var fileName = building.account!!.name + " - " + building.name
								fileName += " - " + dateTimeNow
								fileName = this.getFileName(fileName, this.getSaveFormat(format))
								fileName = fileName.replace("/", " ")
								val entry = ZipEntry(fileName)
								entry.setSize(stream.size().toLong())
								zos.putNextEntry(entry)
								zos.write(stream.toByteArray())
								zos.closeEntry()
							}
						} catch (e: Exception) {
							return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body<ByteArray?>(e.message!!.toByteArray())
						}
					}
					zos.close()
					// mark file for download
					val zipFileName = "Gebäudeauswertungen - " + dateTimeNow + ".zip"
					val headers = HttpHeaders()
					headers.contentDisposition = ContentDisposition.builder("attachment").filename(zipFileName).build()
					return ResponseEntity
						.ok()
						.contentType(ZIP_CONTENT)
						.headers(headers)
						.body<ByteArray?>(baos.toByteArray())
				}
			}
		} catch (e: Exception) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body<ByteArray?>(e.message!!.toByteArray())
		}
	}

	private fun getFileName(
		fileName: String?,
		format: Int,
	): String = fileName + (if (format == SaveFormat.DOCX) ".docx" else ".pdf")

	private fun getSaveFormat(format: String?): Int = if ("docx" == format) SaveFormat.DOCX else SaveFormat.PDF

	companion object {

		val ZIP_CONTENT: MediaType = MediaType(MimeType.valueOf("application/zip"))
		val monthFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
	}
}
