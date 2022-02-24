
package io.zeitwert.ddd.app.adapter.api.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.zeitwert.ddd.app.model.Application;
import io.zeitwert.ddd.app.model.ApplicationInfo;
import io.zeitwert.ddd.app.service.api.ApplicationService;

@RestController("applicationController")
@RequestMapping("/api/app")
public class ApplicationController {

	private final ApplicationService applicationService;

	@Autowired
	ApplicationController(ApplicationService applicationService) {
		this.applicationService = applicationService;
	}

	@GetMapping("/applications")
	ResponseEntity<List<Application>> getApplications() {
		return ResponseEntity.ok(this.applicationService.getAllApplications());
	}

	@GetMapping("/applications/{id}")
	ResponseEntity<ApplicationInfo> getApplication(@PathVariable String id) {
		ApplicationInfo appInfo = this.applicationService.getApplicationMenu(id);
		if (appInfo == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(appInfo);
	}

}
