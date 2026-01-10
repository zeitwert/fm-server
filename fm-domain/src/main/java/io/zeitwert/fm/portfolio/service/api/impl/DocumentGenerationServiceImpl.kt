package io.zeitwert.fm.portfolio.service.api.impl

import com.aspose.words.AxisBound
import com.aspose.words.AxisCategoryType
import com.aspose.words.Cell
import com.aspose.words.Document
import com.aspose.words.DocumentBuilder
import com.aspose.words.MarkerSymbol
import com.aspose.words.NodeType
import com.aspose.words.Paragraph
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
import io.zeitwert.app.model.SessionContext
import io.zeitwert.config.aspose.AsposeConfig
import io.zeitwert.fm.building.service.api.dto.BuildingEvaluationResult
import io.zeitwert.fm.building.service.api.dto.EvaluationBuilding
import io.zeitwert.fm.building.service.api.impl.BuildingEvaluationServiceImpl.Companion.BAD_CONDITION
import io.zeitwert.fm.building.service.api.impl.BuildingEvaluationServiceImpl.Companion.GOOD_CONDITION
import io.zeitwert.fm.building.service.api.impl.BuildingEvaluationServiceImpl.Companion.OK_CONDITION
import io.zeitwert.fm.portfolio.model.ObjPortfolio
import io.zeitwert.fm.portfolio.service.api.DocumentGenerationService
import io.zeitwert.fm.portfolio.service.api.PortfolioEvaluationService
import io.zeitwert.fm.portfolio.service.api.PortfolioService
import io.zeitwert.fm.portfolio.service.api.dto.PortfolioEvaluationResult
import io.zeitwert.fm.util.Formatter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

@Component("portfolioDocumentGenerationService")
class DocumentGenerationServiceImpl : DocumentGenerationService {

	private val logger: Logger = LoggerFactory.getLogger(DocumentGenerationServiceImpl::class.java)

	@Value("classpath:templates/Portfolio Evaluation Template.docx")
	var templateFile: Resource? = null

	@Value("classpath:templates/missing.jpg")
	var missingImage: Resource? = null
	var engine: ReportingEngine = ReportingEngine()

	@Autowired
	lateinit var sessionContext: SessionContext

	@Autowired
	lateinit var evaluationService: PortfolioEvaluationService

	@Autowired
	lateinit var asposeConfig: AsposeConfig

	@Autowired
	lateinit var portfolioService: PortfolioService

	init {
		this.engine.knownTypes.add(BuildingEvaluationResult::class.java)
	}

	override fun generateEvaluationReport(
		portfolio: ObjPortfolio,
		stream: ByteArrayOutputStream,
		format: Int,
	) {
		val evaluationResult = this.evaluationService.getEvaluation(portfolio)
		try {
			val doc = Document(this.templateFile!!.inputStream)
			doc.fontSettings = this.asposeConfig.fontSettings

			this.engine.buildReport(doc, evaluationResult, "portfolio")
			this.fillBuildingStateChart(doc, evaluationResult)
			this.fillValueChart(doc, evaluationResult)
			this.fillCostsChart(doc, evaluationResult)
			this.fillCostsTable(doc, evaluationResult)
			this.insertLocationImage(doc, portfolio)
			this.fillBuildingStateChartNames(doc, evaluationResult)

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
	private fun insertLocationImage(
		doc: Document?,
		portfolio: ObjPortfolio,
	) {
		val imageContent: ByteArray?
		val ir = this.portfolioService.getMap(portfolio, Size(1200, 1200))
		if (ir == null) {
			imageContent = this.missingImage!!.inputStream.readAllBytes()
		} else {
			imageContent = ir.imageData
		}

		val builder = DocumentBuilder(doc)
		try {
			builder.moveToBookmark(CoverfotoBookmark)
			val coverFoto = builder.insertImage(imageContent)
			coverFoto.aspectRatioLocked = true
			// adjust either width or height
			if (coverFoto.width / coverFoto.height > (CoverFotoWidth.toDouble()) / (CoverFotoHeight.toDouble())) {
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
			throw RuntimeException("Could not insert location image", e)
		}
	}

	private fun fillBuildingStateChart(
		doc: Document,
		evaluationResult: PortfolioEvaluationResult,
	) {
		val buildings: MutableList<EvaluationBuilding> = evaluationResult.buildings.toMutableList()
		buildings.sortWith({ a: EvaluationBuilding, b: EvaluationBuilding -> b.condition - a.condition })
		val goodBuildings = buildings.filter { it.condition >= 85 }
		val okBuildings = buildings.filter { it.condition >= 70 && it.condition < 85 }
		val badBuildings = buildings.filter { it.condition < 70 }

		val goodOriginalValues = DoubleArray(4 * goodBuildings.size + 2)
		val goodTimeValues = DoubleArray(4 * goodBuildings.size + 2)
		val okOriginalValues = DoubleArray(4 * okBuildings.size + 2)
		val okTimeValues = DoubleArray(4 * okBuildings.size + 2)
		val badOriginalValues = DoubleArray(4 * badBuildings.size + 2)
		val badTimeValues = DoubleArray(4 * badBuildings.size + 2)

		var cumulatedValue = 0
		var index = 0
		for (bldg in goodBuildings) {
			goodOriginalValues[index] = cumulatedValue.toDouble()
			goodTimeValues[index] = bldg.condition.toDouble()
			cumulatedValue += bldg.insuredValue!!
			goodOriginalValues[index + 1] = (cumulatedValue - 10).toDouble()
			goodTimeValues[index + 1] = bldg.condition.toDouble()
			goodOriginalValues[index + 2] = (cumulatedValue - 10).toDouble()
			goodTimeValues[index + 2] = 0.0
			goodOriginalValues[index + 3] = cumulatedValue.toDouble()
			goodTimeValues[index + 3] = 0.0
			index += 4
		}
		goodOriginalValues[index] = cumulatedValue.toDouble()
		goodTimeValues[index] = 0.0

		okOriginalValues[0] = 0.0
		okTimeValues[0] = 0.0
		okOriginalValues[1] = cumulatedValue.toDouble()
		okTimeValues[1] = 0.0
		index = 2
		for (bldg in okBuildings) {
			okOriginalValues[index] = cumulatedValue.toDouble()
			okTimeValues[index] = bldg.condition.toDouble()
			cumulatedValue += bldg.insuredValue!!
			okOriginalValues[index + 1] = (cumulatedValue - 10).toDouble()
			okTimeValues[index + 1] = bldg.condition.toDouble()
			okOriginalValues[index + 2] = (cumulatedValue - 10).toDouble()
			okTimeValues[index + 2] = 0.0
			okOriginalValues[index + 3] = cumulatedValue.toDouble()
			okTimeValues[index + 3] = 0.0
			index += 4
		}

		badOriginalValues[0] = cumulatedValue.toDouble()
		badTimeValues[0] = 0.0
		badOriginalValues[1] = cumulatedValue.toDouble()
		badTimeValues[1] = 0.0
		index = 2
		for (bldg in badBuildings) {
			badOriginalValues[index] = cumulatedValue.toDouble()
			badTimeValues[index] = bldg.condition.toDouble()
			cumulatedValue += bldg.insuredValue!!
			badOriginalValues[index + 1] = (cumulatedValue - 10).toDouble()
			badTimeValues[index + 1] = bldg.condition.toDouble()
			badOriginalValues[index + 2] = (cumulatedValue - 10).toDouble()
			badTimeValues[index + 2] = 0.0
			badOriginalValues[index + 3] = cumulatedValue.toDouble()
			badTimeValues[index + 3] = 0.0
			index += 4
		}

		goodOriginalValues[4 * goodBuildings.size + 1] = cumulatedValue.toDouble()
		goodTimeValues[4 * goodBuildings.size + 1] = 0.0

		val valueChartShape = doc.getChild(NodeType.SHAPE, BuildingStateChart, true) as Shape
		val valueChart = valueChartShape.chart
		valueChart.series.clear()
		val goodSeries = valueChart.series.add("Z/N 100", goodOriginalValues, goodTimeValues)
		val okSeries = valueChart.series.add("Z/N 100", okOriginalValues, okTimeValues)
		val badSeries = valueChart.series.add("Z/N 100", badOriginalValues, badTimeValues)
		goodSeries.format.fill.foreColor = GOOD_CONDITION
		okSeries.format.fill.foreColor = OK_CONDITION
		badSeries.format.fill.foreColor = BAD_CONDITION

		// format x-axis
		val xAxis = valueChart.axisX
		xAxis.categoryType = AxisCategoryType.TIME
		val totalValue = buildings
			.map { it.insuredValue ?: 0 }
			.reduce { a, b -> a + b }
			.toDouble()
		val targetStep = totalValue / 20
		val targetDim = floor(log10(targetStep))
		val stepSize = 10.0.pow(targetDim)
		if (targetStep < 1.4 * stepSize) {
			xAxis.majorUnit = stepSize
		} else if (targetStep < 3 * stepSize) {
			xAxis.majorUnit = 2 * stepSize
		} else {
			xAxis.majorUnit = 5 * stepSize
		}
	}

	private fun fillBuildingStateChartNames(
		doc: Document?,
		evaluationResult: PortfolioEvaluationResult,
	) {
		val buildings = evaluationResult.buildings.toMutableList()
		buildings.sortWith({ a, b -> b.condition - a.condition })

		// write building names
		val totalValue = buildings
			.map { it.insuredValue ?: 0 }
			.reduce { a, b -> a + b }
			.toDouble()
		var cumulatedValue = 0
		try {
			val shapeBuilder = DocumentBuilder(doc)
			shapeBuilder.moveToBookmark(BuildingStateBookmark)
			cumulatedValue = 0
			for (bldg in buildings) {
				if (bldg.insuredValue!! > 0.02 * totalValue) {
					val textbox = shapeBuilder.insertShape(ShapeType.TEXT_BOX, 8.0, 8.0)
					textbox.relativeHorizontalPosition = RelativeHorizontalPosition.PAGE
					textbox.relativeVerticalPosition = RelativeVerticalPosition.PAGE
					textbox.width = 400.0
					textbox.height = 100.0
					textbox.stroked = false
					textbox.filled = false
					textbox.wrapType = WrapType.NONE
					textbox.allowOverlap = true

					val paragraph = Paragraph(doc)
					val run = Run(doc, bldg.name)
					run.font.size = 8.0
					paragraph.appendChild(run)
					textbox.appendChild(paragraph)

					textbox.top = 228.0
					val offset = cumulatedValue + bldg.insuredValue / 2
					textbox.left = 112 + (offset / totalValue * 689 - 200)
					textbox.rotation = -90.0
				}
				cumulatedValue += bldg.insuredValue
			}
		} catch (ex: Exception) {
			this.logger.error("Building state chartNames crashed", ex)
			ex.printStackTrace()
		}
	}

	private fun fillValueChart(
		doc: Document,
		evaluationResult: PortfolioEvaluationResult,
	) {
		val periodCount = evaluationResult.periods.count { it.year != null }
		val years = arrayOfNulls<String>(periodCount)
		val originalValues = DoubleArray(periodCount)
		val timeValues = DoubleArray(periodCount)
		val maintenanceCosts = DoubleArray(periodCount)
		val restorationCosts = DoubleArray(periodCount)

		var index = 0
		for (ep in evaluationResult.periods) {
			if (ep.year != null && ep.year > 0) { // only yearly summary records
				years[index] = ep.year.toString()
				originalValues[index] = ep.originalValue!!.toDouble()
				timeValues[index] = ep.timeValue!!.toDouble()
				maintenanceCosts[index] = ep.maintenanceCosts!!.toDouble()
				restorationCosts[index] = ep.restorationCosts.toDouble()
				index += 1
			}
		}

		val valueChartShape = doc.getChild(NodeType.SHAPE, ValueValueChart, true) as Shape
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

		val costChartShape = doc.getChild(NodeType.SHAPE, ValueCostChart, true) as Shape
		val costChart = costChartShape.chart
		costChart.series.clear()
		costChart.series.add("Instandhaltung", years, maintenanceCosts)
		costChart.series.add("Instandsetzung", years, restorationCosts)
	}

	private fun fillCostsChart(
		doc: Document,
		evaluationResult: PortfolioEvaluationResult,
	) {
		val periodCount = evaluationResult.periods.count { it.year != null }
		val years = arrayOfNulls<String>(periodCount)
		val cumMaintenanceCosts = DoubleArray(periodCount)
		val cumTotalCosts = DoubleArray(periodCount)
		val maintenanceCosts = DoubleArray(periodCount)
		val restorationCosts = DoubleArray(periodCount)

		var index = 0
		for (ep in evaluationResult.periods) {
			if (ep.year != null && ep.year > 0) { // only yearly summary records
				years[index] = ep.year.toString()
				cumMaintenanceCosts[index] = if (index > 0) {
					cumMaintenanceCosts[index - 1] + ep.maintenanceCosts!!
				} else {
					ep.maintenanceCosts!!.toDouble()
				}
				cumTotalCosts[index] = if (index > 0) {
					cumTotalCosts[index - 1] + ep.maintenanceCosts + ep.restorationCosts
				} else {
					(ep.maintenanceCosts + ep.restorationCosts).toDouble()
				}
				maintenanceCosts[index] = ep.maintenanceCosts.toDouble()
				restorationCosts[index] = ep.restorationCosts.toDouble()
				index += 1
			}
		}

		val valueChartShape = doc.getChild(NodeType.SHAPE, CostsAccumulatedChart, true) as Shape
		val valueChart = valueChartShape.chart
		valueChart.series.clear()
		valueChart.series.add("Kumulierte IH Kosten", years, cumMaintenanceCosts)
		valueChart.series.add("Kumulierte IH+IS Kosten", years, cumTotalCosts)

		valueChart.axisY.scaling.minimum = AxisBound(0.0)
		valueChart
			.series
			.get(0)
			.marker.symbol = MarkerSymbol.CIRCLE
		valueChart
			.series
			.get(0)
			.marker.size = 5

		valueChart
			.series
			.get(1)
			.marker.symbol = MarkerSymbol.CIRCLE
		valueChart
			.series
			.get(1)
			.marker.size = 5

		val costChartShape = doc.getChild(NodeType.SHAPE, CostsDetailChart, true) as Shape
		val costChart = costChartShape.chart
		costChart.series.clear()
		costChart.series.add("Instandhaltung", years, maintenanceCosts)
		costChart.series.add("Instandsetzung", years, restorationCosts)
	}

	private fun fillCostsTable(
		doc: Document,
		evaluationResult: PortfolioEvaluationResult,
	) {
		val costsTable = doc.getChild(NodeType.TABLE, CostsDetailTable, true) as Table
		val builder = DocumentBuilder(doc)

		for (ep in evaluationResult.periods) {
			val row = this.addCostsTableRow(costsTable)
			var cell = row.firstCell
			if (ep.year != null) { // only yearly summary records
				// year
				builder.moveTo(cell.firstParagraph)
				builder.write("" + ep.year)
				// originalValue
				cell = cell.nextSibling as Cell
				builder.moveTo(cell.firstParagraph)
				builder.write(Formatter.formatNumber(ep.originalValue))
				// timeValue
				cell = cell.nextSibling as Cell
				builder.moveTo(cell.firstParagraph)
				builder.write(Formatter.formatNumber(ep.timeValue))
				// maintenanceCosts
				cell = cell.nextSibling as Cell
				builder.moveTo(cell.firstParagraph)
				builder.write(Formatter.formatNumber(ep.maintenanceCosts))
			} else {
				cell = cell.nextSibling.nextSibling.nextSibling as Cell
			}
			// restorationCosts
			cell = cell.nextSibling as Cell
			if (ep.restorationCosts > 0) {
				builder.moveTo(cell.firstParagraph)
				builder.write(Formatter.formatNumber(ep.restorationCosts))
			}
			// restorationElement
			cell = cell.nextSibling as Cell
			builder.moveTo(cell.firstParagraph)
			builder.write(ep.restorationBuilding)
			cell = cell.nextSibling as Cell
			builder.moveTo(cell.firstParagraph)
			builder.write(ep.restorationElement)
			if (ep.year != null) { // only yearly summary records
				// totalCosts
				cell = cell.nextSibling as Cell
				builder.moveTo(cell.firstParagraph)
				builder.write(Formatter.formatNumber(ep.totalCosts))
				// aggrCosts
				cell = cell.nextSibling as Cell
				builder.moveTo(cell.firstParagraph)
				builder.write(Formatter.formatNumber(ep.aggrCosts))
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

	companion object {

		const val CoverfotoBookmark: String = "CoverFoto"
		const val BuildingStateBookmark: String = "BuildingState"
		const val LocationBookmark: String = "Location"
		const val BuildingTable: Int = 0
		const val CostsSummaryTable: Int = 1
		const val CostsDetailTable: Int = 2
		const val OptimalRenovationTable: Int = 3
		const val BuildingStateChart: Int = 6
		const val ValueValueChart: Int = 7
		const val ValueCostChart: Int = 8
		const val CostsAccumulatedChart: Int = 9
		const val CostsDetailChart: Int = 10
		private const val CoverFotoWidth = 400
		private const val CoverFotoHeight = 230
	}

}
