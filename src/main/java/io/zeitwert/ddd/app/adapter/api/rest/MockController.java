
package io.zeitwert.ddd.app.adapter.api.rest;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("mockController")
@RequestMapping("/mock")
public class MockController {

	private static String KERNEL_TENANT = "t0";
	private static String MOCK_DIR = "mock";

	private ResourceLoader resourceLoader;

	@Autowired
	public MockController(ResourceLoader resourceLoader) {
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

	@GetMapping(path = "/{tenant}/{module}/{collection}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Resource> getModuleMock(@PathVariable String tenant, @PathVariable String module,
			@PathVariable String collection) throws IOException {
		return this.generateResourceResponse(tenant, MOCK_DIR, module + "/" + collection);
	}

	@GetMapping(path = "/{tenant}/{module}/{collection}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Resource> getModuleMockItem(@PathVariable String tenant, @PathVariable String module,
			@PathVariable String collection, @PathVariable String id) throws IOException {
		return this.generateResourceResponse(tenant, MOCK_DIR, module + "/" + collection + "/" + id);
	}

}
