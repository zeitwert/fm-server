package io.zeitwert.fm.app.adapter.api.rest

import org.springframework.core.io.Resource
import org.springframework.util.FileCopyUtils
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.UncheckedIOException
import java.nio.charset.StandardCharsets

/**
 * Utility class to work with Resources.
 */
object ResourceReader {

	fun asString(resource: Resource): String {
		try {
			InputStreamReader(resource.inputStream, StandardCharsets.UTF_8).use { reader ->
				return FileCopyUtils.copyToString(reader)
			}
		} catch (e: IOException) {
			throw UncheckedIOException(e)
		}
	}

	@Throws(IOException::class)
	fun asByteArray(resource: Resource): ByteArray {
		val `is` = resource.inputStream
		val buffer = ByteArrayOutputStream()
		var nRead: Int
		val data = ByteArray(1024)
		while ((`is`.read(data, 0, data.size).also { nRead = it }) != -1) {
			buffer.write(data, 0, nRead)
		}
		buffer.flush()

		return buffer.toByteArray()
	}

}
