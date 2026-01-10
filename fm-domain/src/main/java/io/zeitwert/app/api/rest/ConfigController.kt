package io.zeitwert.app.api.rest

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.IOException

@RestController("configController")
@RequestMapping("/config")
class ConfigController(
	private val resourceLoader: ResourceLoader,
) {

	companion object {

		private const val KERNEL_TENANT = "t0"
		private const val CONFIG_DIR = "config"
	}

	@GetMapping(path = ["/{tenant}/{config}"], produces = [MediaType.APPLICATION_JSON_VALUE])
	@Throws(IOException::class)
	fun getTenantConfig(
		@PathVariable tenant: String,
		@PathVariable config: String,
	): ResponseEntity<Resource> = generateResourceResponse(tenant, CONFIG_DIR, config)

	@GetMapping(
		path = ["/{tenant}/{module}/datamarts/{datamart}/{collection}"],
		produces = [MediaType.APPLICATION_JSON_VALUE],
	)
	@Throws(
		IOException::class,
	)
	fun getModuleDatamartConfig(
		@PathVariable tenant: String,
		@PathVariable module: String,
		@PathVariable datamart: String,
		@PathVariable collection: String,
	): ResponseEntity<List<JsonNode>> = generateResourceCollection(tenant, CONFIG_DIR, "$module/datamarts/$datamart/$collection")

	@GetMapping(
		path = ["/{tenant}/{module}/datamarts/{datamart}/{collection}/{id}"],
		produces = [MediaType.APPLICATION_JSON_VALUE],
	)
	@Throws(
		IOException::class,
	)
	fun getModuleDatamartConfigItem(
		@PathVariable tenant: String,
		@PathVariable module: String,
		@PathVariable datamart: String,
		@PathVariable collection: String,
		@PathVariable id: String,
	): ResponseEntity<Resource> = generateResourceResponse(tenant, CONFIG_DIR, "$module/datamarts/$datamart/$collection/$id")

	@GetMapping(path = ["/{tenant}/{module}/{collection}"], produces = [MediaType.APPLICATION_JSON_VALUE])
	@Throws(
		IOException::class,
	)
	fun getModuleConfig(
		@PathVariable tenant: String,
		@PathVariable module: String,
		@PathVariable collection: String,
	): ResponseEntity<Resource> = generateResourceResponse(tenant, CONFIG_DIR, "$module/$collection")

	@GetMapping(path = ["/{tenant}/{module}/{collection}/{id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
	@Throws(
		IOException::class,
	)
	fun getModuleConfigItem(
		@PathVariable tenant: String,
		@PathVariable module: String,
		@PathVariable collection: String,
		@PathVariable id: String,
	): ResponseEntity<Resource> = generateResourceResponse(tenant, CONFIG_DIR, "$module/$collection/$id")

	private fun generateResourceResponse(
		tenant: String,
		basePath: String,
		subPath: String,
	): ResponseEntity<Resource> {
		val path = "classpath:$basePath/$tenant/$subPath.json"
		try {
			return ResponseEntity.ok(InputStreamResource(resourceLoader.getResource(path).inputStream))
		} catch (e: IOException) {
			if (tenant !== KERNEL_TENANT) {
				return generateResourceResponse(KERNEL_TENANT, basePath, subPath)
			}
			return ResponseEntity.notFound().build()
		}
	}

	private fun generateResourceCollection(
		tenant: String,
		basePath: String,
		subPath: String,
	): ResponseEntity<List<JsonNode>> {
		val path = "classpath:$basePath/$tenant/$subPath"
		val resource = resourceLoader.getResource(path)
		if (!resource.exists()) {
			if (tenant !== KERNEL_TENANT) {
				return generateResourceCollection(KERNEL_TENANT, basePath, subPath)
			}
			return ResponseEntity.notFound().build()
		}
		return ResponseEntity.ok(aggregatedJson(path))
	}

	private fun aggregatedJson(classpath: String): List<JsonNode> {
		val result: MutableList<JsonNode> = mutableListOf()
		val om = ObjectMapper()
		val resolver = PathMatchingResourcePatternResolver()
		val resources: Array<Resource>
		try {
			resources = resolver.getResources(classpath + "/*.*")
			for (res in resources) {
				try {
					result.add(om.readTree(ResourceReader.asString(res)))
				} catch (e: IOException) {
					//
				}
			}
		} catch (e: IOException) {
			//
		}
		return result
	}

}
