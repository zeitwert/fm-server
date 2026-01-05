package io.zeitwert.fm.app.adapter.api.rest

import dddrive.app.doc.model.enums.CodeCaseStageEnum.Companion.getCaseStage
import dddrive.ddd.core.model.enums.CodeAggregateTypeEnum.Companion.getAggregateType
import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.EnumeratedDto
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.app.adapter.api.rest.dto.HomeActionResponse
import io.zeitwert.fm.app.adapter.api.rest.dto.HomeActivityResponse
import io.zeitwert.fm.app.adapter.api.rest.dto.HomeOverviewResponse
import io.zeitwert.fm.building.model.ObjBuilding
import io.zeitwert.fm.building.model.ObjBuildingRepository
import io.zeitwert.fm.collaboration.model.db.tables.records.ActivityVRecord
import io.zeitwert.fm.obj.model.FMObjVRepository
import io.zeitwert.fm.oe.model.ObjUserRepository
import io.zeitwert.fm.portfolio.model.ObjPortfolioRepository
import io.zeitwert.fm.task.model.DocTask
import io.zeitwert.fm.task.model.DocTaskRepository
import io.zeitwert.fm.task.model.enums.CodeTaskPriority
import io.zeitwert.fm.util.Formatter.formatIsoDate
import org.jooq.DSLContext
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
	lateinit var objRepository: FMObjVRepository

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

	@Autowired
	lateinit var dslContext: DSLContext

	@GetMapping("/overview/{accountId}")
	fun getOverview(
		@PathVariable("accountId") accountId: Int,
	): ResponseEntity<HomeOverviewResponse> {
		val account = accountRepository.get(accountId)
		sessionContext.tenantId
		val portfolioCount = portfolioRepository.find(null).size
		val buildingCount = buildingRepository.find(null).size
		// 		List<ObjBuilding> buildings = buildingRepository.getAll(tenantId).stream().map(it -> buildingRepository.get(it)).toList();
		val ratingCount = 0
		val insuranceValue = 0
		// 		Integer ratingCount = (int) buildings.stream()
// 				.filter(b -> b.getCurrentRating() != null && activeRatings.contains(b.getCurrentRating().getRatingStatus().getId()))
// 				.count();
// 		Integer insuranceValue = buildings.stream()
// 				.map(b -> b.getInsuredValue() != null ? b.getInsuredValue().intValue() : 0).reduce(0, Integer::sum);
		return ResponseEntity.ok<HomeOverviewResponse>(
			HomeOverviewResponse(
				accountId = accountId,
				accountName = account.name ?: "Unbekannt",
				buildingCount = buildingCount,
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
	): ResponseEntity<MutableList<HomeActivityResponse?>?> {
		return ResponseEntity.ok<MutableList<HomeActivityResponse?>?>(mutableListOf<HomeActivityResponse?>())
		// 		Object tenantId = sessionContext.getTenantId();
// 		List<ObjBuilding> buildingList = buildingRepository.getAll(tenantId).stream().map(it -> buildingRepository.get(it)).toList();
// 		List<HomeActivityResponse> rrList = buildingList
// 				.stream()
// 				.filter(b -> b.getCurrentRating() != null && activeRatings.contains(b.getCurrentRating().getRatingStatus().getId()))
// 				.map(this::getRatingResponse)
// 				.toList();
// 		List<DocTask> taskList = taskRepository.getAll(tenantId).stream().map(it -> taskRepository.get(it)).toList();
// 		List<HomeActivityResponse> ttList = taskList
// 				.stream()
// 				.filter(b -> !"task.done".equals(b.getMeta().getCaseStage().getId()))
// 				.map(this::getTaskResponse)
// 				.toList();
// 		List<HomeActivityResponse> result = new ArrayList<>();
// 		result.addAll(rrList);
// 		result.addAll(ttList);
// 		return ResponseEntity.ok(result);
	}

	private fun getRatingResponse(building: ObjBuilding): HomeActivityResponse? {
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

	private fun getTaskResponse(task: DocTask): HomeActivityResponse? {
		val relatedToId = task.relatedToId as Int?
		val relatedTo = this.objRepository!!.get(relatedToId!!)
		return HomeActivityResponse(
			item = EnumeratedDto.of(task)!!,
			relatedTo = EnumeratedDto.of(relatedTo)!!,
			owner = EnumeratedDto.of(if (task.ownerId != null) userRepository!!.get(task.ownerId!!) else null),
			user = EnumeratedDto.of(if (task.assigneeId != null) userRepository!!.get(task.assigneeId!!) else null),
			dueAt = formatIsoDate(task.dueAt!!.toLocalDate()),
			subject = task.subject,
			content = task.content,
			priority = EnumeratedDto.of(task.priority),
		)
	}

	@GetMapping("/recentActions/{accountId}")
	fun getRecentActions(
		@PathVariable("accountId") accountId: Int?,
	): ResponseEntity<MutableList<HomeActionResponse?>?> {
		return ResponseEntity.ok<MutableList<HomeActionResponse?>?>(mutableListOf<HomeActionResponse?>())
		// 		ObjAccount account = this.accountRepository.get(accountId);
// 		Result<ActivityVRecord> result = this.dslContext
// 				.selectFrom(Tables.ACTIVITY_V)
// 				.where(
// 						Tables.ACTIVITY_V.TENANT_ID.eq((Integer) account.getTenantId())
// 								.and(Tables.ACTIVITY_V.ACCOUNT_ID.eq(accountId)))
// 				.orderBy(Tables.ACTIVITY_V.TIMESTAMP.desc())
// 				.limit(20)
// 				.fetch();
// 		return ResponseEntity.ok(result.stream().map(this::getActivityResponse).toList());
	}

	private fun getActivityResponse(a: ActivityVRecord): HomeActionResponse? {
		getAggregateType(a.aggregateTypeId)
		val item = EnumeratedDto.of(a.id.toString(), a.caption)
		// 		EnumeratedDto item = EnumeratedDto.builder()
// 				.id(a.getId().toString())
// 				.name(a.getCaption())
// 				.itemType(EnumeratedDto.of(type))
// 				.build();
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
			user = EnumeratedDto.of(this.userRepository.get(a.userId))!!,
			changes = null,
			oldCaseStage = EnumeratedDto.of(oldCaseStage),
			newCaseStage = EnumeratedDto.of(newCaseStage)!!,
		)
	}

	companion object {

		private val activeRatings = mutableListOf<String?>("open", "review")
	}

}
