
package io.zeitwert.fm.building.adapter.api.rest;

import java.io.ByteArrayOutputStream;

import com.aspose.words.AxisBound;
import com.aspose.words.Cell;
import com.aspose.words.Chart;
import com.aspose.words.Document;
import com.aspose.words.License;
import com.aspose.words.MarkerSymbol;
import com.aspose.words.Node;
import com.aspose.words.NodeType;
import com.aspose.words.ReportingEngine;
import com.aspose.words.Row;
import com.aspose.words.Run;
import com.aspose.words.SaveFormat;
import com.aspose.words.Shape;
import com.aspose.words.Table;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.building.service.api.EvaluationService;
import io.zeitwert.fm.building.service.api.dto.BuildingEvaluationResult;
import io.zeitwert.fm.building.service.api.dto.EvaluationElement;
import io.zeitwert.fm.building.service.api.dto.EvaluationPeriod;

@RestController("buildingEvaluationController")
@RequestMapping("/evaluation/building/buildings")
public class BuildingEvaluationController {

	private static final int SAVE_FORMAT = SaveFormat.PDF;
	@Autowired
	private ObjBuildingRepository repo;

	@Autowired
	SessionInfo sessionInfo;

	@Autowired
	EvaluationService evaluationService;

	@Value("classpath:license/Aspose.Words.Java.lic")
	Resource licenseFile;

	@Value("classpath:templates/Building Evaluation Template.docx")
	Resource templateFile;

	@GetMapping("/{id}")
	protected ResponseEntity<byte[]> exportBuilding(@PathVariable("id") Integer id)
			throws Exception {

		License lic = new License();
		lic.setLicense(licenseFile.getInputStream());

		ObjBuilding building = this.repo.get(sessionInfo, id);
		BuildingEvaluationResult evaluationResult = evaluationService.getEvaluation(building);

		ReportingEngine.setUseReflectionOptimization(false);
		ReportingEngine engine = new ReportingEngine();
		engine.getKnownTypes().add(BuildingEvaluationResult.class);

		Document doc = new Document(templateFile.getFile().getAbsolutePath());
		engine.buildReport(doc, evaluationResult, "building");

		Table optRenovationTable = (Table) doc.getChild(NodeType.TABLE, 3, true);
		for (int i = 0; i < 25; i++) {
			String plh = "[[t" + String.format("%02d", i) + "]]";
			optRenovationTable.getFirstRow().getRange().replace(plh, Integer.toString(evaluationResult.getStartYear() + i));
		}

		for (EvaluationElement e : evaluationResult.getElements()) {
			if (e.getValuePart() > 0) {
				Row row = addRow(optRenovationTable);
				Cell cell = row.getFirstCell();
				cell.getFirstParagraph().appendChild(new Run(doc, e.getName()));
				Integer restorationYear = e.getRestorationYear();
				if (restorationYear != null) {
					Integer delta = (int) Math.max(0, restorationYear - evaluationResult.getStartYear());
					cell = getRenovationCell(row.getFirstCell(), delta);
					cell.getFirstParagraph().appendChild(new Run(doc, "\u058D"));
					cell = row.getLastCell();
					cell.getFirstParagraph().appendChild(new Run(doc, "CHF " + e.getRestorationCosts()));
				}
				// if all content was deleted, would have to add paragraph first
				// cell = row.getLastCell();
				// cell.appendChild(new Paragraph(doc));
				// cell.getFirstParagraph().appendChild(new Run(doc,
				// Integer.toString(evaluationResult.getStartYear() + delta)));
			}
		}
		optRenovationTable.getFirstRow().getNextSibling().remove();
		// optRenovationTable.getLastRow().remove();

		// NodeCollection<Shape> shapes = doc.getChildNodes(NodeType.SHAPE, true);
		// for (Shape s : shapes) {
		// System.out.println("Shape " + s.getNodeType() + ", " + s.getName() + ", " +
		// s.getLeft());
		// }
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
		valueChart.getSeries().add("Neuwert (indexiert, kCHF)", years, originalValues);
		valueChart.getSeries().add("Zeitwert (kCHF)", years, timeValues);

		valueChart.getAxisY().getScaling().setMinimum(new AxisBound());
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

		// ChartSeriesCollection seriesCollection = valueChart.getSeries();
		// for (ChartSeries s : seriesCollection) {
		// System.out.println(s.getName() + " " + s.getDataLabels().getCount() + " " +
		// s.getDataPoints().getCount());
		// for (ChartDataLabel d : s.getDataLabels()) {
		// System.out.println(d.getIndex() + " " + d.toString());
		// }
		// for (ChartDataPoint d : s.getDataPoints()) {
		// System.out.println(d.getIndex() + " " + d.toString());
		// }
		// }

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		doc.save(outStream, SAVE_FORMAT);

		String fileName = this.getFileName(building);
		ResponseEntity<byte[]> response = ResponseEntity.ok()
				.header("Content-Disposition", "attachment; filename=\"" + fileName + "\"") // mark file for download
				.body(outStream.toByteArray());
		return response;
	}

	private Row addRow(Table table) {
		Row firstRow = (Row) table.getFirstRow().getNextSibling();
		Row clonedRow = (Row) firstRow.deepClone(true);
		// for (Cell cell : clonedRow.getCells()) {
		// cell.removeAllChildren();
		// }
		table.appendChild(clonedRow);
		return clonedRow;
	}

	private Cell getRenovationCell(Node cell, int year) {
		if (year >= 0) {
			return getRenovationCell(cell.getNextSibling(), year - 1);
		}
		return (Cell) cell;
	}

	private String getFileName(ObjBuilding building) {
		return (building.getAccount() != null ? building.getAccount().getName() + " " : "") + building.getName()
				+ (SAVE_FORMAT == SaveFormat.DOCX ? ".docx" : ".pdf");
	}

}
