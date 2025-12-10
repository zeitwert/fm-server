
package io.zeitwert.fm.app.adapter.api.rest;

import static io.dddrive.util.Invariant.assertThis;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.crnk.core.queryspec.QuerySpec;
import io.dddrive.ddd.model.base.AggregateRepositorySPI;
import io.dddrive.ddd.model.enums.CodeAggregateType;
import io.dddrive.ddd.model.enums.CodeAggregateTypeEnum;
import io.dddrive.doc.model.enums.CodeCaseStage;
import io.dddrive.doc.model.enums.CodeCaseStageEnum;
import io.dddrive.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.dddrive.obj.model.Obj;
import io.dddrive.oe.service.api.ObjUserCache;
import io.dddrive.util.Formatter;
import io.zeitwert.fm.collaboration.model.db.Tables;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.service.api.ObjAccountCache;
import io.zeitwert.fm.app.adapter.api.rest.dto.HomeActionResponse;
import io.zeitwert.fm.app.adapter.api.rest.dto.HomeOverviewResponse;
import io.zeitwert.fm.app.adapter.api.rest.dto.HomeActivityResponse;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingVRecord;
import io.zeitwert.fm.building.service.api.ObjBuildingCache;
import io.zeitwert.fm.collaboration.model.db.tables.records.ActivityVRecord;
import io.zeitwert.fm.obj.service.api.ObjVCache;
import io.zeitwert.fm.portfolio.model.ObjPortfolio;
import io.zeitwert.fm.portfolio.model.ObjPortfolioRepository;
import io.zeitwert.fm.portfolio.model.db.tables.records.ObjPortfolioVRecord;
import io.zeitwert.fm.task.model.DocTask;
import io.zeitwert.fm.task.model.DocTaskRepository;
import io.zeitwert.fm.task.model.db.tables.records.DocTaskVRecord;
import io.zeitwert.fm.task.model.enums.CodeTaskPriorityEnum;
import io.zeitwert.fm.task.service.api.DocTaskCache;

@RestController("homeController")
@RequestMapping("/rest/home")
public class HomeController {

	@Autowired
	DSLContext dslContext;

	@Autowired
	ObjUserCache userCache;

	@Autowired
	ObjAccountCache accountCache;

	@Autowired
	ObjBuildingCache buildingCache;

	@Autowired
	ObjBuildingRepository buildingRepository;

	@Autowired
	ObjPortfolioRepository portfolioRepository;

	@Autowired
	DocTaskCache taskCache;

	@Autowired
	DocTaskRepository taskRepository;

	@Autowired
	ObjVCache objCache;

	@GetMapping("/overview/{accountId}")
	public ResponseEntity<HomeOverviewResponse> getOverview(@PathVariable("accountId") Integer accountId) {
		ObjAccount account = this.accountCache.get(accountId);
		List<ObjPortfolioVRecord> portfolioList = this.portfolioRepository.find(new QuerySpec(ObjPortfolio.class));
		List<ObjBuildingVRecord> buildingList = this.buildingRepository.find(new QuerySpec(ObjBuilding.class));
		Integer ratingCount = (int) buildingList.stream()
				.filter(b -> Objects.equals(b.getRatingStatusId(), "open") || Objects
						.equals(b.getRatingStatusId(), "review"))
				.count();
		Integer insuranceValue = buildingList.stream()
				.map(b -> b.getInsuredValue() != null ? b.getInsuredValue().intValue() : 0).reduce(0, (a, b) -> a + b);
		return ResponseEntity.ok(
				HomeOverviewResponse.builder()
						.accountId(accountId)
						.accountName(account.getName())
						.buildingCount(buildingList.size())
						.portfolioCount(portfolioList.size())
						.ratingCount(ratingCount)
						.insuranceValue(insuranceValue)
						.timeValue(null)
						.shortTermRenovationCosts(null)
						.midTermRenovationCosts(null)
						.build());
	}

	@GetMapping("/openActivities/{accountId}")
	public ResponseEntity<List<HomeActivityResponse>> getOpenActivities(@PathVariable("accountId") Integer accountId) {
		List<ObjBuildingVRecord> buildingList = this.buildingRepository.find(new QuerySpec(ObjBuilding.class));
		List<HomeActivityResponse> rrList = buildingList
				.stream()
				.filter(b -> Objects.equals(b.getRatingStatusId(), "open") || Objects.equals(b.getRatingStatusId(), "review"))
				.map(b -> this.getRatingResponse(b))
				.toList();
		List<DocTaskVRecord> taskList = this.taskRepository.find(new QuerySpec(DocTask.class));
		List<HomeActivityResponse> ttList = taskList
				.stream()
				.filter(b -> !"task.done".equals(b.getCaseStageId()))
				.map(b -> this.getTaskResponse(b))
				.toList();
		List<HomeActivityResponse> result = new ArrayList<>();
		result.addAll(rrList);
		result.addAll(ttList);
		return ResponseEntity.ok(result);
	}

	private HomeActivityResponse getRatingResponse(ObjBuildingVRecord record) {
		ObjBuilding building = this.buildingCache.get(record.getId());
		String address = record.getStreet() + "\n" + record.getZip() + " " + record.getCity();
		return HomeActivityResponse.builder()
				.item(EnumeratedDto.fromObj(building))
				.relatedTo(EnumeratedDto.fromObj(building))
				.owner(EnumeratedDto
						.fromObj(record.getRatingUserId() != null ? this.userCache.get(record.getRatingUserId()) : null))
				.user(EnumeratedDto
						.fromObj(record.getRatingUserId() != null ? this.userCache.get(record.getRatingUserId()) : null))
				.dueAt(Formatter.INSTANCE.formatIsoDate(record.getRatingDate()))
				.subject(building.getName())
				.content(address)
				.priority(EnumeratedDto.fromEnum(CodeTaskPriorityEnum.getPriority("normal")))
				.build();
	}

	private HomeActivityResponse getTaskResponse(DocTaskVRecord record) {
		DocTask task = this.taskCache.get(record.getId());
		Integer relatedToId = record.getRelatedObjId() != null ? record.getRelatedObjId() : record.getRelatedDocId();
		assertThis(((AggregateRepositorySPI<?, ?>) this.taskRepository).getIdProvider().isObjId(relatedToId),
				"only obj supported yet");
		Obj relatedTo = this.objCache.get(relatedToId);
		return HomeActivityResponse.builder()
				.item(EnumeratedDto.fromDoc(task))
				.relatedTo(EnumeratedDto.fromObj(relatedTo))
				.owner(EnumeratedDto.fromObj(record.getOwnerId() != null ? this.userCache.get(record.getOwnerId()) : null))
				.user(EnumeratedDto.fromObj(record.getAssigneeId() != null ? this.userCache.get(record.getAssigneeId()) : null))
				.dueAt(Formatter.INSTANCE.formatIsoDate(record.getDueAt().toLocalDate()))
				.subject(record.getSubject())
				.content(record.getContent())
				.priority(EnumeratedDto.fromEnum(CodeTaskPriorityEnum.getPriority(record.getPriorityId())))
				.build();
	}

	@GetMapping("/recentActions/{accountId}")
	public ResponseEntity<List<HomeActionResponse>> getRecentActions(@PathVariable("accountId") Integer accountId) {
		ObjAccount account = this.accountCache.get(accountId);
		Result<ActivityVRecord> result = this.dslContext
				.selectFrom(Tables.ACTIVITY_V)
				.where(
						Tables.ACTIVITY_V.TENANT_ID.eq(account.getTenantId())
								.and(Tables.ACTIVITY_V.ACCOUNT_ID.eq(accountId)))
				.orderBy(Tables.ACTIVITY_V.TIMESTAMP.desc())
				.limit(20)
				.fetch();
		return ResponseEntity.ok(result.stream().map(record -> this.getActivityResponse(record)).toList());
	}

	private HomeActionResponse getActivityResponse(ActivityVRecord a) {
		CodeAggregateType type = CodeAggregateTypeEnum.getAggregateType(a.getAggregateTypeId());
		EnumeratedDto item = EnumeratedDto.builder()
				.id(a.getId().toString())
				.name(a.getCaption())
				.itemType(EnumeratedDto.fromEnum(type))
				.build();
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
				.user(EnumeratedDto.fromObj(this.userCache.get(a.getUserId())))
				.changes(null)
				.oldCaseStage(EnumeratedDto.fromEnum(oldCaseStage))
				.newCaseStage(EnumeratedDto.fromEnum(newCaseStage))
				.build();
	}

}
