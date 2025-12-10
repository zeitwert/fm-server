package io.zeitwert.fm.building.adapter.api.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.zeitwert.fm.building.model.enums.CodeBuildingPart;
import io.zeitwert.fm.building.service.api.dto.ProjectionPeriod;

@RestController("projectionController")
@RequestMapping("/rest/projection")
public class ProjectionController {

	@GetMapping("/elements/{elementId}")
	ResponseEntity<List<ProjectionPeriod>> getElementProjection(@PathVariable String elementId)
			throws InterruptedException, ExecutionException {
		final CodeBuildingPart buildingPart = CodeBuildingPart.getBuildingPart(elementId);
		final int startYear = 2021;
		final double condition = 1;
		final int duration = 50;
		final double value = 1000000.0;
		List<ProjectionPeriod> projection = buildingPart.getProjection(value, startYear, condition, startYear, duration);
		return ResponseEntity.ok(projection);
	}

	@GetMapping("/elements/{elementId}/withRestoration")
	ResponseEntity<List<Double>> getRestoredTimeValue(@PathVariable String elementId)
			throws InterruptedException, ExecutionException {
		CodeBuildingPart buildingPart = CodeBuildingPart.getBuildingPart(elementId);
		List<Double> timeValues = new ArrayList<Double>();
		int year = 0;
		for (int t = 0; t < 100; t++) {
			Double timeValue = buildingPart.getTimeValue((double) year);
			timeValues.add(timeValue);
			if (timeValue <= buildingPart.getOptimalRestoreTimeValue()) {
				year = 0;
			}
			year += 1;
		}
		return ResponseEntity.ok(timeValues);
	}

	@GetMapping("/elements/{elementId}/relativeAge/{timeValue}")
	ResponseEntity<Double> getRelativeAge(@PathVariable String elementId, @PathVariable Double timeValue)
			throws InterruptedException, ExecutionException {
		CodeBuildingPart buildingPart = CodeBuildingPart.getBuildingPart(elementId);
		return ResponseEntity.ok(buildingPart.getRelativeAge(timeValue));
	}

	@GetMapping("/elements/{elementId}/nextRestoration/{ratingYear}/{condition}")
	ResponseEntity<ProjectionPeriod> getNextRestoration(@PathVariable String elementId,
			@PathVariable Integer ratingYear,
			@PathVariable Double condition)
			throws InterruptedException, ExecutionException {
		CodeBuildingPart buildingPart = CodeBuildingPart.getBuildingPart(elementId);
		return ResponseEntity
				.ok(buildingPart.getNextRestoration(1000000, ratingYear, condition));
	}

}
