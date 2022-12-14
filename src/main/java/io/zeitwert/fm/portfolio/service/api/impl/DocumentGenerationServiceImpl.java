
package io.zeitwert.fm.portfolio.service.api.impl;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.DoubleStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.aspose.words.*;
import com.google.maps.ImageResult;
import com.google.maps.model.Size;

import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.ddd.util.Formatter;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.service.api.BuildingService;
import io.zeitwert.fm.portfolio.model.ObjPortfolio;
import io.zeitwert.fm.portfolio.service.api.DocumentGenerationService;
import io.zeitwert.fm.portfolio.service.api.dto.PortfolioEvaluationResult;
import io.zeitwert.fm.portfolio.service.api.PortfolioEvaluationService;
import io.zeitwert.fm.building.service.api.dto.BuildingEvaluationResult;
import io.zeitwert.fm.building.service.api.dto.EvaluationBuilding;
import io.zeitwert.fm.building.service.api.dto.EvaluationElement;
import io.zeitwert.fm.building.service.api.dto.EvaluationPeriod;
import io.zeitwert.server.config.aspose.AsposeConfig;

@Component("portfolioDocumentGenerationService")
public class DocumentGenerationServiceImpl implements DocumentGenerationService {

	private static final int CoverFotoWidth = 400;
	private static final int CoverFotoHeight = 230;

	private static final String OptimumRenovationMarker = Character.toString((char) 110);

	static final String CoverfotoBookmark = "CoverFoto";
	static final String LocationBookmark = "Location";

	static final int BuildingTable = 0;
	static final int CostsSummaryTable = 1;
	static final int CostsDetailTable = 2;

	static final int OptimalRenovationTable = 3;

	static final int BuildingStateChart = 2;
	static final int ValueValueChart = 3;
	static final int ValueCostChart = 4;
	static final int CostsAccumulatedChart = 5;
	static final int CostsDetailChart = 6;

	@Autowired
	private AsposeConfig asposeConfig;

	@Autowired
	private BuildingService buildingService;

	@Autowired
	RequestContext requestCtx;

	@Autowired
	PortfolioEvaluationService evaluationService;

	@Value("classpath:templates/Portfolio Evaluation Template.docx")
	Resource templateFile;

	@Value("classpath:templates/missing.jpg")
	Resource missingImage;

	ReportingEngine engine = new ReportingEngine();

	public DocumentGenerationServiceImpl() {
		this.engine.getKnownTypes().add(BuildingEvaluationResult.class);
	}

	@Override
	public void generateEvaluationReport(ObjPortfolio portfolio, ByteArrayOutputStream stream, int format) {

		PortfolioEvaluationResult evaluationResult = this.evaluationService.getEvaluation(portfolio);
		try {

			Document doc = new Document(this.templateFile.getInputStream());
			doc.setFontSettings(this.asposeConfig.getFontSettings());

			// this.insertLocationImage(doc, building);
			this.engine.buildReport(doc, evaluationResult, "portfolio");
			this.fillBuildingStateChart(doc, evaluationResult);
			this.fillValueChart(doc, evaluationResult);
			this.fillCostsChart(doc, evaluationResult);
			this.fillCostsTable(doc, evaluationResult);

			if (format == SaveFormat.PDF) {
				PdfSaveOptions saveOptions = new PdfSaveOptions();
				saveOptions.setSaveFormat(format);
				// Create encryption details and set user password = 1
				int year = Calendar.getInstance().get(Calendar.YEAR);
				PdfEncryptionDetails encryptionDetails = new PdfEncryptionDetails(null, "zeit" + year + "wert");
				// Disallow all
				encryptionDetails.setPermissions(PdfPermissions.DISALLOW_ALL);
				// Allow printing
				encryptionDetails.setPermissions(PdfPermissions.PRINTING);
				saveOptions.setEncryptionDetails(encryptionDetails);
				doc.save(stream, saveOptions);
			} else {
				doc.save(stream, format);
			}

		} catch (Exception x) {
			System.err.println("Document generation crashed");
			x.printStackTrace();
			throw new RuntimeException("Document generation crashed", x);
		}

	}

	private void insertLocationImage(Document doc, ObjBuilding building) throws IOException {

		byte[] imageContent;
		if (building.getGeoCoordinates() == null || "".equals(building.getGeoCoordinates())) {
			imageContent = this.missingImage.getInputStream().readAllBytes();
		} else {
			String address = building.getGeoCoordinates().substring(4);
			ImageResult ir = this.buildingService.getMap(building.getName(), address, new Size(1200, 1200),
					building.getGeoZoom());
			if (ir == null) {
				imageContent = this.missingImage.getInputStream().readAllBytes();
			} else {
				imageContent = ir.imageData;
			}
		}

		DocumentBuilder builder = new DocumentBuilder(doc);
		try {
			builder.moveToBookmark(CoverfotoBookmark);
			builder.insertImage(imageContent, 360, 360);
		} catch (Exception e) {
			throw new RuntimeException("Could not insert location image", e);
		}

	}

	private void fillBuildingStateChart(Document doc, PortfolioEvaluationResult evaluationResult) {

		List<EvaluationBuilding> buildings = evaluationResult.getBuildings();
		buildings.sort((a, b) -> b.getCondition() - a.getCondition());
		List<EvaluationBuilding> goodBuildings = buildings.stream().filter(b -> b.getCondition() >= 85).toList();
		List<EvaluationBuilding> okBuildings = buildings.stream()
				.filter(b -> b.getCondition() >= 70 && b.getCondition() < 85).toList();
		List<EvaluationBuilding> badBuildings = buildings.stream().filter(b -> b.getCondition() < 70).toList();

		double[] goodOriginalValues = new double[4 * goodBuildings.size() + 2];
		double[] goodTimeValues = new double[4 * goodBuildings.size() + 2];
		double[] okOriginalValues = new double[4 * okBuildings.size() + 2];
		double[] okTimeValues = new double[4 * okBuildings.size() + 2];
		double[] badOriginalValues = new double[4 * badBuildings.size() + 2];
		double[] badTimeValues = new double[4 * badBuildings.size() + 2];

		double totalValue = buildings.stream().map(b -> b.getInsuredValue()).reduce(0, (a, b) -> a + b);

		int cumulatedValue = 0;
		int index = 0;
		for (EvaluationBuilding bldg : goodBuildings) {
			goodOriginalValues[index] = cumulatedValue;
			goodTimeValues[index] = bldg.getCondition();
			cumulatedValue += bldg.getInsuredValue();
			goodOriginalValues[index + 1] = cumulatedValue - 10;
			goodTimeValues[index + 1] = bldg.getCondition();
			goodOriginalValues[index + 2] = cumulatedValue - 10;
			goodTimeValues[index + 2] = 0;
			goodOriginalValues[index + 3] = cumulatedValue;
			goodTimeValues[index + 3] = 0;
			index += 4;
		}
		goodOriginalValues[index] = cumulatedValue;
		goodTimeValues[index] = 0;

		okOriginalValues[0] = 0;
		okTimeValues[0] = 0;
		okOriginalValues[1] = cumulatedValue;
		okTimeValues[1] = 0;
		index = 2;
		for (EvaluationBuilding bldg : okBuildings) {
			okOriginalValues[index] = cumulatedValue;
			okTimeValues[index] = bldg.getCondition();
			cumulatedValue += bldg.getInsuredValue();
			okOriginalValues[index + 1] = cumulatedValue - 10;
			okTimeValues[index + 1] = bldg.getCondition();
			okOriginalValues[index + 2] = cumulatedValue - 10;
			okTimeValues[index + 2] = 0;
			okOriginalValues[index + 3] = cumulatedValue;
			okTimeValues[index + 3] = 0;
			index += 4;
		}

		badOriginalValues[0] = cumulatedValue;
		badTimeValues[0] = 0;
		badOriginalValues[1] = cumulatedValue;
		badTimeValues[1] = 0;
		index = 1;
		for (EvaluationBuilding bldg : badBuildings) {
			badOriginalValues[index] = cumulatedValue;
			badTimeValues[index] = bldg.getCondition();
			cumulatedValue += bldg.getInsuredValue();
			badOriginalValues[index + 1] = cumulatedValue - 10;
			badTimeValues[index + 1] = bldg.getCondition();
			badOriginalValues[index + 2] = cumulatedValue - 10;
			badTimeValues[index + 2] = 0;
			badOriginalValues[index + 3] = cumulatedValue;
			badTimeValues[index + 3] = 0;
			index += 4;
		}

		goodOriginalValues[4 * goodBuildings.size() + 1] = cumulatedValue;
		goodTimeValues[4 * goodBuildings.size() + 1] = 0;

		Shape valueChartShape = (Shape) doc.getChild(NodeType.SHAPE, BuildingStateChart, true);
		Chart valueChart = valueChartShape.getChart();
		valueChart.getSeries().clear();
		ChartSeries goodSeries = valueChart.getSeries().add("Z/N 100", goodOriginalValues, goodTimeValues);
		ChartSeries okSeries = valueChart.getSeries().add("Z/N 100", okOriginalValues, okTimeValues);
		ChartSeries badSeries = valueChart.getSeries().add("Z/N 100", badOriginalValues, badTimeValues);
		valueChart.getAxisX().setCategoryType(2);
		goodSeries.getFormat().getFill().setForeColor(PortfolioEvaluationServiceImpl.GOOD_CONDITION);
		okSeries.getFormat().getFill().setForeColor(PortfolioEvaluationServiceImpl.OK_CONDITION);
		badSeries.getFormat().getFill().setForeColor(PortfolioEvaluationServiceImpl.BAD_CONDITION);

	}

	private void fillValueChart(Document doc, PortfolioEvaluationResult evaluationResult) {

		int periodCount = (int) evaluationResult.getPeriods().stream().filter(p -> p.getYear() != null).count();
		String[] years = new String[periodCount];
		double[] originalValues = new double[periodCount];
		double[] timeValues = new double[periodCount];
		double[] maintenanceCosts = new double[periodCount];
		double[] restorationCosts = new double[periodCount];

		int index = 0;
		for (EvaluationPeriod ep : evaluationResult.getPeriods()) {
			if (ep.getYear() != null && ep.getYear() > 0) { // only yearly summary records
				years[index] = ep.getYear().toString();
				originalValues[index] = ep.getOriginalValue();
				timeValues[index] = ep.getTimeValue();
				maintenanceCosts[index] = ep.getMaintenanceCosts();
				restorationCosts[index] = ep.getRestorationCosts();
				index += 1;
			}
		}

		Shape valueChartShape = (Shape) doc.getChild(NodeType.SHAPE, ValueValueChart, true);
		Chart valueChart = valueChartShape.getChart();
		valueChart.getSeries().clear();
		valueChart.getSeries().add("Neuwert (indexiert)", years, originalValues);
		valueChart.getSeries().add("Zeitwert", years, timeValues);

		Arrays.sort(timeValues);
		double minValue = timeValues[0];
		valueChart.getAxisY().getScaling().setMinimum(new AxisBound(minValue));
		valueChart.getSeries().get(0).getMarker().setSymbol(MarkerSymbol.CIRCLE);
		valueChart.getSeries().get(0).getMarker().setSize(5);

		valueChart.getSeries().get(1).getMarker().setSymbol(MarkerSymbol.CIRCLE);
		valueChart.getSeries().get(1).getMarker().setSize(5);

		Shape costChartShape = (Shape) doc.getChild(NodeType.SHAPE, ValueCostChart, true);
		Chart costChart = costChartShape.getChart();
		costChart.getSeries().clear();
		costChart.getSeries().add("Instandhaltung", years, maintenanceCosts);
		costChart.getSeries().add("Instandsetzung", years, restorationCosts);

	}

	private void fillCostsChart(Document doc, PortfolioEvaluationResult evaluationResult) {

		int periodCount = (int) evaluationResult.getPeriods().stream().filter(p -> p.getYear() != null).count();
		String[] years = new String[periodCount];
		double[] timeValues = new double[periodCount];
		double[] cumMaintenanceCosts = new double[periodCount];
		double[] cumTotalCosts = new double[periodCount];
		double[] maintenanceCosts = new double[periodCount];
		double[] restorationCosts = new double[periodCount];

		int index = 0;
		for (EvaluationPeriod ep : evaluationResult.getPeriods()) {
			if (ep.getYear() != null && ep.getYear() > 0) { // only yearly summary records
				years[index] = ep.getYear().toString();
				timeValues[index] = ep.getTimeValue();
				cumMaintenanceCosts[index] = index > 0
						? cumMaintenanceCosts[index - 1] + ep.getMaintenanceCosts()
						: ep.getMaintenanceCosts();
				cumTotalCosts[index] = index > 0
						? cumTotalCosts[index - 1] + ep.getMaintenanceCosts() + ep.getRestorationCosts()
						: ep.getMaintenanceCosts() + ep.getRestorationCosts();
				maintenanceCosts[index] = ep.getMaintenanceCosts();
				restorationCosts[index] = ep.getRestorationCosts();
				index += 1;
			}
		}

		Shape valueChartShape = (Shape) doc.getChild(NodeType.SHAPE, CostsAccumulatedChart, true);
		Chart valueChart = valueChartShape.getChart();
		valueChart.getSeries().clear();
		valueChart.getSeries().add("Kumulierter Zeitwert", years, timeValues);
		valueChart.getSeries().add("Kumulierte IH Kosten", years, cumMaintenanceCosts);
		valueChart.getSeries().add("Kumulierte IH+IS Kosten", years, cumTotalCosts);

		valueChart.getAxisY().getScaling().setMinimum(new AxisBound(0));
		valueChart.getSeries().get(0).getMarker().setSymbol(MarkerSymbol.CIRCLE);
		valueChart.getSeries().get(0).getMarker().setSize(5);

		valueChart.getSeries().get(1).getMarker().setSymbol(MarkerSymbol.CIRCLE);
		valueChart.getSeries().get(1).getMarker().setSize(5);

		valueChart.getSeries().get(2).getMarker().setSymbol(MarkerSymbol.CIRCLE);
		valueChart.getSeries().get(2).getMarker().setSize(5);

		Shape costChartShape = (Shape) doc.getChild(NodeType.SHAPE, CostsDetailChart, true);
		Chart costChart = costChartShape.getChart();
		costChart.getSeries().clear();
		costChart.getSeries().add("Instandhaltung", years, maintenanceCosts);
		costChart.getSeries().add("Instandsetzung", years, restorationCosts);

	}

	private void fillCostsTable(Document doc, PortfolioEvaluationResult evaluationResult) {

		Table costsTable = (Table) doc.getChild(NodeType.TABLE, CostsDetailTable, true);
		DocumentBuilder builder = new DocumentBuilder(doc);
		Formatter fmt = Formatter.INSTANCE;

		for (EvaluationPeriod ep : evaluationResult.getPeriods()) {
			Row row = this.addCostsTableRow(costsTable);
			Cell cell = row.getFirstCell();
			if (ep.getYear() != null) { // only yearly summary records
				// year
				builder.moveTo(cell.getFirstParagraph());
				builder.write("" + ep.getYear());
				// originalValue
				cell = (Cell) cell.getNextSibling();
				builder.moveTo(cell.getFirstParagraph());
				builder.write(fmt.formatNumber(ep.getOriginalValue()));
				// timeValue
				cell = (Cell) cell.getNextSibling();
				builder.moveTo(cell.getFirstParagraph());
				builder.write(fmt.formatNumber(ep.getTimeValue()));
				// maintenanceCosts
				cell = (Cell) cell.getNextSibling();
				builder.moveTo(cell.getFirstParagraph());
				builder.write(fmt.formatNumber(ep.getMaintenanceCosts()));
			} else {
				cell = (Cell) cell.getNextSibling().getNextSibling().getNextSibling();
			}
			// restorationCosts
			cell = (Cell) cell.getNextSibling();
			if (ep.getRestorationCosts() != null && ep.getRestorationCosts() > 0) {
				builder.moveTo(cell.getFirstParagraph());
				builder.write(fmt.formatNumber(ep.getRestorationCosts()));
			}
			// restorationElement
			cell = (Cell) cell.getNextSibling();
			builder.moveTo(cell.getFirstParagraph());
			builder.write(ep.getRestorationBuilding());
			cell = (Cell) cell.getNextSibling();
			builder.moveTo(cell.getFirstParagraph());
			builder.write(ep.getRestorationElement());
			if (ep.getYear() != null) { // only yearly summary records
				// totalCosts
				cell = (Cell) cell.getNextSibling();
				builder.moveTo(cell.getFirstParagraph());
				builder.write(fmt.formatNumber(ep.getTotalCosts()));
				// aggrCosts
				cell = (Cell) cell.getNextSibling();
				builder.moveTo(cell.getFirstParagraph());
				builder.write(fmt.formatNumber(ep.getAggrCosts()));
			}
		}

		costsTable.getFirstRow().getNextSibling().remove();

	}

	private Row addCostsTableRow(Table table) {
		Row templateRow = (Row) table.getFirstRow().getNextSibling();
		Row clonedRow = (Row) templateRow.deepClone(true);
		table.appendChild(clonedRow);
		return clonedRow;
	}

	private Cell getNthNextSibling(Node cell, int n) {
		if (n >= 0) {
			return this.getNthNextSibling(cell.getNextSibling(), n - 1);
		}
		return (Cell) cell;
	}

}
