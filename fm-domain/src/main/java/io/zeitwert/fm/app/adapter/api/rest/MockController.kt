package io.zeitwert.fm.app.adapter.api.rest

import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.IOException

@RestController("mockController")
@RequestMapping("/mock")
class MockController(
	private val resourceLoader: ResourceLoader,
) {

	companion object {

		private const val KERNEL_TENANT = "t0"
		private const val MOCK_DIR = "mock"
	}

	@Throws(IOException::class)
	private fun generateResourceResponse(
		tenant: String,
		basePath: String,
		subPath: String,
	): ResponseEntity<Resource> {
		val path = "classpath:$basePath/$tenant/$subPath.json"
		try {
			return ResponseEntity.ok(
				InputStreamResource(
					this.resourceLoader.getResource(path).inputStream,
				),
			)
		} catch (e: IOException) {
			if (tenant !== KERNEL_TENANT) {
				return this.generateResourceResponse(KERNEL_TENANT, basePath, subPath)
			}
			return ResponseEntity.notFound().build()
		}
	}

	@GetMapping(path = ["/{tenant}/{module}/{collection}"], produces = [MediaType.APPLICATION_JSON_VALUE])
	@Throws(
		IOException::class,
	)
	fun getModuleMock(
		@PathVariable tenant: String,
		@PathVariable module: String,
		@PathVariable collection: String,
	): ResponseEntity<Resource> = this.generateResourceResponse(tenant, MOCK_DIR, "$module/$collection")

	@GetMapping(path = ["/{tenant}/{module}/{collection}/{id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
	@Throws(
		IOException::class,
	)
	fun getModuleMockItem(
		@PathVariable tenant: String,
		@PathVariable module: String,
		@PathVariable collection: String,
		@PathVariable id: String,
	): ResponseEntity<Resource> = this.generateResourceResponse(tenant, MOCK_DIR, "$module/$collection/$id")

}
