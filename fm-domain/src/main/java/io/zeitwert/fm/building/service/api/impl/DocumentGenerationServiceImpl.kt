package io.zeitwert.fm.building.service.api.impl

import com.aspose.words.AxisBound
import com.aspose.words.Cell
import com.aspose.words.Document
import com.aspose.words.DocumentBuilder
import com.aspose.words.MarkerSymbol
import com.aspose.words.Node
import com.aspose.words.NodeType
import com.aspose.words.PdfEncryptionDetails
import com.aspose.words.PdfPermissions
import com.aspose.words.PdfSaveOptions
import com.aspose.words.RelativeHorizontalPosition
import com.aspose.words.RelativeVerticalPosition
import com.aspose.words.ReportingEngine
import com.aspose.words.Row
import com.aspose.words.Run
import com.aspose.words.SaveFormat
import com.aspose.words.Shape
import com.aspose.words.ShapeType
import com.aspose.words.Table
import com.aspose.words.WrapType
import com.google.maps.model.Size
import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.fm.building.model.ObjBuilding
import io.zeitwert.fm.building.service.api.BuildingEvaluationService
import io.zeitwert.fm.building.service.api.BuildingService
import io.zeitwert.fm.building.service.api.DocumentGenerationService
import io.zeitwert.fm.building.service.api.dto.BuildingEvaluationResult
import io.zeitwert.fm.building.service.api.dto.EvaluationElement
import io.zeitwert.fm.building.service.api.dto.EvaluationPeriod
import io.zeitwert.fm.server.config.aspose.AsposeConfig
import io.zeitwert.fm.util.Formatter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import java.awt.Color
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import kotlin.math.max
import kotlin.math.min

@Component("buildingDocumentGenerationService")
class DocumentGenerationServiceImpl : DocumentGenerationService {

	private val logger: Logger = LoggerFactory.getLogger(DocumentGenerationServiceImpl::class.java)

	@Autowired
	lateinit var sessionContext: SessionContext

	@Autowired
	lateinit var evaluationService: BuildingEvaluationService

	@Value("classpath:templates/Building Evaluation Template.docx")
	lateinit var templateFile: Resource

	@Value("classpath:templates/missing.jpg")
	lateinit var missingImage: Resource

	@Autowired
	lateinit var asposeConfig: AsposeConfig

	@Autowired
	lateinit var buildingService: BuildingService

	var engine: ReportingEngine = ReportingEngine()

	init {
		this.engine.knownTypes.add(BuildingEvaluationResult::class.java)
	}

	override fun generateEvaluationReport(
		building: ObjBuilding,
		stream: ByteArrayOutputStream,
		format: Int,
	) {
		val evaluationResult = this.evaluationService.getEvaluation(building)

		try {
			val doc = Document(this.templateFile.inputStream)
			doc.fontSettings = this.asposeConfig.getFontSettings()

			this.insertCoverFoto(doc, building)
			this.insertLocationImage(doc, building)
			this.engine.buildReport(doc, evaluationResult, "building")
			this.fillOptRenovationTable(doc, evaluationResult)
			this.fillCostsChart(doc, evaluationResult)
			this.fillCostsTable(doc, evaluationResult)
			this.fillOnePagerCostsChart(doc, evaluationResult)
			this.fillOnePager(doc, evaluationResult)

			if (format == SaveFormat.PDF) {
				val saveOptions = PdfSaveOptions()
				saveOptions.saveFormat = format
				// Create encryption details and set user password = 1
				val year = Calendar.getInstance().get(Calendar.YEAR)
				val encryptionDetails = PdfEncryptionDetails(null, "zeit" + year + "wert")
				// Disallow all
				encryptionDetails.permissions = PdfPermissions.DISALLOW_ALL
				// Allow printing
				encryptionDetails.permissions = PdfPermissions.PRINTING
				saveOptions.encryptionDetails = encryptionDetails
				doc.save(stream, saveOptions)
			} else {
				doc.save(stream, format)
			}
		} catch (ex: Exception) {
			this.logger.error("Document generation crashed", ex)
			ex.printStackTrace()
			throw RuntimeException("Document generation crashed", ex)
		}
	}

	@Throws(IOException::class)
	private fun insertCoverFoto(
		doc: Document?,
		building: ObjBuilding,
	) {
		val imageContent: ByteArray?
		if (building.coverFoto == null || building.coverFoto!!.contentType == null) {
			imageContent = this.missingImage.inputStream.readAllBytes()
		} else {
			imageContent = building.coverFoto!!.content
		}

		val builder = DocumentBuilder(doc)
		try {
			builder.moveToBookmark(CoverfotoBookmark)
			val coverFoto = builder.insertImage(imageContent)
			coverFoto.aspectRatioLocked = true
			// adjust either width or height
			if (coverFoto.width / coverFoto.height >
				(CoverFotoWidth.toDouble()) / (CoverFotoHeight.toDouble())
			) {
				coverFoto.width = CoverFotoWidth.toDouble()
			} else {
				coverFoto.height = CoverFotoHeight.toDouble()
			}
			coverFoto.wrapType = WrapType.NONE
			coverFoto.relativeHorizontalPosition = RelativeHorizontalPosition.RIGHT_MARGIN
			coverFoto.relativeVerticalPosition = RelativeVerticalPosition.TOP_MARGIN
			coverFoto.top = 170.0
			coverFoto.left = -coverFoto.width - 20
		} catch (e: Exception) {
			throw RuntimeException("Could not insert cover foto", e)
		}
	}

	@Throws(IOException::class)
	private fun insertLocationImage(
		doc: Document?,
		building: ObjBuilding,
	) {
		val imageContent: ByteArray?
		if (building.geoCoordinates == null || "" == building.geoCoordinates) {
			imageContent = this.missingImage.inputStream.readAllBytes()
		} else {
			val address = building.geoCoordinates!!.substring(4)
			val ir = this.buildingService.getMap(address, Size(1200, 1200), building.geoZoom!!)
			if (ir == null) {
				imageContent = this.missingImage.inputStream.readAllBytes()
			} else {
				imageContent = ir.imageData
			}
		}

		val builder = DocumentBuilder(doc)
		try {
			builder.moveToBookmark(LocationBookmark)
			builder.insertImage(imageContent, 360.0, 360.0)
		} catch (e: Exception) {
			throw RuntimeException("Could not insert location image", e)
		}
	}

	private fun fillOptRenovationTable(
		doc: Document,
		evaluationResult: BuildingEvaluationResult,
	) {
		val optRenovationTable = doc.getChild(NodeType.TABLE, OptimalRenovationTable, true) as Table
		val builder = DocumentBuilder(doc)

		var yearCell = (optRenovationTable.firstRow.nextSibling as Row).firstCell
		for (i in 0..25) {
			yearCell = yearCell.nextSibling as Cell
			builder.moveTo(yearCell.firstParagraph)
			builder.write("" + (evaluationResult.startYear!! + i))
		}

		var totalRestorationCosts = 0
		for (e in evaluationResult.elements!!) {
			if (e != null && e.weight != null && e.weight > 0 && ("Total" != e.name)) {
				val row = this.addRenovationTableRow(optRenovationTable)
				var cell = row.firstCell
				cell.firstParagraph.appendChild(Run(doc, e.name))
				val restorationYear = e.restorationYear
				if (restorationYear != null) {
					val delta = max(0, restorationYear - evaluationResult.startYear!!)
					cell = this.getNthNextSibling(row.firstCell, delta)
					builder.moveTo(cell.firstParagraph)
					builder.write(OptimumRenovationMarker)
					cell = row.lastCell
					val costs = Formatter.INSTANCE.formatNumber(e.restorationCosts)
					builder.moveTo(cell.firstParagraph)
					builder.write(costs)
					cell = cell.previousSibling as Cell
					builder.moveTo(cell.firstParagraph)
					builder.write("" + restorationYear)
					totalRestorationCosts += e.restorationCosts ?: 0
				}
			}
		}

		val row = this.addRenovationTableRow(optRenovationTable)
		var cell = row.firstCell
		builder.font.bold = true
		builder.moveTo(cell.firstParagraph)
		builder.write("Total")
		cell = row.lastCell
		val costs = Formatter.INSTANCE.formatNumber(totalRestorationCosts)
		builder.moveTo(cell.firstParagraph)
		builder.write(costs)

		optRenovationTable.firstRow.nextSibling.nextSibling
			.remove()
	}

	private fun addRenovationTableRow(table: Table): Row {
		val templateRow = table.firstRow.nextSibling.nextSibling as Row
		val clonedRow = templateRow.deepClone(true) as Row
		table.appendChild(clonedRow)
		return clonedRow
	}

	private fun fillCostsChart(
		doc: Document,
		evaluationResult: BuildingEvaluationResult,
	) {
		val periodCount = evaluationResult.periods!!.count { p: EvaluationPeriod? -> p?.year != null }
		val years = arrayOfNulls<String>(periodCount)
		val originalValues = DoubleArray(periodCount)
		val timeValues = DoubleArray(periodCount)
		val maintenanceCosts = DoubleArray(periodCount)
		val restorationCosts = DoubleArray(periodCount)

		var index = 0
		for (ep in evaluationResult.periods!!) {
			if (ep != null && ep.year != null && ep.year > 0) { // only yearly summary records
				years[index] = ep.year.toString()
				originalValues[index] = (ep.originalValue ?: 0).toDouble()
				timeValues[index] = (ep.timeValue ?: 0).toDouble()
				maintenanceCosts[index] = (ep.maintenanceCosts ?: 0).toDouble()
				restorationCosts[index] = (ep.restorationCosts ?: 0).toDouble()
				index += 1
			}
		}

		val valueChartShape = doc.getChild(NodeType.SHAPE, 5, true) as Shape
		val valueChart = valueChartShape.chart
		valueChart.series.clear()
		valueChart.series.add("Neuwert (indexiert)", years, originalValues)
		valueChart.series.add("Zeitwert", years, timeValues)

		Arrays.sort(timeValues)
		val minValue = timeValues[0]
		valueChart.axisY.scaling.minimum = AxisBound(minValue)
		valueChart.series
			.get(0)
			.marker.symbol = MarkerSymbol.CIRCLE
		valueChart.series
			.get(0)
			.marker.size = 5

		valueChart.series
			.get(1)
			.marker.symbol = MarkerSymbol.CIRCLE
		valueChart.series
			.get(1)
			.marker.size = 5

		val costChartShape = doc.getChild(NodeType.SHAPE, 6, true) as Shape
		val costChart = costChartShape.chart
		costChart.series.clear()
		costChart.series.add("Instandhaltung", years, maintenanceCosts)
		costChart.series.add("Instandsetzung", years, restorationCosts)
	}

	private fun fillCostsTable(
		doc: Document,
		evaluationResult: BuildingEvaluationResult,
	) {
		val costsTable = doc.getChild(NodeType.TABLE, CostsTable, true) as Table
		val builder = DocumentBuilder(doc)
		val fmt = Formatter.INSTANCE

		for (ep in evaluationResult.periods!!) {
			val row = this.addCostsTableRow(costsTable)
			var cell = row.firstCell
			if (ep?.year != null) { // only yearly summary records
				// year
				builder.moveTo(cell.firstParagraph)
				builder.write("" + ep.year)
				// originalValue
				cell = cell.nextSibling as Cell
				builder.moveTo(cell.firstParagraph)
				builder.write(fmt.formatNumber(ep.originalValue))
				// timeValue
				cell = cell.nextSibling as Cell
				builder.moveTo(cell.firstParagraph)
				builder.write(fmt.formatNumber(ep.timeValue))
				// maintenanceCosts
				cell = cell.nextSibling as Cell
				builder.moveTo(cell.firstParagraph)
				builder.write(fmt.formatNumber(ep.maintenanceCosts))
			} else {
				cell = cell.nextSibling.nextSibling.nextSibling as Cell
			}
			// restorationCosts
			cell = cell.nextSibling as Cell
			if (ep?.restorationCosts != null && ep.restorationCosts > 0) {
				builder.moveTo(cell.firstParagraph)
				builder.write(fmt.formatNumber(ep.restorationCosts))
			}
			// restorationElement
			cell = cell.nextSibling as Cell
			builder.moveTo(cell.firstParagraph)
			builder.write(ep?.restorationElement ?: "")
			if (ep?.year != null) { // only yearly summary records
				// totalCosts
				cell = cell.nextSibling as Cell
				builder.moveTo(cell.firstParagraph)
				builder.write(fmt.formatNumber(ep.totalCosts))
				// aggrCosts
				cell = cell.nextSibling as Cell
				builder.moveTo(cell.firstParagraph)
				builder.write(fmt.formatNumber(ep.aggrCosts))
			}
		}

		costsTable.firstRow.nextSibling.remove()
	}

	private fun addCostsTableRow(table: Table): Row {
		val templateRow = table.firstRow.nextSibling as Row
		val clonedRow = templateRow.deepClone(true) as Row
		table.appendChild(clonedRow)
		return clonedRow
	}

	private fun fillOnePagerCostsChart(
		doc: Document,
		evaluationResult: BuildingEvaluationResult,
	) {
		val periodCount = evaluationResult.periods!!.count { p: EvaluationPeriod? -> p?.year != null }
		val years = arrayOfNulls<String>(periodCount)
		val maintenanceCosts = DoubleArray(periodCount)
		val restorationCosts = DoubleArray(periodCount)

		var index = 0
		for (ep in evaluationResult.periods!!) {
			if (ep != null && ep.year != null && ep.year > 0) { // only yearly summary records
				years[index] = ep.year.toString()
				maintenanceCosts[index] = (ep.maintenanceCosts ?: 0) / 1000.0
				restorationCosts[index] = (ep.restorationCosts ?: 0) / 1000.0
				index += 1
			}
		}

		val costChartShape = doc.getChild(NodeType.SHAPE, 9, true) as Shape
		val costChart = costChartShape.chart
		costChart.series.clear()
		costChart.series.add("Instandhaltung", years, maintenanceCosts)
		costChart.series.add("Instandsetzung", years, restorationCosts)
	}

	@Throws(Exception::class)
	private fun fillOnePager(
		doc: Document,
		evaluationResult: BuildingEvaluationResult,
	) {
		val onePagerDetailsTable = doc.getChild(NodeType.TABLE, OnePageDetailsTable, true) as Table
		val builder = DocumentBuilder(doc)
		val shapeBuilder = DocumentBuilder(doc)
		shapeBuilder.moveToBookmark(OnePagerBookmark)

		var yearCell = onePagerDetailsTable.lastRow.firstCell
		yearCell = this.getNthNextSibling(yearCell, 8)
		for (i in 0..25) {
			yearCell = yearCell.nextSibling as Cell
			builder.moveTo(yearCell.firstParagraph)
			builder.write("" + (evaluationResult.startYear!! + i))
		}

		run {
			val titleElement = evaluationResult.elements!![evaluationResult.elements!!.size - 1]!!
			val titleRow = onePagerDetailsTable.firstRow.nextSibling as Row
			var titleCell = titleRow.firstCell

			val shape = shapeBuilder.insertShape(ShapeType.FLOW_CHART_CONNECTOR, 8.0, 8.0)
			shape.stroked = false
			shape.fillColor = titleElement.conditionColor
			shape.wrapType = WrapType.NONE
			shape.allowOverlap = true
			shape.relativeHorizontalPosition = RelativeHorizontalPosition.PAGE
			shape.relativeVerticalPosition = RelativeVerticalPosition.PAGE
			shape.top = this.getRatingLineVOffset(0)
			shape.left = this.getRatingHOffset(titleElement.condition!!)

			titleCell = this.getNthNextSibling(titleRow.firstCell, 13)
			builder.moveTo(titleCell.firstParagraph)
			builder.write("" + titleElement.condition)
		}

		val elements =
			evaluationResult.elements!!
				.filterNotNull()
				.filter { e: EvaluationElement -> "Total" != e.name }
				.filter { e: EvaluationElement -> e.weight != null && e.weight > 0 }

		val maxWeight = elements.mapNotNull { a: EvaluationElement -> a.weight }.maxOrNull() ?: 0

		var lineNr = 1
		for (e in elements) {
			val row = this.addOnePageTableRow(onePagerDetailsTable)

			var cell = row.firstCell
			builder.moveTo(cell.firstParagraph)
			builder.write(e.name)

			cell = cell.nextSibling as Cell
			val weight = Math.round(76.0 * e.weight!! / maxWeight).toInt()
			builder.moveTo(cell.firstParagraph)
			builder.write(String(CharArray(weight)).replace('\u0000', 'I'))

			val weightPC = Formatter.INSTANCE.formatValueWithUnit(e.weight, "%")
			cell = cell.nextSibling as Cell
			builder.moveTo(cell.firstParagraph)
			builder.write(weightPC)

			val shape = shapeBuilder.insertShape(ShapeType.FLOW_CHART_CONNECTOR, 8.0, 8.0)
			shape.stroked = false
			shape.fillColor = e.conditionColor
			shape.wrapType = WrapType.NONE
			shape.allowOverlap = true
			shape.relativeHorizontalPosition = RelativeHorizontalPosition.PAGE
			shape.relativeVerticalPosition = RelativeVerticalPosition.PAGE
			shape.top = this.getRatingLineVOffset(lineNr)
			shape.left = this.getRatingHOffset(e.condition!!)

			cell = this.getNthNextSibling(row.firstCell, 13)
			builder.moveTo(cell.firstParagraph)
			builder.write("" + e.condition)

			val restorationYear = e.restorationYear
			if (restorationYear != null) {
				val delta = 15 + max(0, restorationYear - evaluationResult.startYear!!)
				cell = this.getNthNextSibling(row.firstCell, delta)
				builder.moveTo(cell.firstParagraph)
				builder.write(OptimumRenovationMarker)
			}

			lineNr++
		}

		onePagerDetailsTable.firstRow.nextSibling.nextSibling
			.remove()

		var cell = (onePagerDetailsTable.firstRow.nextSibling as Row).firstCell
		cell.cellFormat.borders.bottom.color = Color.BLACK
		cell.cellFormat.borders.bottom.lineWidth = 1.0
		for (i in 0..12) {
			cell = cell.nextSibling as Cell
			cell.cellFormat.borders.bottom.color = Color.BLACK
			cell.cellFormat.borders.bottom.lineWidth = 1.0
		}
	}

	private fun addOnePageTableRow(table: Table): Row {
		val templateRow = table.firstRow.nextSibling.nextSibling as Row
		val clonedRow = templateRow.deepClone(true) as Row
		table.insertBefore(clonedRow, table.lastRow)
		return clonedRow
	}

	private fun getRatingLineVOffset(lineNr: Int): Double = 98.8 * POINTS_PER_MM + 11.84 * lineNr

	private fun getRatingHOffset(rating: Int): Double {
		val ratingDelta = min((100 - rating).toDouble(), 50.0) / 50.0
		return 90.3 * POINTS_PER_MM + ratingDelta * (124.5 - 75.3) * POINTS_PER_MM
	}

	private fun getNthNextSibling(
		cell: Node,
		n: Int,
	): Cell {
		if (n >= 0) {
			return this.getNthNextSibling(cell.nextSibling, n - 1)
		}
		return cell as Cell
	}

	companion object {

		const val CoverfotoBookmark: String = "CoverFoto"
		const val LocationBookmark: String = "Location"
		const val OnePagerBookmark: String = "OnePager"
		const val BasicDataTable: Int = 0
		const val EvaluationTable: Int = 1
		const val ElementTable: Int = 2
		const val OptimalRenovationTable: Int = 3
		const val CostsTable: Int = 4
		const val OnePageDetailsTable: Int = 5
		const val OnePageBasicDataTable: Int = 6
		const val OnePageEvaluationTable: Int = 7
		private const val POINTS_PER_MM = 2.834647454889553
		private const val CoverFotoWidth = 400
		private const val CoverFotoHeight = 230
		private val OptimumRenovationMarker: String = Character.toString(110.toChar())
	}
}
