
package fm.comunas.ddd.app.adapter.api.rest;

import fm.comunas.ddd.util.ResourceReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("configController")
@RequestMapping("/config")
public class ConfigController {

	private static String KERNEL_TENANT = "t0";
	private static String CONFIG_DIR = "config";

	private ResourceLoader resourceLoader;

	@Autowired
	public ConfigController(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	private ResponseEntity<Resource> generateResourceResponse(String tenant, String basePath, String subPath)
			throws IOException {
		String path = "classpath:" + basePath + "/" + tenant + "/" + subPath + ".json";
		try {
			return ResponseEntity.ok(new InputStreamResource(this.resourceLoader.getResource(path).getInputStream()));
		} catch (IOException e) {
			if (tenant != KERNEL_TENANT) {
				return this.generateResourceResponse(KERNEL_TENANT, basePath, subPath);
			}
			return ResponseEntity.notFound().build();
		}
	}

	private ResponseEntity<List<JsonNode>> generateResourceCollection(String tenant, String basePath, String subPath)
			throws IOException {
		String path = "classpath:" + basePath + "/" + tenant + "/" + subPath;

		Resource resource = this.resourceLoader.getResource(path);
		if (!resource.exists()) {
			if (tenant != KERNEL_TENANT) {
				return this.generateResourceCollection(KERNEL_TENANT, basePath, subPath);
			}
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(this.aggregatedJson(path));
	}

	/**
	 * Read and concatenates files inside classpath directory.
	 * 
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	private List<JsonNode> aggregatedJson(String classpath) {
		List<JsonNode> result = new ArrayList<>();
		ObjectMapper om = new ObjectMapper();

		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource[] resources;
		try {
			resources = resolver.getResources(classpath + "/*.*");
			for (Resource res : resources) {
				try {
					result.add(om.readTree(ResourceReader.asString(res)));
				} catch (IOException e) {
					//
				}
			}
		} catch (IOException e) {
			//
		}

		return result;
	}

	@GetMapping(path = "/{tenant}/{config}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Resource> getTenantConfig(@PathVariable String tenant, @PathVariable String config)
			throws IOException {
		return this.generateResourceResponse(tenant, CONFIG_DIR, config);
	}

	@GetMapping(path = "/{tenant}/{module}/datamarts/{datamart}/{collection}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<List<JsonNode>> getModuleDatamartConfig(@PathVariable String tenant, @PathVariable String module,
			@PathVariable String datamart, @PathVariable String collection) throws IOException {
		return this.generateResourceCollection(tenant, CONFIG_DIR, module + "/datamarts/" + datamart + "/" + collection);
	}

	@GetMapping(path = "/{tenant}/{module}/datamarts/{datamart}/{collection}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Resource> getModuleDatamartConfigItem(@PathVariable String tenant, @PathVariable String module,
			@PathVariable String datamart, @PathVariable String collection, @PathVariable String id) throws IOException {
		return this.generateResourceResponse(tenant, CONFIG_DIR,
				module + "/datamarts/" + datamart + "/" + collection + "/" + id);
	}

	@GetMapping(path = "/{tenant}/{module}/{collection}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Resource> getModuleConfig(@PathVariable String tenant, @PathVariable String module,
			@PathVariable String collection) throws IOException {
		return this.generateResourceResponse(tenant, CONFIG_DIR, module + "/" + collection);
	}

	@GetMapping(path = "/{tenant}/{module}/{collection}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Resource> getModuleConfigItem(@PathVariable String tenant, @PathVariable String module,
			@PathVariable String collection, @PathVariable String id) throws IOException {
		return this.generateResourceResponse(tenant, CONFIG_DIR, module + "/" + collection + "/" + id);
	}

}
