
package io.zeitwert.fm.building.adapter.api.rest;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import com.aspose.words.AxisBound;
import com.aspose.words.Cell;
import com.aspose.words.Chart;
import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.License;
import com.aspose.words.MarkerSymbol;
import com.aspose.words.Node;
import com.aspose.words.NodeType;
import com.aspose.words.RelativeHorizontalPosition;
import com.aspose.words.RelativeVerticalPosition;
import com.aspose.words.ReportingEngine;
import com.aspose.words.Row;
import com.aspose.words.Run;
import com.aspose.words.SaveFormat;
import com.aspose.words.Shape;
import com.aspose.words.Table;
import com.aspose.words.WrapType;
import com.google.maps.ImageResult;
import com.google.maps.model.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.building.service.api.BuildingService;
import io.zeitwert.fm.building.service.api.EvaluationService;
import io.zeitwert.fm.building.service.api.dto.BuildingEvaluationResult;
import io.zeitwert.fm.building.service.api.dto.EvaluationElement;
import io.zeitwert.fm.building.service.api.dto.EvaluationPeriod;
import io.zeitwert.fm.common.service.api.Formatter;

import javax.annotation.PostConstruct;

@RestController("buildingEvaluationController")
@RequestMapping("/evaluation/building/buildings")
public class BuildingEvaluationController {

	private static final int CoverFotoWidth = 400;
	private static final int CoverFotoHeight = 230;

	// private static final String OPT_IS_MARKER = "\u058D";
	private static final String OptimumRenovationMarker = "X";

	@Autowired
	private ObjBuildingRepository repo;

	@Autowired
	private BuildingService buildingService;

	@Autowired
	SessionInfo sessionInfo;

	@Autowired
	EvaluationService evaluationService;

	@Value("classpath:license/Aspose.Words.Java.lic")
	Resource licenseFile;

	@Value("classpath:templates/Building Evaluation Template.docx")
	Resource templateFile;

	ReportingEngine engine = new ReportingEngine();

	@PostConstruct
	protected void initLicense() throws Exception {

		License lic = new License();
		lic.setLicense(licenseFile.getInputStream());

		ReportingEngine.setUseReflectionOptimization(false);
		engine.getKnownTypes().add(BuildingEvaluationResult.class);

	}

	@GetMapping("/{id}")
	protected ResponseEntity<byte[]> exportBuilding(@PathVariable("id") Integer id,
			@RequestParam(required = false, name = "format") String format)
			throws Exception {

		ObjBuilding building = this.repo.get(sessionInfo, id);

		if (building.getCoverFoto() == null || building.getCoverFoto().getContentType() == null) {
			return ResponseEntity.badRequest().body("Coverfoto missing".getBytes(StandardCharsets.UTF_8));
		} else if (building.getGeoCoordinates() == null) {
			return ResponseEntity.badRequest().body("Coordinates missing".getBytes(StandardCharsets.UTF_8));
		}

		BuildingEvaluationResult evaluationResult = evaluationService.getEvaluation(building);

		Document doc = new Document(templateFile.getInputStream());

		this.insertCoverFoto(doc, building);
		this.insertLocationImage(doc, building);
		engine.buildReport(doc, evaluationResult, "building");
		this.fillRenovationTable(doc, evaluationResult);
		this.fillCostsChart(doc, evaluationResult);

		int saveFormat = this.getSaveFormat(format);
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		doc.save(outStream, saveFormat);

		String fileName = this.getFileName(building, saveFormat);
		ResponseEntity<byte[]> response = ResponseEntity.ok()
				.header("Content-Disposition", "attachment; filename=\"" + fileName + "\"") // mark file for download
				.body(outStream.toByteArray());

		return response;

	}

	private void insertCoverFoto(Document doc, ObjBuilding building) {

		if (building.getCoverFoto() == null || building.getCoverFoto().getContentType() == null) {
			return;
		}

		// String contentType = building.getCoverFoto().getContentType().getExtension();
		byte[] content = building.getCoverFoto().getContent();

		DocumentBuilder builder = new DocumentBuilder(doc);
		try {
			builder.moveToBookmark("CoverFoto");
			Shape coverFoto = builder.insertImage(content);
			coverFoto.setAspectRatioLocked(true);
			// adjust either width or height
			if (coverFoto.getWidth() / coverFoto.getHeight() > ((double) CoverFotoWidth) / ((double) CoverFotoHeight)) {
				coverFoto.setWidth(CoverFotoWidth);
			} else {
				coverFoto.setHeight(CoverFotoHeight);
			}
			coverFoto.setWrapType(WrapType.NONE);
			coverFoto.setRelativeHorizontalPosition(RelativeHorizontalPosition.RIGHT_MARGIN);
			coverFoto.setRelativeVerticalPosition(RelativeVerticalPosition.TOP_MARGIN);
			coverFoto.setTop(170);
			coverFoto.setLeft(-coverFoto.getWidth() - 20);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void insertLocationImage(Document doc, ObjBuilding building) {

		if (building.getGeoCoordinates() == null) {
			return;
		}

		String address = building.getGeoCoordinates().substring(4);
		ImageResult ir = buildingService.getMap(building.getName(), address, new Size(1200, 1200), building.getGeoZoom());

		if (ir == null) {
			return;
		}

		DocumentBuilder builder = new DocumentBuilder(doc);
		try {
			builder.moveToBookmark("Location");
			builder.insertImage(ir.imageData, 360, 360);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void fillRenovationTable(Document doc, BuildingEvaluationResult evaluationResult) {

		Table optRenovationTable = (Table) doc.getChild(NodeType.TABLE, 3, true);

		Cell yearCell = ((Row) optRenovationTable.getFirstRow().getNextSibling()).getFirstCell();
		for (int i = 0; i < 25; i++) {
			yearCell = (Cell) yearCell.getNextSibling();
			yearCell.getFirstParagraph().appendChild(new Run(doc, Integer.toString(evaluationResult.getStartYear() + i)));
		}

		for (EvaluationElement e : evaluationResult.getElements()) {
			if (e.getValuePart() > 0) {
				Row row = addRenovationTableRow(optRenovationTable);
				Cell cell = row.getFirstCell();
				cell.getFirstParagraph().appendChild(new Run(doc, e.getName()));
				Integer restorationYear = e.getRestorationYear();
				if (restorationYear != null) {
					Integer delta = (int) Math.max(0, restorationYear - evaluationResult.getStartYear());
					cell = getNthNextSibling(row.getFirstCell(), delta);
					cell.getFirstParagraph().appendChild(new Run(doc, OptimumRenovationMarker));
					cell = row.getLastCell();
					String costs = Formatter.INSTANCE.formatMonetaryValue(e.getRestorationCosts(), "CHF");
					cell.getFirstParagraph().appendChild(new Run(doc, costs));
					cell = (Cell) cell.getPreviousSibling();
					cell.getFirstParagraph().appendChild(new Run(doc, Integer.toString(restorationYear)));
				}
			}
		}
		optRenovationTable.getFirstRow().getNextSibling().getNextSibling().remove();

	}

	private void fillCostsChart(Document doc, BuildingEvaluationResult evaluationResult) {

		String[] years = new String[evaluationResult.getPeriods().size()];
		double[] originalValues = new double[evaluationResult.getPeriods().size()];
		double[] timeValues = new double[evaluationResult.getPeriods().size()];
		double[] maintenanceCosts = new double[evaluationResult.getPeriods().size()];
		double[] restorationCosts = new double[evaluationResult.getPeriods().size()];

		for (int i = 0; i < evaluationResult.getPeriods().size(); i++) {
			EvaluationPeriod ep = evaluationResult.getPeriods().get(i);
			years[i] = ep.getYear().toString();
			originalValues[i] = ep.getOriginalValue();
			timeValues[i] = ep.getTimeValue();
			maintenanceCosts[i] = ep.getMaintenanceCosts();
			restorationCosts[i] = ep.getRestorationCosts();
		}

		Shape valueChartShape = (Shape) doc.getChild(NodeType.SHAPE, 5, true);
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

		Shape costChartShape = (Shape) doc.getChild(NodeType.SHAPE, 6, true);
		Chart costChart = costChartShape.getChart();
		costChart.getSeries().clear();
		costChart.getSeries().add("Instandhaltung", years, maintenanceCosts);
		costChart.getSeries().add("Instandsetzung", years, restorationCosts);

		costChart.getSeries().get(0).getMarker().setSymbol(MarkerSymbol.CIRCLE);
		costChart.getSeries().get(0).getMarker().setSize(5);

		costChart.getSeries().get(1).getMarker().setSymbol(MarkerSymbol.CIRCLE);
		costChart.getSeries().get(1).getMarker().setSize(5);

	}

	private Row addRenovationTableRow(Table table) {
		Row templateRow = (Row) table.getFirstRow().getNextSibling().getNextSibling();
		Row clonedRow = (Row) templateRow.deepClone(true);
		table.appendChild(clonedRow);
		return clonedRow;
	}

	private Cell getNthNextSibling(Node cell, int n) {
		if (n >= 0) {
			return getNthNextSibling(cell.getNextSibling(), n - 1);
		}
		return (Cell) cell;
	}

	private int getSaveFormat(String format) {
		return format != null && "docx".equals(format) ? SaveFormat.DOCX : SaveFormat.PDF;
	}

	private String getFileName(ObjBuilding building, int saveFormat) {
		return (building.getAccount() != null ? building.getAccount().getName() + " " : "") + building.getName()
				+ (saveFormat == SaveFormat.DOCX ? ".docx" : ".pdf");
	}

}
