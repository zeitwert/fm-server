package io.zeitwert.fm.building.adapter.rest

import io.zeitwert.fm.building.api.dto.ProjectionPeriod
import io.zeitwert.fm.building.model.enums.CodeBuildingPart.Enumeration.getBuildingPart
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.ExecutionException

@RestController("projectionController")
@RequestMapping("/rest/projection")
class ProjectionController {

	@GetMapping("/elements/{elementId}")
	@Throws(InterruptedException::class, ExecutionException::class)
	fun getElementProjection(
		@PathVariable elementId: String,
	): ResponseEntity<List<ProjectionPeriod>> {
		val buildingPart = getBuildingPart(elementId)!!
		val startYear = 2021
		val condition = 1.0
		val duration = 50
		val value = 1000000.0
		val projection = buildingPart.getProjection(value, startYear, condition, startYear, duration)
		return ResponseEntity.ok(projection)
	}

	@GetMapping("/elements/{elementId}/withRestoration")
	@Throws(InterruptedException::class, ExecutionException::class)
	fun getRestoredTimeValue(
		@PathVariable elementId: String,
	): ResponseEntity<List<Double>> {
		val buildingPart = getBuildingPart(elementId)!!
		val timeValues: MutableList<Double> = mutableListOf()
		var year = 0
		for (t in 0..99) {
			val timeValue = buildingPart.getTimeValue(year.toDouble())
			timeValues.add(timeValue)
			if (timeValue <= buildingPart.optimalRestoreTimeValue) {
				year = 0
			}
			year += 1
		}
		return ResponseEntity.ok(timeValues)
	}

	@GetMapping("/elements/{elementId}/relativeAge/{timeValue}")
	@Throws(InterruptedException::class, ExecutionException::class)
	fun getRelativeAge(
		@PathVariable elementId: String,
		@PathVariable timeValue: Double,
	): ResponseEntity<Double?> {
		val buildingPart = getBuildingPart(elementId)!!
		return ResponseEntity.ok(buildingPart.getRelativeAge(timeValue))
	}

	@GetMapping("/elements/{elementId}/nextRestoration/{ratingYear}/{condition}")
	@Throws(InterruptedException::class, ExecutionException::class)
	fun getNextRestoration(
		@PathVariable elementId: String,
		@PathVariable ratingYear: Int,
		@PathVariable condition: Double,
	): ResponseEntity<ProjectionPeriod?> {
		val buildingPart = getBuildingPart(elementId)!!
		return ResponseEntity.ok(buildingPart.getNextRestoration(1000000.0, ratingYear, condition))
	}

}
