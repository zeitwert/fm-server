package io.zeitwert.fm.app.adapter.api.rest;

import dddrive.app.doc.model.enums.CodeCaseStage;
import dddrive.app.doc.model.enums.CodeCaseStageEnum;
import dddrive.ddd.core.model.enums.CodeAggregateType;
import dddrive.ddd.core.model.enums.CodeAggregateTypeEnum;
import dddrive.ddd.core.model.Aggregate;
import io.dddrive.oe.model.ObjUser;
import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.app.adapter.api.rest.dto.HomeActionResponse;
import io.zeitwert.fm.app.adapter.api.rest.dto.HomeActivityResponse;
import io.zeitwert.fm.app.adapter.api.rest.dto.HomeOverviewResponse;
import io.zeitwert.fm.app.model.RequestContextFM;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartRating;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.collaboration.model.db.tables.records.ActivityVRecord;
import io.zeitwert.fm.oe.model.ObjUserFMRepository;
import io.zeitwert.fm.portfolio.model.ObjPortfolioRepository;
import io.zeitwert.fm.task.model.DocTask;
import io.zeitwert.fm.task.model.DocTaskRepository;
import io.zeitwert.fm.task.model.enums.CodeTaskPriority;
import io.zeitwert.fm.util.Formatter;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@RestController("homeController")
@RequestMapping("/rest/home")
public class HomeController {

	private static final List<String> activeRatings = Arrays.asList("open", "review");
	@Autowired
	DSLContext dslContext;
	@Autowired
	ObjUserFMRepository userRepository;
	@Autowired
	ObjAccountRepository accountRepository;
	@Autowired
	ObjBuildingRepository buildingRepository;
	@Autowired
	ObjPortfolioRepository portfolioRepository;
	@Autowired
	DocTaskRepository taskRepository;
	@Autowired
	private RequestContextFM requestContext;

	@GetMapping("/overview/{accountId}")
	public ResponseEntity<HomeOverviewResponse> getOverview(@PathVariable("accountId") Integer accountId) {
		ObjAccount account = accountRepository.get(accountId);
		Object tenantId = requestContext.getTenantId();
		Integer portfolioCount = portfolioRepository.find(null).size();
		Integer buildingCount = buildingRepository.find(null).size();
//		List<ObjBuilding> buildings = buildingRepository.getAll(tenantId).stream().map(it -> buildingRepository.get(it)).toList();
		Integer ratingCount = 0;
		Integer insuranceValue = 0;
//		Integer ratingCount = (int) buildings.stream()
//				.filter(b -> b.getCurrentRating() != null && activeRatings.contains(b.getCurrentRating().getRatingStatus().getId()))
//				.count();
//		Integer insuranceValue = buildings.stream()
//				.map(b -> b.getInsuredValue() != null ? b.getInsuredValue().intValue() : 0).reduce(0, Integer::sum);
		return ResponseEntity.ok(
				HomeOverviewResponse.builder()
						.accountId(accountId)
						.accountName(account.getName())
						.buildingCount(buildingCount)
						.portfolioCount(portfolioCount)
						.ratingCount(ratingCount)
						.insuranceValue(insuranceValue)
						.timeValue(null)
						.shortTermRenovationCosts(null)
						.midTermRenovationCosts(null)
						.build());
	}

	@GetMapping("/openActivities/{accountId}")
	public ResponseEntity<List<HomeActivityResponse>> getOpenActivities(@PathVariable("accountId") Integer accountId) {
		return ResponseEntity.ok(List.of());
//		Object tenantId = requestContext.getTenantId();
//		List<ObjBuilding> buildingList = buildingRepository.getAll(tenantId).stream().map(it -> buildingRepository.get(it)).toList();
//		List<HomeActivityResponse> rrList = buildingList
//				.stream()
//				.filter(b -> b.getCurrentRating() != null && activeRatings.contains(b.getCurrentRating().getRatingStatus().getId()))
//				.map(this::getRatingResponse)
//				.toList();
//		List<DocTask> taskList = taskRepository.getAll(tenantId).stream().map(it -> taskRepository.get(it)).toList();
//		List<HomeActivityResponse> ttList = taskList
//				.stream()
//				.filter(b -> !"task.done".equals(b.getMeta().getCaseStage().getId()))
//				.map(this::getTaskResponse)
//				.toList();
//		List<HomeActivityResponse> result = new ArrayList<>();
//		result.addAll(rrList);
//		result.addAll(ttList);
//		return ResponseEntity.ok(result);
	}

	private HomeActivityResponse getRatingResponse(ObjBuilding building) {
		String address = building.getStreet() + "\n" + building.getZip() + " " + building.getCity();
		ObjBuildingPartRating rating = building.getCurrentRating();
		ObjUser ratingUser = rating != null ? rating.getRatingUser() : null;
		EnumeratedDto ratingUserDto = EnumeratedDto.of(ratingUser);
		LocalDate ratingDate = rating != null ? rating.getRatingDate() : null;
		return HomeActivityResponse.builder()
				.item(EnumeratedDto.of(building))
				.relatedTo(EnumeratedDto.of(building))
				.owner(ratingUserDto)
				.user(ratingUserDto)
				.dueAt(Formatter.INSTANCE.formatIsoDate(ratingDate))
				.subject(building.getName())
				.content(address)
				.priority(EnumeratedDto.of(CodeTaskPriority.NORMAL))
				.build();
	}

	private HomeActivityResponse getTaskResponse(DocTask task) {
		Aggregate relatedTo = task.getRelatedTo();
//
//		Integer relatedToId = task.getRelatedToId();
//		assertThis(((AggregateRepositorySPI<?>) this.taskRepository).getIdProvider().isObjId(relatedToId),
//				"only obj supported yet");
//		Obj relatedTo = this.objCache.get(relatedToId);
		return HomeActivityResponse.builder()
				.item(EnumeratedDto.of(task))
				.relatedTo(EnumeratedDto.of(relatedTo))
				.owner(EnumeratedDto.of(task.getOwner() != null ? task.getOwner() : null))
				.user(EnumeratedDto.of(task.getAssignee() != null ? task.getAssignee() : null))
				.dueAt(Formatter.INSTANCE.formatIsoDate(task.getDueAt().toLocalDate()))
				.subject(task.getSubject())
				.content(task.getContent())
				.priority(EnumeratedDto.of(task.getPriority()))
				.build();
	}

	@GetMapping("/recentActions/{accountId}")
	public ResponseEntity<List<HomeActionResponse>> getRecentActions(@PathVariable("accountId") Integer accountId) {
		return ResponseEntity.ok(List.of());
//		ObjAccount account = this.accountRepository.get(accountId);
//		Result<ActivityVRecord> result = this.dslContext
//				.selectFrom(Tables.ACTIVITY_V)
//				.where(
//						Tables.ACTIVITY_V.TENANT_ID.eq((Integer) account.getTenantId())
//								.and(Tables.ACTIVITY_V.ACCOUNT_ID.eq(accountId)))
//				.orderBy(Tables.ACTIVITY_V.TIMESTAMP.desc())
//				.limit(20)
//				.fetch();
//		return ResponseEntity.ok(result.stream().map(this::getActivityResponse).toList());
	}

	private HomeActionResponse getActivityResponse(ActivityVRecord a) {
		CodeAggregateType type = CodeAggregateTypeEnum.getAggregateType(a.getAggregateTypeId());
		EnumeratedDto item = EnumeratedDto.of(a.getId().toString(), a.getCaption());
//		EnumeratedDto item = EnumeratedDto.builder()
//				.id(a.getId().toString())
//				.name(a.getCaption())
//				.itemType(EnumeratedDto.of(type))
//				.build();
		CodeCaseStage oldCaseStage = a.getOldCaseStageId() != null
				? CodeCaseStageEnum.getCaseStage(a.getOldCaseStageId())
				: null;
		CodeCaseStage newCaseStage = a.getNewCaseStageId() != null
				? CodeCaseStageEnum.getCaseStage(a.getNewCaseStageId())
				: null;
		return HomeActionResponse.builder()
				.item(item)
				.seqNr(a.getSeqNr())
				.timestamp(a.getTimestamp())
				.user(EnumeratedDto.of(this.userRepository.get(a.getUserId())))
				.changes(null)
				.oldCaseStage(EnumeratedDto.of(oldCaseStage))
				.newCaseStage(EnumeratedDto.of(newCaseStage))
				.build();
	}

}
