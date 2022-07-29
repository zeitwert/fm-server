
package io.zeitwert.fm.building.adapter.api.rest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.words.AxisBound;
import com.aspose.words.Cell;
import com.aspose.words.Chart;
import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.FolderFontSource;
import com.aspose.words.FontSettings;
import com.aspose.words.License;
import com.aspose.words.MarkerSymbol;
import com.aspose.words.Node;
import com.aspose.words.NodeType;
import com.aspose.words.PdfEncryptionDetails;
import com.aspose.words.PdfPermissions;
import com.aspose.words.PdfSaveOptions;
import com.aspose.words.PhysicalFontInfo;
import com.aspose.words.RelativeHorizontalPosition;
import com.aspose.words.RelativeVerticalPosition;
import com.aspose.words.ReportingEngine;
import com.aspose.words.Row;
import com.aspose.words.Run;
import com.aspose.words.SaveFormat;
import com.aspose.words.Shape;
import com.aspose.words.ShapeType;
import com.aspose.words.Table;
import com.aspose.words.WrapType;
import com.google.maps.ImageResult;
import com.google.maps.model.Size;

import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.ddd.util.Formatter;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.building.service.api.BuildingService;
import io.zeitwert.fm.building.service.api.EvaluationService;
import io.zeitwert.fm.building.service.api.dto.BuildingEvaluationResult;
import io.zeitwert.fm.building.service.api.dto.EvaluationElement;
import io.zeitwert.fm.building.service.api.dto.EvaluationPeriod;

@RestController("buildingEvaluationController")
@RequestMapping("/rest/building/buildings")
public class BuildingEvaluationController {

	private static final double POINTS_PER_MM = 2.834647454889553;

	private static final int CoverFotoWidth = 400;
	private static final int CoverFotoHeight = 230;

	private static final String OptimumRenovationMarker = Character.toString((char) 110);

	static final String CoverfotoBookmark = "CoverFoto";
	static final String LocationBookmark = "Location";
	static final String OnePagerBookmark = "OnePager";

	static final int BasicDataTable = 0;
	static final int EvaluationTable = 1;
	static final int ElementTable = 2;
	static final int OptimalRenovationTable = 3;
	static final int CostsTable = 4;
	static final int OnePageDetailsTable = 5;
	static final int OnePageBasicDataTable = 6;
	static final int OnePageEvaluationTable = 7;

	private Logger logger = LoggerFactory.getLogger(BuildingEvaluationController.class);

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

	@Value("classpath:templates/missing.jpg")
	Resource missingImage;

	ClassLoader classLoader = this.getClass().getClassLoader();

	File fontsDirectory;

	FontSettings fontSettings;

	ReportingEngine engine = new ReportingEngine();

	@PostConstruct
	protected void initLicense() throws Exception {

		License lic = new License();
		lic.setLicense(licenseFile.getInputStream());

		ReportingEngine.setUseReflectionOptimization(false);
		engine.getKnownTypes().add(BuildingEvaluationResult.class);

	}

	@PostConstruct
	protected void initFonts() throws Exception {

		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		this.fontsDirectory = new File(tmpDir, "fonts");
		if (!this.fontsDirectory.exists()) {
			this.fontsDirectory.mkdirs();
		}
		logger.info("initFonts: " + this.fontsDirectory.getAbsolutePath());

		this.copyStream2File("trebuc");
		this.copyStream2File("trebucbd");
		this.copyStream2File("trebucbi");
		this.copyStream2File("trabucit");
		this.copyStream2File("webdings");
		this.copyStream2File("wingding");

		this.fontSettings = new FontSettings();
		this.fontSettings.setFontsFolder(this.fontsDirectory.getAbsolutePath(), false);

		listFonts();
	}

	private void copyStream2File(String fontName) throws IOException {
		InputStream is = classLoader.getResourceAsStream("fonts/" + fontName + ".ttf");
		if (is != null) {
			File f = new File(this.fontsDirectory.getAbsolutePath() + "/" + fontName + ".ttf");
			f.deleteOnExit();
			try (FileOutputStream out = new FileOutputStream(f)) {
				IOUtils.copy(is, out);
			}
		}
	}

	private void listFonts() {
		// Get available fonts in folder
		for (PhysicalFontInfo fontInfo : (Iterable<PhysicalFontInfo>) new FolderFontSource(fontsDirectory.getAbsolutePath(),
				false)
				.getAvailableFonts()) {
			logger.info(
					"Font family: " + fontInfo.getFontFamilyName()
							+ ", version: " + fontInfo.getVersion()
							+ ", font: " + fontInfo.getFullFontName()
							+ ", path : " + fontInfo.getFilePath());
		}
	}

	@GetMapping("/{id}/evaluation")
	protected ResponseEntity<byte[]> exportBuilding(
			@PathVariable("id") Integer id,
			@RequestParam(required = false, name = "format") String format,
			@RequestParam(required = false, name = "inline") Boolean isInline) {

		ObjBuilding building = this.repo.get(sessionInfo, id);
		BuildingEvaluationResult evaluationResult = evaluationService.getEvaluation(building);

		try {

			Document doc = new Document(templateFile.getInputStream());
			doc.setFontSettings(this.fontSettings);

			this.insertCoverFoto(doc, building);
			this.insertLocationImage(doc, building);
			engine.buildReport(doc, evaluationResult, "building");
			this.fillOptRenovationTable(doc, evaluationResult);
			this.fillCostsChart(doc, evaluationResult);
			this.fillCostsTable(doc, evaluationResult);
			this.fillOnePagerCostsChart(doc, evaluationResult);
			this.fillOnePager(doc, evaluationResult);

			int saveFormat = this.getSaveFormat(format);
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();

			if (saveFormat == SaveFormat.PDF) {
				PdfSaveOptions saveOptions = new PdfSaveOptions();
				saveOptions.setSaveFormat(saveFormat);
				// Create encryption details and set user password = 1
				int year = Calendar.getInstance().get(Calendar.YEAR);
				PdfEncryptionDetails encryptionDetails = new PdfEncryptionDetails(null, "zeit" + year + "wert");
				// Disallow all
				encryptionDetails.setPermissions(PdfPermissions.DISALLOW_ALL);
				// Allow printing
				encryptionDetails.setPermissions(PdfPermissions.PRINTING);
				saveOptions.setEncryptionDetails(encryptionDetails);
				doc.save(outStream, saveOptions);
			} else {
				doc.save(outStream, saveFormat);
			}

			String fileName = this.getFileName(building, saveFormat);
			// mark file for download
			HttpHeaders headers = new HttpHeaders();
			if (isInline != null && isInline) {
				return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).headers(headers)
						.body(outStream.toByteArray());
			} else {
				ContentDisposition contentDisposition = ContentDisposition.builder("attachment").filename(fileName).build();
				headers.setContentDisposition(contentDisposition);
				return ResponseEntity.ok().headers(headers).body(outStream.toByteArray());
			}

		} catch (Exception x) {
			System.err.println("Document generation crashed");
			x.printStackTrace();
			throw new RuntimeException("Document generation crashed", x);
		}

	}

	private void insertCoverFoto(Document doc, ObjBuilding building) throws IOException {

		byte[] imageContent;
		if (building.getCoverFoto() == null || building.getCoverFoto().getContentType() == null) {
			imageContent = missingImage.getInputStream().readAllBytes();
		} else {
			imageContent = building.getCoverFoto().getContent();
		}

		DocumentBuilder builder = new DocumentBuilder(doc);
		try {
			builder.moveToBookmark(CoverfotoBookmark);
			Shape coverFoto = builder.insertImage(imageContent);
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

	private void insertLocationImage(Document doc, ObjBuilding building) throws IOException {

		byte[] imageContent;
		if (building.getGeoCoordinates() == null || building.getGeoCoordinates().equals("")) {
			imageContent = missingImage.getInputStream().readAllBytes();
		} else {
			String address = building.getGeoCoordinates().substring(4);
			ImageResult ir = buildingService.getMap(building.getName(), address, new Size(1200, 1200), building.getGeoZoom());
			if (ir == null) {
				imageContent = missingImage.getInputStream().readAllBytes();
			} else {
				imageContent = ir.imageData;
			}
		}

		DocumentBuilder builder = new DocumentBuilder(doc);
		try {
			builder.moveToBookmark(LocationBookmark);
			builder.insertImage(imageContent, 360, 360);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void fillOptRenovationTable(Document doc, BuildingEvaluationResult evaluationResult) {

		Table optRenovationTable = (Table) doc.getChild(NodeType.TABLE, OptimalRenovationTable, true);
		DocumentBuilder builder = new DocumentBuilder(doc);

		Cell yearCell = ((Row) optRenovationTable.getFirstRow().getNextSibling()).getFirstCell();
		for (int i = 0; i <= 25; i++) {
			yearCell = (Cell) yearCell.getNextSibling();
			builder.moveTo(yearCell.getFirstParagraph());
			builder.write("" + (evaluationResult.getStartYear() + i));
		}

		int totalRestorationCosts = 0;
		for (EvaluationElement e : evaluationResult.getElements()) {
			if (e.getValuePart() != null && e.getValuePart() > 0 && !e.getName().equals("Total")) {
				Row row = addRenovationTableRow(optRenovationTable);
				Cell cell = row.getFirstCell();
				cell.getFirstParagraph().appendChild(new Run(doc, e.getName()));
				Integer restorationYear = e.getRestorationYear();
				if (restorationYear != null) {
					Integer delta = (int) Math.max(0, restorationYear - evaluationResult.getStartYear());
					cell = getNthNextSibling(row.getFirstCell(), delta);
					builder.moveTo(cell.getFirstParagraph());
					builder.write(OptimumRenovationMarker);
					cell = row.getLastCell();
					String costs = Formatter.INSTANCE.formatNumber(e.getRestorationCosts());
					builder.moveTo(cell.getFirstParagraph());
					builder.write(costs);
					cell = (Cell) cell.getPreviousSibling();
					builder.moveTo(cell.getFirstParagraph());
					builder.write("" + restorationYear);
					totalRestorationCosts += e.getRestorationCosts() != null ? e.getRestorationCosts() : 0;
				}
			}
		}

		Row row = addRenovationTableRow(optRenovationTable);
		Cell cell = row.getFirstCell();
		builder.getFont().setBold(true);
		builder.moveTo(cell.getFirstParagraph());
		builder.write("Total");
		cell = row.getLastCell();
		String costs = Formatter.INSTANCE.formatNumber(totalRestorationCosts);
		builder.moveTo(cell.getFirstParagraph());
		builder.write(costs);

		optRenovationTable.getFirstRow().getNextSibling().getNextSibling().remove();

	}

	private Row addRenovationTableRow(Table table) {
		Row templateRow = (Row) table.getFirstRow().getNextSibling().getNextSibling();
		Row clonedRow = (Row) templateRow.deepClone(true);
		table.appendChild(clonedRow);
		return clonedRow;
	}

	private void fillCostsChart(Document doc, BuildingEvaluationResult evaluationResult) {

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

	}

	private void fillCostsTable(Document doc, BuildingEvaluationResult evaluationResult) {

		Table costsTable = (Table) doc.getChild(NodeType.TABLE, CostsTable, true);
		DocumentBuilder builder = new DocumentBuilder(doc);
		Formatter fmt = Formatter.INSTANCE;

		for (EvaluationPeriod ep : evaluationResult.getPeriods()) {
			Row row = addCostsTableRow(costsTable);
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

	private void fillOnePagerCostsChart(Document doc, BuildingEvaluationResult evaluationResult) {

		int periodCount = (int) evaluationResult.getPeriods().stream().filter(p -> p.getYear() != null).count();
		String[] years = new String[periodCount];
		double[] maintenanceCosts = new double[periodCount];
		double[] restorationCosts = new double[periodCount];

		int index = 0;
		for (EvaluationPeriod ep : evaluationResult.getPeriods()) {
			if (ep.getYear() != null && ep.getYear() > 0) { // only yearly summary records
				years[index] = ep.getYear().toString();
				maintenanceCosts[index] = ep.getMaintenanceCosts() / 1000.0;
				restorationCosts[index] = ep.getRestorationCosts() / 1000.0;
				index += 1;
			}
		}

		Shape costChartShape = (Shape) doc.getChild(NodeType.SHAPE, 9, true);
		Chart costChart = costChartShape.getChart();
		costChart.getSeries().clear();
		costChart.getSeries().add("Instandhaltung", years, maintenanceCosts);
		costChart.getSeries().add("Instandsetzung", years, restorationCosts);

	}

	private void fillOnePager(Document doc, BuildingEvaluationResult evaluationResult) throws Exception {

		Table onePagerDetailsTable = (Table) doc.getChild(NodeType.TABLE, OnePageDetailsTable, true);
		DocumentBuilder builder = new DocumentBuilder(doc);
		DocumentBuilder shapeBuilder = new DocumentBuilder(doc);
		shapeBuilder.moveToBookmark(OnePagerBookmark);

		Cell yearCell = onePagerDetailsTable.getLastRow().getFirstCell();
		yearCell = getNthNextSibling(yearCell, 8);
		for (int i = 0; i <= 25; i++) {
			yearCell = (Cell) yearCell.getNextSibling();
			builder.moveTo(yearCell.getFirstParagraph());
			builder.write("" + (evaluationResult.getStartYear() + i));
		}

		{

			EvaluationElement titleElement = evaluationResult.getElements().get(evaluationResult.getElements().size() - 1);
			Row titleRow = (Row) onePagerDetailsTable.getFirstRow().getNextSibling();
			Cell titleCell = titleRow.getFirstCell();

			Shape shape = shapeBuilder.insertShape(ShapeType.FLOW_CHART_CONNECTOR, 8, 8);
			shape.setStroked(false);
			shape.setFillColor(titleElement.getRatingColor());
			shape.setWrapType(WrapType.NONE);
			shape.setAllowOverlap(true);
			shape.setRelativeHorizontalPosition(RelativeHorizontalPosition.PAGE);
			shape.setRelativeVerticalPosition(RelativeVerticalPosition.PAGE);
			shape.setTop(this.getRatingLineVOffset(0));
			shape.setLeft(this.getRatingHOffset(titleElement.getRating()));

			titleCell = getNthNextSibling(titleRow.getFirstCell(), 13);
			builder.moveTo(titleCell.getFirstParagraph());
			builder.write("" + titleElement.getRating());

		}

		List<EvaluationElement> elements = evaluationResult.getElements().stream()
				.filter(e -> !e.getName().equals("Total"))
				.filter(e -> e.getValuePart() != null && e.getValuePart() > 0)
				.toList();

		Integer maxValuePart = elements.stream()
				.map(a -> a.getValuePart())
				.reduce(0, (a, b) -> Math.max(a, b));

		int lineNr = 1;
		for (EvaluationElement e : elements) {

			Row row = addOnePageTableRow(onePagerDetailsTable);

			Cell cell = row.getFirstCell();
			builder.moveTo(cell.getFirstParagraph());
			builder.write(e.getName());

			cell = (Cell) cell.getNextSibling();
			int valuePart = (int) Math.round(76.0 * e.getValuePart() / maxValuePart);
			builder.moveTo(cell.getFirstParagraph());
			builder.write(new String(new char[valuePart]).replace('\0', 'I'));

			String valuePartPC = Formatter.INSTANCE.formatValueWithUnit(e.getValuePart(), "%");
			cell = (Cell) cell.getNextSibling();
			builder.moveTo(cell.getFirstParagraph());
			builder.write(valuePartPC);

			Shape shape = shapeBuilder.insertShape(ShapeType.FLOW_CHART_CONNECTOR, 8, 8);
			shape.setStroked(false);
			shape.setFillColor(e.getRatingColor());
			shape.setWrapType(WrapType.NONE);
			shape.setAllowOverlap(true);
			shape.setRelativeHorizontalPosition(RelativeHorizontalPosition.PAGE);
			shape.setRelativeVerticalPosition(RelativeVerticalPosition.PAGE);
			shape.setTop(this.getRatingLineVOffset(lineNr));
			shape.setLeft(this.getRatingHOffset(e.getRating()));

			cell = getNthNextSibling(row.getFirstCell(), 13);
			builder.moveTo(cell.getFirstParagraph());
			builder.write("" + e.getRating());

			Integer restorationYear = e.getRestorationYear();
			if (restorationYear != null) {
				Integer delta = 15 + (int) Math.max(0, restorationYear - evaluationResult.getStartYear());
				cell = getNthNextSibling(row.getFirstCell(), delta);
				builder.moveTo(cell.getFirstParagraph());
				builder.write(OptimumRenovationMarker);
			}

			lineNr++;
		}

		onePagerDetailsTable.getFirstRow().getNextSibling().getNextSibling().remove();

	}

	private Row addOnePageTableRow(Table table) {
		Row templateRow = (Row) table.getFirstRow().getNextSibling().getNextSibling();
		Row clonedRow = (Row) templateRow.deepClone(true);
		table.insertBefore(clonedRow, table.getLastRow());
		return clonedRow;
	}

	private double getRatingLineVOffset(int lineNr) {
		return 98.8 * POINTS_PER_MM + 11.84 * lineNr;
	}

	private double getRatingHOffset(int rating) {
		double ratingDelta = Math.min(100 - rating, 50.0) / 50.0;
		return 90.3 * POINTS_PER_MM + ratingDelta * (124.5 - 75.3) * POINTS_PER_MM;
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
