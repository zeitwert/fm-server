package io.zeitwert.fm.server.config.aspose;

import javax.annotation.PostConstruct;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.aspose.words.FolderFontSource;
import com.aspose.words.FontSettings;
import com.aspose.words.License;
import com.aspose.words.PhysicalFontInfo;
import com.aspose.words.ReportingEngine;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration("asposeConfig")
public class AsposeConfig {

	private Logger logger = LoggerFactory.getLogger(AsposeConfig.class);
	private ClassLoader classLoader = this.getClass().getClassLoader();

	@Value("classpath:license/Aspose.Words.Java.lic")
	Resource licenseFile;

	File fontsDirectory;
	FontSettings fontSettings;

	public FontSettings getFontSettings() {
		return this.fontSettings;
	}

	@PostConstruct
	protected void initLicense() throws Exception {

		License lic = new License();
		lic.setLicense(licenseFile.getInputStream());

		ReportingEngine.setUseReflectionOptimization(false);

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

	// Get available fonts in folder
	private void listFonts() {
		List<PhysicalFontInfo> fonts = new FolderFontSource(fontsDirectory.getAbsolutePath(), false).getAvailableFonts();
		for (PhysicalFontInfo fontInfo : fonts) {
			logger.info(
					"Font family: " + fontInfo.getFontFamilyName()
							+ ", version: " + fontInfo.getVersion()
							+ ", font: " + fontInfo.getFullFontName()
							+ ", path : " + fontInfo.getFilePath());
		}
	}

}
