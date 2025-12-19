package io.zeitwert.fm.building;

import io.zeitwert.dddrive.app.model.RequestContext;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.account.model.enums.CodeCurrency;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartElementRating;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.building.model.enums.*;
import io.zeitwert.fm.oe.model.ObjUserFM;
import io.zeitwert.fm.oe.model.enums.CodeCountry;
import io.zeitwert.test.TestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test")
public class BuildingTest {

	@Autowired
	private RequestContext requestCtx;

	@Autowired
	private ObjAccountRepository accountRepo;

	@Autowired
	private ObjBuildingRepository buildingRepository;

	@Test
	public void testBuilding() throws Exception {

		assertNotNull(this.buildingRepository, "buildingRepository not null");
		assertEquals("obj_building", this.buildingRepository.getAggregateType().getId());

		ObjAccount account = this.getTestAccount(requestCtx);
		ObjBuilding buildingA1 = this.buildingRepository.create(requestCtx.getTenantId(), requestCtx.getUserId(), requestCtx.getCurrentTime());

		assertNotNull(buildingA1, "test not null");
		assertNotNull(buildingA1.getId(), "id not null");
		assertNotNull(buildingA1.getTenant(), "tenant not null");

		Integer buildingA_id = (Integer) buildingA1.getId();
		Integer buildingA_idHash = System.identityHashCode(buildingA1);

		assertNotNull(buildingA1.getMeta().getCreatedByUser(), "createdByUser not null");
		assertNotNull(buildingA1.getMeta().getCreatedAt(), "createdAt not null");

		buildingA1.accountId = account.getId();
		this.initBuilding(buildingA1);
		assertEquals(account.getId(), buildingA1.accountId, "account id");
		assertEquals(account.getId(), buildingA1.account.getId(), "account id");

		assertEquals(22, buildingA1.currentRating.getElementCount(), "element count 22");
		assertEquals(22, buildingA1.currentRating.getElementList().size(), "element count 22");
		assertEquals(100, buildingA1.currentRating.getElementWeights(), "element contributions 100");

		CodeBuildingPart bp1 = CodeBuildingPart.P2;
		ObjBuildingPartElementRating e1 = buildingA1.currentRating.getElement(bp1);
		e1.condition = 100;
		e1.ratingYear = 2000;
		e1.weight = 50;
		Integer e1id = e1.getId();

		assertEquals(22, buildingA1.currentRating.getElementCount(), "element count 22");
		assertEquals(22, buildingA1.currentRating.getElementList().size(), "element count 22");
		assertEquals(e1, buildingA1.currentRating.getElementById(e1id), "e1 by id");
		assertEquals(e1, buildingA1.currentRating.getElement(bp1), "e1 by buildingPart");
		// assertEquals(50, building1a.getCurrentRating().getElementContributions(), 50,
		// "element contributions 50");

		CodeBuildingPart bp2 = CodeBuildingPart.P3;
		ObjBuildingPartElementRating e2 = buildingA1.currentRating.getElement(bp2);
		e2.condition = 100;
		e2.ratingYear = 2000;
		e2.weight = 50;
		Integer e2id = e2.getId();

		this.checkBuilding(buildingA1);
		assertEquals(e1, buildingA1.currentRating.getElementById(e1id), "e1 by id");
		assertEquals(e2, buildingA1.currentRating.getElementById(e2id), "e2 by id");
		assertEquals(e1, buildingA1.currentRating.getElement(bp1), "e1 by buildingPart");
		assertEquals(e2, buildingA1.currentRating.getElement(bp2), "e2 by buildingPart");

		this.buildingRepository.store(buildingA1, requestCtx.getUserId(), requestCtx.getCurrentTime());
		buildingA1 = null;

		ObjBuilding buildingA2 = this.buildingRepository.load(buildingA_id);
		Integer buildingA2_idHash = System.identityHashCode(buildingA2);
		assertNotEquals(buildingA_idHash, buildingA2_idHash);
		assertNotNull(buildingA2.getMeta().getModifiedByUser(), "modifiedByUser not null");
		assertNotNull(buildingA2.getMeta().getModifiedAt(), "modifiedAt not null");
		assertEquals(account.getId(), buildingA2.accountId, "account id");
		assertEquals(account.getId(), buildingA2.account.getId(), "account id");

		this.checkBuilding(buildingA2);
		assertEquals(bp1, buildingA2.currentRating.getElementById(e1id).getBuildingPart(), "e1 by id");
		assertEquals(bp2, buildingA2.currentRating.getElementById(e2id).getBuildingPart(), "e2 by id");
		assertEquals(bp1, buildingA2.currentRating.getElement(bp1).getBuildingPart(), "e1 by buildingPart");
		assertEquals(bp2, buildingA2.currentRating.getElement(bp2).getBuildingPart(), "e2 by buildingPart");

		CodeBuildingPart bp3 = CodeBuildingPart.P4;
		ObjBuildingPartElementRating e3 = buildingA2.currentRating.getElement(bp3);
		e3.condition = 100;
		e3.ratingYear = 2000;
		e3.weight = 50;
		Integer e3id = e3.getId();

		buildingA2.currentRating.removeElement(e2id);

		assertEquals(21, buildingA2.currentRating.getElementCount(), "element count 22");
		assertEquals(21, buildingA2.currentRating.getElementList().size(), "element count 22");
		// assertEquals(building1b.getCurrentRating().getElementContributions(), 100,
		// "element contributions 100");
		assertEquals(bp1, buildingA2.currentRating.getElementById(e1id).getBuildingPart(), "e1 by id");
		assertEquals(bp3, buildingA2.currentRating.getElementById(e3id).getBuildingPart(), "e3 by id");
		assertEquals(bp1, buildingA2.currentRating.getElement(bp1).getBuildingPart(), "e1 by buildingPart");
		assertEquals(bp3, buildingA2.currentRating.getElement(bp3).getBuildingPart(), "e3 by buildingPart");

		this.buildingRepository.store(buildingA2, requestCtx.getUserId(), requestCtx.getCurrentTime());
		buildingA2 = null;

		ObjBuilding buildingA3 = this.buildingRepository.get(buildingA_id);

		assertEquals(21, buildingA3.currentRating.getElementCount(), "element count 22");
		assertEquals(21, buildingA3.currentRating.getElementList().size(), "element count 22");
		// assertEquals(building1c.getCurrentRating().getElementContributions(), 100,
		// "element contributions 100");
		assertEquals(bp1, buildingA3.currentRating.getElementById(e1id).getBuildingPart(), "e1 by id");
		assertEquals(bp3, buildingA3.currentRating.getElementById(e3id).getBuildingPart(), "e3 by id");
		assertEquals(bp1, buildingA3.currentRating.getElement(bp1).getBuildingPart(), "e1 by buildingPart");
		assertEquals(bp3, buildingA3.currentRating.getElement(bp3).getBuildingPart(), "e3 by buildingPart");

	}

	private ObjAccount getTestAccount(RequestContext requestCtx) {
		return this.accountRepo.get(this.accountRepo.getAll(requestCtx.getTenantId()).get(0));
	}

	private void initBuilding(ObjBuilding building) {

		building.buildingNr = "B1";
		building.insuranceNr = "BI1";
		building.plotNr = "P1";
		building.nationalBuildingId = "NB1";

		building.street = "Teststrasse 10";
		building.zip = "1111";
		building.city = "Testingen";
		building.country = CodeCountry.getCountry("ch");
		building.currency = CodeCurrency.CHF;

		building.volume = BigDecimal.valueOf(1000.0);
		building.areaGross = BigDecimal.valueOf(100.0);
		building.areaNet = BigDecimal.valueOf(90.0);
		building.nrOfFloorsAboveGround = 3;
		building.nrOfFloorsBelowGround = 1;

		building.buildingType = CodeBuildingType.T01;
		building.buildingSubType = CodeBuildingSubType.ST05_26;
		building.buildingYear = 1985;

		building.insuredValue = BigDecimal.valueOf(1000000.0);
		building.insuredValueYear = 2000;
		building.notInsuredValue = BigDecimal.valueOf(0.0);
		building.notInsuredValueYear = 2000;
		building.thirdPartyValue = BigDecimal.valueOf(0.0);
		building.thirdPartyValueYear = 2000;

		building.addRating((ObjUserFM) requestCtx.getUser(), requestCtx.getCurrentTime());
		building.currentRating.setPartCatalog(CodeBuildingPartCatalog.C6);
		building.currentRating.setMaintenanceStrategy(CodeBuildingMaintenanceStrategy.N);

	}

	private void checkBuilding(ObjBuilding building) {
		assertEquals("B1", building.buildingNr);
		assertEquals("BI1", building.insuranceNr);
		assertEquals("P1", building.plotNr);
		assertEquals("NB1", building.nationalBuildingId);

		assertEquals("Teststrasse 10", building.street);
		assertEquals("1111", building.zip);
		assertEquals("Testingen", building.city);
		assertEquals(CodeCountry.getCountry("ch"), building.country);
		assertEquals(CodeCurrency.CHF, building.currency);

		assertEquals(BigDecimal.valueOf(1000.0), building.volume);
		assertEquals(BigDecimal.valueOf(100.0), building.areaGross);
		assertEquals(BigDecimal.valueOf(90.0), building.areaNet);
		assertEquals(3, building.nrOfFloorsAboveGround);
		assertEquals(1, building.nrOfFloorsBelowGround);

		assertEquals(CodeBuildingType.T01, building.buildingType);
		assertEquals(CodeBuildingSubType.ST05_26, building.buildingSubType);
		assertEquals(1985, building.buildingYear);

		assertEquals(BigDecimal.valueOf(1000000.0), building.insuredValue);
		assertEquals(2000, building.insuredValueYear);
		assertEquals(BigDecimal.valueOf(0.0), building.notInsuredValue);
		assertEquals(2000, building.notInsuredValueYear);
		assertEquals(BigDecimal.valueOf(0.0), building.thirdPartyValue);
		assertEquals(2000, building.thirdPartyValueYear);

		assertEquals(CodeBuildingMaintenanceStrategy.N, building.currentRating.getMaintenanceStrategy());
		assertEquals(CodeBuildingPartCatalog.C6, building.currentRating.getPartCatalog());

		assertEquals(22, building.currentRating.getElementCount(), "element count 22");
		assertEquals(22, building.currentRating.getElementList().size(), "element count 22");
		// assertEquals(100, building.getCurrentRating().getElementContributions(),
		// "element contributions 100");
	}

}
