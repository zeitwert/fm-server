
package io.zeitwert.fm.building.adapter.api.rest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Calendar;

import javax.annotation.PostConstruct;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
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
@RequestMapping("/evaluation/building/buildings")
public class BuildingEvaluationController {

	private static final int CoverFotoWidth = 400;
	private static final int CoverFotoHeight = 230;

	// private static final String OPT_IS_MARKER = "\u058D";
	// private static final String OptimumRenovationMarker = "X";
	private static final String OptimumRenovationMarker = Character.toString((char) 110);

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
		doc.setFontSettings(this.fontSettings);

		this.insertCoverFoto(doc, building);
		this.insertLocationImage(doc, building);
		engine.buildReport(doc, evaluationResult, "building");
		this.fillOptRenovationTable(doc, evaluationResult);
		this.fillCostsChart(doc, evaluationResult);
		this.fillCostsTable(doc, evaluationResult);

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
		ContentDisposition contentDisposition = ContentDisposition.builder("attachment").filename(fileName).build();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentDisposition(contentDisposition);
		return ResponseEntity.ok().headers(headers).body(outStream.toByteArray());

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

	private void fillOptRenovationTable(Document doc, BuildingEvaluationResult evaluationResult) {

		Table optRenovationTable = (Table) doc.getChild(NodeType.TABLE, 3, true);

		Cell yearCell = ((Row) optRenovationTable.getFirstRow().getNextSibling()).getFirstCell();
		for (int i = 0; i < 25; i++) {
			yearCell = (Cell) yearCell.getNextSibling();
			yearCell.getFirstParagraph().appendChild(new Run(doc, Integer.toString(evaluationResult.getStartYear() + i)));
		}

		for (EvaluationElement e : evaluationResult.getElements()) {
			if (e.getValuePart() != null && e.getValuePart() > 0) {
				Row row = addRenovationTableRow(optRenovationTable);
				Cell cell = row.getFirstCell();
				cell.getFirstParagraph().appendChild(new Run(doc, e.getName()));
				Integer restorationYear = e.getRestorationYear();
				if (restorationYear != null) {
					Integer delta = (int) Math.max(0, restorationYear - evaluationResult.getStartYear());
					cell = getNthNextSibling(row.getFirstCell(), delta);
					Run marker = new Run(doc, OptimumRenovationMarker);
					marker.getFont().setName("Webdings");
					cell.getFirstParagraph().appendChild(marker);
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

		costChart.getSeries().get(0).getMarker().setSymbol(MarkerSymbol.CIRCLE);
		costChart.getSeries().get(0).getMarker().setSize(5);

		costChart.getSeries().get(1).getMarker().setSymbol(MarkerSymbol.CIRCLE);
		costChart.getSeries().get(1).getMarker().setSize(5);

	}

	private void fillCostsTable(Document doc, BuildingEvaluationResult evaluationResult) {

		Table costsTable = (Table) doc.getChild(NodeType.TABLE, 4, true);
		Formatter fmt = Formatter.INSTANCE;

		for (EvaluationPeriod ep : evaluationResult.getPeriods()) {
			Row row = addCostsTableRow(costsTable);
			Cell cell = row.getFirstCell();
			if (ep.getYear() != null) { // only yearly summary records
				// year
				cell.getFirstParagraph().appendChild(new Run(doc, ep.getYear().toString()));
				cell = (Cell) cell.getNextSibling();
				// originalValue
				cell.getFirstParagraph().appendChild(new Run(doc, fmt.formatNumber(ep.getOriginalValue())));
				cell = (Cell) cell.getNextSibling();
				// timeValue
				cell.getFirstParagraph().appendChild(new Run(doc, fmt.formatNumber(ep.getTimeValue())));
				cell = (Cell) cell.getNextSibling();
				// maintenanceCosts
				cell.getFirstParagraph().appendChild(new Run(doc, fmt.formatNumber(ep.getMaintenanceCosts())));
				cell = (Cell) cell.getNextSibling();
			} else {
				cell = (Cell) cell.getNextSibling().getNextSibling().getNextSibling().getNextSibling();
			}
			// restorationCosts
			if (ep.getRestorationCosts() != null && ep.getRestorationCosts() > 0) {
				cell.getFirstParagraph().appendChild(new Run(doc, fmt.formatNumber(ep.getRestorationCosts())));
			}
			cell = (Cell) cell.getNextSibling();
			// restorationElement
			cell.getFirstParagraph().appendChild(new Run(doc, ep.getRestorationElement()));
			cell = (Cell) cell.getNextSibling();
			if (ep.getYear() != null) { // only yearly summary records
				// totalCosts
				cell.getFirstParagraph().appendChild(new Run(doc, fmt.formatNumber(ep.getTotalCosts())));
				cell = (Cell) cell.getNextSibling();
				// aggrCosts
				cell.getFirstParagraph().appendChild(new Run(doc, fmt.formatNumber(ep.getAggrCosts())));
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
