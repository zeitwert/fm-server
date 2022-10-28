package io.zeitwert.fm.building.adapter.api.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.building.model.enums.CodeBuildingPart;
import io.zeitwert.fm.building.model.enums.CodeBuildingPartEnum;
import io.zeitwert.fm.building.service.api.ProjectionService;
import io.zeitwert.fm.building.service.api.dto.ProjectionPeriod;
import io.zeitwert.fm.building.service.api.dto.ProjectionResult;
import io.zeitwert.fm.portfolio.model.ObjPortfolioRepository;

@RestController("projectionController")
@RequestMapping("/api/projection")
public class ProjectionController {

	private final RequestContext requestCtx;

	private final ObjPortfolioRepository portfolioRepo;

	private final ObjBuildingRepository buildingRepo;

	private final ProjectionService projectionService;

	public ProjectionController(RequestContext requestCtx,
			ObjPortfolioRepository portfolioRepo, ObjBuildingRepository buildingRepo,
			ProjectionService projectionService) {
		this.requestCtx = requestCtx;
		this.portfolioRepo = portfolioRepo;
		this.buildingRepo = buildingRepo;
		this.projectionService = projectionService;
	}

	@GetMapping("/portfolios/{portfolioId}")
	ResponseEntity<ProjectionResult> getPortfolioProjection(@PathVariable Integer portfolioId)
			throws InterruptedException, ExecutionException {
		return ResponseEntity
				.ok(this.projectionService.getProjection(this.portfolioRepo.get(requestCtx, portfolioId)));
	}

	@GetMapping("/buildings/{buildingId}")
	ResponseEntity<ProjectionResult> getBuildingProjection(@PathVariable Integer buildingId)
			throws InterruptedException, ExecutionException {
		return ResponseEntity
				.ok(this.projectionService.getProjection(this.buildingRepo.get(requestCtx, buildingId)));
	}

	@GetMapping("/elements/{elementId}")
	ResponseEntity<List<ProjectionPeriod>> getElementProjection(@PathVariable String elementId)
			throws InterruptedException, ExecutionException {
		final CodeBuildingPart buildingPart = CodeBuildingPartEnum.getBuildingPart(elementId);
		final int startYear = 2021;
		final double condition = 1;
		final int duration = 50;
		final double value = 1000000.0;
		List<ProjectionPeriod> projection = this.projectionService.getProjection(buildingPart, value, startYear, condition,
				startYear, duration);
		return ResponseEntity.ok(projection);
	}

	@GetMapping("/elements/{elementId}/withRestoration")
	ResponseEntity<List<Double>> getRestoredTimeValue(@PathVariable String elementId)
			throws InterruptedException, ExecutionException {
		CodeBuildingPart buildingPart = CodeBuildingPartEnum.getBuildingPart(elementId);
		List<Double> timeValues = new ArrayList<Double>();
		int year = 0;
		for (int t = 0; t < 100; t++) {
			Double timeValue = this.projectionService.getTimeValue(buildingPart, (double) year);
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
		CodeBuildingPart buildingPart = CodeBuildingPartEnum.getBuildingPart(elementId);
		return ResponseEntity.ok(this.projectionService.getRelativeAge(buildingPart, timeValue));
	}

	@GetMapping("/elements/{elementId}/nextRestoration/{conditionYear}/{condition}")
	ResponseEntity<ProjectionPeriod> getNextRestoration(@PathVariable String elementId,
			@PathVariable Integer conditionYear,
			@PathVariable Double condition)
			throws InterruptedException, ExecutionException {
		CodeBuildingPart buildingPart = CodeBuildingPartEnum.getBuildingPart(elementId);
		return ResponseEntity
				.ok(this.projectionService.getNextRestoration(buildingPart, 1000000, conditionYear, condition));
	}

}
