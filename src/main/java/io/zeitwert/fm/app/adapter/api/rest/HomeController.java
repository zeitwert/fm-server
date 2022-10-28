
package io.zeitwert.fm.app.adapter.api.rest;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.obj.model.db.Tables;
import io.zeitwert.ddd.obj.model.db.tables.records.ObjActivityVRecord;
import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.util.Formatter;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.app.adapter.api.rest.dto.HomeActivityResponse;
import io.zeitwert.fm.app.adapter.api.rest.dto.HomeOverviewResponse;
import io.zeitwert.fm.app.adapter.api.rest.dto.HomeRatingResponse;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingVRecord;
import io.zeitwert.fm.portfolio.model.ObjPortfolio;
import io.zeitwert.fm.portfolio.model.ObjPortfolioRepository;
import io.zeitwert.fm.portfolio.model.db.tables.records.ObjPortfolioVRecord;

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

@RestController("homeController")
@RequestMapping("/rest/home")
public class HomeController {

	@Autowired
	DSLContext dslContext;

	@Autowired
	ObjUserRepository userRepository;

	@Autowired
	ObjAccountRepository accountRepository;

	@Autowired
	ObjBuildingRepository buildingRepository;

	@Autowired
	ObjPortfolioRepository portfolioRepository;

	@GetMapping("/overview/{accountId}")
	public ResponseEntity<HomeOverviewResponse> getOverview(@PathVariable("accountId") Integer accountId) {
		ObjAccount account = this.accountRepository.get(accountId);
		String accountImageUrl = "/account/" + account.getKey() + "/logo.jpg";
		List<ObjPortfolioVRecord> portfolioList = this.portfolioRepository.find(new QuerySpec(ObjPortfolio.class));
		List<ObjBuildingVRecord> buildingList = this.buildingRepository.find(new QuerySpec(ObjBuilding.class));
		Integer ratingCount = (int) buildingList.stream()
				.filter(b -> Objects.equals(b.getRatingStatusId(), "open") || Objects
						.equals(b.getRatingStatusId(), "review"))
				.count();
		Integer insuranceValue = buildingList.stream()
				.map(b -> b.getInsuredValue() != null ? b.getInsuredValue().intValue() : 0).reduce(0, (a, b) -> a + b);
		//@formatter:off
		return ResponseEntity.ok(
			HomeOverviewResponse.builder()
				.accountId(accountId)
				.accountName(account.getName())
				.accountImageUrl(accountImageUrl)
				.buildingCount(buildingList.size())
				.portfolioCount(portfolioList.size())
				.ratingCount(ratingCount)
				.insuranceValue(insuranceValue)
				.timeValue(null)
				.shortTermRenovationCosts(null)
				.midTermRenovationCosts(null)
				.build()
		);
		//@formatter:on
	}

	@GetMapping("/activeRatings/{accountId}")
	public ResponseEntity<List<HomeRatingResponse>> getActiveRatings(@PathVariable("accountId") Integer accountId) {
		List<ObjBuildingVRecord> buildingList = this.buildingRepository.find(new QuerySpec(ObjBuilding.class));
		//@formatter:off
		List<HomeRatingResponse> rrList = buildingList
			.stream()
			.filter(b -> Objects.equals(b.getRatingStatusId(), "open") || Objects.equals(b.getRatingStatusId(), "review"))
			.map(b -> this.getRatingResponse(b))
			.toList();
		return ResponseEntity.ok(rrList);
		//@formatter:on
	}

	private HomeRatingResponse getRatingResponse(ObjBuildingVRecord record) {
		//@formatter:off
		return HomeRatingResponse.builder()
			.buildingId(record.getId())
			.buildingName(record.getName())
			.buildingOwner(userRepository.get(record.getOwnerId()).getCaption())
			.buildingAddress(record.getStreet() + " " + record.getZip() + " " + record.getCity())
			.ratingDate(Formatter.INSTANCE.formatDate(record.getRatingDate()))
			.ratingUser(record.getRatingUserId() != null ? userRepository.get(record.getRatingUserId()).getCaption() : null)
		.build();
		//@formatter:on
	}

	@GetMapping("/recentActivity/{accountId}")
	public ResponseEntity<List<HomeActivityResponse>> getRecentActivity(@PathVariable("accountId") Integer accountId) {
		ObjAccount account = this.accountRepository.get(accountId);
		Result<ObjActivityVRecord> result = this.dslContext.selectFrom(Tables.OBJ_ACTIVITY_V)
				.where(Tables.OBJ_ACTIVITY_V.TENANT_ID.eq(account.getTenantId())
						.and(Tables.OBJ_ACTIVITY_V.ACCOUNT_ID.eq(accountId)))
				.limit(20).fetch();
		return ResponseEntity.ok(result.stream().map(record -> this.getActivityResponse(record)).toList());
	}

	private HomeActivityResponse getActivityResponse(ObjActivityVRecord record) {
		return HomeActivityResponse.builder()
				.objTypeId(record.getObjTypeId())
				.objId(record.getId())
				.objCaption(record.getCaption())
				.seqNr(record.getSeqNr())
				.timestamp(record.getTimestamp())
				.user(this.userRepository.get(record.getUserId()).getCaption())
				.changes(null)
				.build();
	}

}
