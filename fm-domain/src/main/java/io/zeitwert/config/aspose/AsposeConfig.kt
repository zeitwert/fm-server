package io.zeitwert.config.aspose

import com.aspose.words.FolderFontSource
import com.aspose.words.FontSettings
import com.aspose.words.License
import com.aspose.words.PhysicalFontInfo
import com.aspose.words.ReportingEngine
import jakarta.annotation.PostConstruct
import org.apache.tomcat.util.http.fileupload.IOUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@Configuration("asposeConfig")
open class AsposeConfig {

	private val logger: Logger = LoggerFactory.getLogger(AsposeConfig::class.java)
	private val classLoader: ClassLoader = this.javaClass.getClassLoader()

	@JvmField
	@Value("classpath:license/Aspose.Words.Java.lic")
	var licenseFile: Resource? = null

	@JvmField
	var fontsDirectory: File? = null

	@JvmField
	var fontSettings: FontSettings? = null

	@PostConstruct
	@Throws(Exception::class)
	protected fun initLicense() {
		val lic = License()
		lic.setLicense(licenseFile!!.inputStream)
		ReportingEngine.setUseReflectionOptimization(false)
	}

	@PostConstruct
	@Throws(Exception::class)
	protected fun initFonts() {
		val tmpDir = File(System.getProperty("java.io.tmpdir"))
		this.fontsDirectory = File(tmpDir, "fonts")
		if (!this.fontsDirectory!!.exists()) {
			this.fontsDirectory!!.mkdirs()
		}
		logger.info("initFonts: " + this.fontsDirectory!!.absolutePath)

		this.copyStream2File("trebuc")
		this.copyStream2File("trebucbd")
		this.copyStream2File("trebucbi")
		this.copyStream2File("trabucit")
		this.copyStream2File("webdings")
		this.copyStream2File("wingding")

		this.fontSettings = FontSettings()
		this.fontSettings!!.setFontsFolder(this.fontsDirectory!!.absolutePath, false)

		listFonts()
	}

	@Throws(IOException::class)
	private fun copyStream2File(fontName: String?) {
		val `is` = classLoader.getResourceAsStream("fonts/" + fontName + ".ttf")
		if (`is` != null) {
			val f = File(this.fontsDirectory!!.absolutePath + "/" + fontName + ".ttf")
			f.deleteOnExit()
			FileOutputStream(f).use { out ->
				IOUtils.copy(`is`, out)
			}
		}
	}

	// Get available fonts in folder
	private fun listFonts() {
		val fonts: MutableList<PhysicalFontInfo> =
			FolderFontSource(fontsDirectory!!.absolutePath, false).availableFonts
		for (fontInfo in fonts) {
			logger.info(
				(
					"Font family: " + fontInfo.fontFamilyName +
						", version: " + fontInfo.version +
						", font: " + fontInfo.fullFontName +
						", path : " + fontInfo.filePath
				),
			)
		}
	}

}
