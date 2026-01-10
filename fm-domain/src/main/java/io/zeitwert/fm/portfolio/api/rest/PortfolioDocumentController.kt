package io.zeitwert.fm.portfolio.api.rest

import com.aspose.words.SaveFormat
import io.zeitwert.fm.building.model.ObjBuildingRepository
import io.zeitwert.fm.building.service.api.ProjectionService
import io.zeitwert.fm.building.service.api.dto.ProjectionResult
import io.zeitwert.fm.portfolio.model.ObjPortfolioRepository
import io.zeitwert.fm.portfolio.service.api.DocumentGenerationService
import org.springframework.beans.factory.annotation.Autowired
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

@RestController("portfolioDocumentController")
@RequestMapping("/rest/portfolio/portfolios")
class PortfolioDocumentController {

	@Autowired
	lateinit var portfolioRepository: ObjPortfolioRepository

	@Autowired
	lateinit var buildingRepository: ObjBuildingRepository

	@Autowired
	lateinit var projectionService: ProjectionService

	@Autowired
	lateinit var documentGeneration: DocumentGenerationService

	@GetMapping("/{id}/projection")
	fun getPortfolioProjection(
		@PathVariable id: Int,
	): ResponseEntity<ProjectionResult> {
		val buildings = portfolioRepository
			.get(id)
			.buildingSet
			.map { buildingRepository.get(it) }
			.toSet()
		return ResponseEntity.ok(projectionService.getProjection(buildings, ProjectionService.DefaultDuration))
	}

	@GetMapping("/{id}/evaluation/{title}")
	fun getPortfolioEvaluationWithTitle(
		@PathVariable("id") id: Int,
		@RequestParam(required = false, name = "format") format: String?,
		@RequestParam(required = false, name = "inline") isInline: Boolean?,
	): ResponseEntity<ByteArray> = getPortfolioEvaluation(id, format, isInline)

	@GetMapping("/{ids}/evaluation")
	fun getPortfolioEvaluation(
		@PathVariable("ids") ids: String,
		@RequestParam(required = false, name = "format") format: String?,
		@RequestParam(required = false, name = "inline") isInline: Boolean?,
	): ResponseEntity<ByteArray> {
		val idList = ids.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
		if (idList.size == 1) {
			return getPortfolioEvaluation(idList[0].toInt(), format, isInline)
		} else {
			return getPortfolioEvaluation(idList, format)
		}
	}

	private fun getPortfolioEvaluation(
		id: Int,
		format: String?,
		isInline: Boolean?,
	): ResponseEntity<ByteArray> {
		val portfolio = portfolioRepository.get(id)
		try {
			ByteArrayOutputStream().use { stream ->
				documentGeneration.generateEvaluationReport(portfolio, stream, getSaveFormat(format))
				var fileName = portfolio.account!!.name + " - " + portfolio.name
				fileName += " - " + monthFormatter.format(OffsetDateTime.now())
				fileName = getFileName(fileName, getSaveFormat(format))
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
					.body(stream.toByteArray())
			}
		} catch (e: Exception) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.message!!.toByteArray())
		}
	}

	private fun getPortfolioEvaluation(
		ids: Array<String>,
		format: String?,
	): ResponseEntity<ByteArray> {
		for (id in ids) {
			portfolioRepository.get(id.toInt())
		}
		val dateTimeNow = monthFormatter.format(OffsetDateTime.now())
		try {
			ByteArrayOutputStream().use { baos ->
				ZipOutputStream(baos).use { zos ->
					for (id in ids) {
						val portfolio = portfolioRepository.get(id.toInt())
						try {
							ByteArrayOutputStream().use { stream ->
								documentGeneration.generateEvaluationReport(
									portfolio,
									stream,
									getSaveFormat(format),
								)
								var fileName = portfolio.account!!.name + " - " + portfolio.name
								fileName += " - $dateTimeNow"
								fileName = getFileName(fileName, getSaveFormat(format))
								fileName = fileName.replace("/", " ")
								val entry = ZipEntry(fileName)
								entry.setSize(stream.size().toLong())
								zos.putNextEntry(entry)
								zos.write(stream.toByteArray())
								zos.closeEntry()
							}
						} catch (e: Exception) {
							return ResponseEntity
								.status(HttpStatus.INTERNAL_SERVER_ERROR)
								.body(e.message!!.toByteArray())
						}
					}
					zos.close()
					// mark file for download
					val zipFileName = "Gebäudeauswertungen - $dateTimeNow.zip"
					val headers = HttpHeaders()
					headers.contentDisposition = ContentDisposition.builder("attachment").filename(zipFileName).build()
					return ResponseEntity
						.ok()
						.contentType(ZIP_CONTENT)
						.headers(headers)
						.body(baos.toByteArray())
				}
			}
		} catch (e: Exception) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.message!!.toByteArray())
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
