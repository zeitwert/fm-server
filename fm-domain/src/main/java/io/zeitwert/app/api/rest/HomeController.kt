package io.zeitwert.app.api.rest

import dddrive.app.doc.model.enums.CodeCaseStageEnum.Companion.getCaseStage
import dddrive.ddd.model.enums.CodeAggregateTypeEnum.Companion.getAggregateType
import io.zeitwert.app.api.jsonapi.EnumeratedDto
import io.zeitwert.app.api.jsonapi.dto.TypedEnumeratedDto
import io.zeitwert.app.api.rest.dto.HomeActionResponse
import io.zeitwert.app.api.rest.dto.HomeActivityResponse
import io.zeitwert.app.api.rest.dto.HomeOverviewResponse
import io.zeitwert.app.obj.model.FMObjRepository
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.building.model.ObjBuilding
import io.zeitwert.fm.building.model.ObjBuildingRepository
import io.zeitwert.fm.collaboration.model.db.tables.records.ActivityVRecord
import io.zeitwert.fm.oe.model.ObjUserRepository
import io.zeitwert.fm.portfolio.model.ObjPortfolioRepository
import io.zeitwert.fm.task.model.DocTask
import io.zeitwert.fm.task.model.DocTaskRepository
import io.zeitwert.fm.task.model.enums.CodeTaskPriority
import io.zeitwert.fm.util.Formatter.formatIsoDate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController("homeController")
@RequestMapping("/rest/home")
class HomeController {

	@Autowired
	lateinit var objRepository: FMObjRepository

	@Autowired
	lateinit var userRepository: ObjUserRepository

	@Autowired
	lateinit var accountRepository: ObjAccountRepository

	@Autowired
	lateinit var buildingRepository: ObjBuildingRepository

	@Autowired
	lateinit var portfolioRepository: ObjPortfolioRepository

	@Autowired
	lateinit var taskRepository: DocTaskRepository

	@Autowired
	lateinit var sessionContext: SessionContext

//	@Autowired
//	lateinit var dslContext: DSLContext

	var allBuildings: List<ObjBuilding>? = null

	fun getBuildings(): List<ObjBuilding> {
		if (allBuildings == null) {
			val buildingIds = buildingRepository.find(null)
			allBuildings = buildingIds.map { buildingRepository.get(it) }
		}
		return allBuildings!!
	}

	@GetMapping("/overview/{accountId}")
	fun getOverview(
		@PathVariable("accountId") accountId: Int,
	): ResponseEntity<HomeOverviewResponse> {
		val account = accountRepository.get(accountId)
		sessionContext.tenantId
		val portfolioCount = portfolioRepository.find(null).size
		val buildings = getBuildings()
		val ratingCount = buildings.filter { activeRatings.contains(it.currentRating?.ratingStatus?.id) }.size
		val insuranceValue = buildings.sumOf { it.insuredValue?.toInt() ?: 0 }
		return ResponseEntity.ok(
			HomeOverviewResponse(
				accountId = accountId,
				accountName = account.name ?: "Unbekannt",
				buildingCount = buildings.size,
				portfolioCount = portfolioCount,
				ratingCount = ratingCount,
				insuranceValue = insuranceValue,
				timeValue = 0,
				shortTermRenovationCosts = 0,
				midTermRenovationCosts = 0,
			),
		)
	}

	@GetMapping("/openActivities/{accountId}")
	fun getOpenActivities(
		@PathVariable("accountId") accountId: Int?,
	): ResponseEntity<List<HomeActivityResponse>> {
		val buildings = getBuildings()
		val rrList = buildings
			.filter { activeRatings.contains(it.currentRating?.ratingStatus?.id) }
			.map { getRatingResponse(it) }
		val taskList = taskRepository.find(null).map { taskRepository.get(it) }
		val ttList = taskList.filter { "task.done" != it.meta.caseStage!!.id }.map { getTaskResponse(it) }
		val result = mutableListOf<HomeActivityResponse>()
		result.addAll(rrList)
		result.addAll(ttList)
		return ResponseEntity.ok(result)
	}

	private fun getRatingResponse(building: ObjBuilding): HomeActivityResponse {
		val address = building.street + "\n" + building.zip + " " + building.city
		val rating = building.currentRating
		val ratingUser = rating?.ratingUser
		val ratingUserDto = EnumeratedDto.of(ratingUser)
		val ratingDate = rating?.ratingDate
		return HomeActivityResponse(
			item = EnumeratedDto.of(building)!!,
			relatedTo = EnumeratedDto.of(building)!!,
			owner = ratingUserDto,
			user = ratingUserDto,
			dueAt = formatIsoDate(ratingDate!!),
			subject = building.name,
			content = address,
			priority = EnumeratedDto.of(CodeTaskPriority.NORMAL),
		)
	}

	private fun getTaskResponse(task: DocTask): HomeActivityResponse {
		val relatedToId = task.relatedToId as Int?
		val relatedTo = objRepository.get(relatedToId!!)
		return HomeActivityResponse(
			item = EnumeratedDto.of(task)!!,
			relatedTo = EnumeratedDto.of(relatedTo)!!,
			owner = EnumeratedDto.of(if (task.ownerId != null) userRepository.get(task.ownerId!!) else null),
			user = EnumeratedDto.of(if (task.assigneeId != null) userRepository.get(task.assigneeId!!) else null),
			dueAt = formatIsoDate(task.dueAt!!.toLocalDate()),
			subject = task.subject,
			content = task.content,
			priority = EnumeratedDto.of(task.priority),
		)
	}

	@GetMapping("/recentActions/{accountId}")
	fun getRecentActions(
		@PathVariable("accountId") accountId: Int,
	): ResponseEntity<List<HomeActionResponse>> {
		return ResponseEntity.ok(emptyList())
		// val account = accountRepository.get(accountId)
		// val result = dslContext
		// 	.selectFrom(Tables.ACTIVITY_V)
		// 	.where(
		// 		Tables.ACTIVITY_V.TENANT_ID
		// 			.eq(account.tenantId as Int)
		// 			.and(Tables.ACTIVITY_V.ACCOUNT_ID.eq(accountId)),
		// 	).orderBy(Tables.ACTIVITY_V.TIMESTAMP.desc())
		// 	.limit(20)
		// 	.fetch()
		// return ResponseEntity.ok(result.map { getActivityResponse(it) })
	}

	private fun getActivityResponse(a: ActivityVRecord): HomeActionResponse {
		val type = getAggregateType(a.aggregateTypeId)
		val item = TypedEnumeratedDto(
			a.id.toString(),
			a.caption,
			EnumeratedDto.of(type)!!,
		)
		val oldCaseStage = if (a.oldCaseStageId != null) {
			getCaseStage(a.oldCaseStageId)
		} else {
			null
		}
		val newCaseStage = if (a.newCaseStageId != null) {
			getCaseStage(a.newCaseStageId)
		} else {
			null
		}
		return HomeActionResponse(
			item = item,
			seqNr = a.seqNr,
			timestamp = a.timestamp,
			user = EnumeratedDto.of(userRepository.get(a.userId))!!,
			changes = null,
			oldCaseStage = EnumeratedDto.of(oldCaseStage),
			newCaseStage = EnumeratedDto.of(newCaseStage),
		)
	}

	companion object {

		private val activeRatings = mutableListOf<String?>("open", "review")
	}

}
