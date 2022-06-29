
package io.zeitwert.fm.building;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.account.model.enums.CodeAccountTypeEnum;
import io.zeitwert.fm.account.model.enums.CodeCountryEnum;
import io.zeitwert.fm.account.model.enums.CodeCurrencyEnum;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartElementRating;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.building.model.enums.CodeBuildingMaintenanceStrategyEnum;
import io.zeitwert.fm.building.model.enums.CodeBuildingPart;
import io.zeitwert.fm.building.model.enums.CodeBuildingPartCatalogEnum;
import io.zeitwert.fm.building.model.enums.CodeBuildingPartEnum;
import io.zeitwert.fm.building.model.enums.CodeBuildingSubTypeEnum;
import io.zeitwert.fm.building.model.enums.CodeBuildingTypeEnum;
import io.zeitwert.server.Application;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class BuildingTest {

	private static final String ACCT_KEY = "##test##building";

	@Autowired
	private SessionInfo sessionInfo;

	@Autowired
	private ObjAccountRepository accountRepository;

	@Autowired
	private ObjBuildingRepository buildingRepository;

	@Test
	public void testBuilding() throws Exception {

		assertTrue(buildingRepository != null, "buildingRepository not null");
		assertEquals("obj_building", buildingRepository.getAggregateType().getId());

		ObjAccount account = this.getOrCreateTestAccount(sessionInfo);
		ObjBuilding building1a = buildingRepository.create(sessionInfo);

		assertNotNull(building1a, "test not null");
		assertNotNull(building1a.getId(), "id not null");
		assertNotNull(building1a.getTenant(), "tenant not null");

		Integer building1Id = building1a.getId();
		Integer building1aIdHash = System.identityHashCode(building1a);

		assertNotNull(building1a.getMeta().getCreatedByUser(), "createdByUser not null");
		assertNotNull(building1a.getMeta().getCreatedAt(), "createdAt not null");

		building1a.setAccountId(account.getId());
		this.initBuilding(building1a);

		assertEquals(22, building1a.getCurrentRating().getElementCount(), "element count 22");
		assertEquals(22, building1a.getCurrentRating().getElementList().size(), "element count 22");
		assertEquals(100, building1a.getCurrentRating().getElementContributions(), "element contributions 100");

		CodeBuildingPart bp1 = CodeBuildingPartEnum.getBuildingPart("P2");
		ObjBuildingPartElementRating e1 = building1a.getCurrentRating().getElement(bp1);
		e1.setCondition(100);
		e1.setConditionYear(2000);
		e1.setValuePart(50);
		Integer e1id = e1.getId();

		assertEquals(22, building1a.getCurrentRating().getElementCount(), "element count 22");
		assertEquals(22, building1a.getCurrentRating().getElementList().size(), "element count 22");
		assertEquals(e1, building1a.getCurrentRating().getElementById(e1id), "e1 by id");
		assertEquals(e1, building1a.getCurrentRating().getElement(bp1), "e1 by buildingPart");
		// assertEquals(50, building1a.getCurrentRating().getElementContributions(), 50,
		// "element contributions 50");

		CodeBuildingPart bp2 = CodeBuildingPartEnum.getBuildingPart("P3");
		ObjBuildingPartElementRating e2 = building1a.getCurrentRating().getElement(bp2);
		e2.setCondition(100);
		e2.setConditionYear(2000);
		e2.setValuePart(50);
		Integer e2id = e2.getId();

		this.checkBuilding(building1a);
		assertEquals(e1, building1a.getCurrentRating().getElementById(e1id), "e1 by id");
		assertEquals(e2, building1a.getCurrentRating().getElementById(e2id), "e2 by id");
		assertEquals(e1, building1a.getCurrentRating().getElement(bp1), "e1 by buildingPart");
		assertEquals(e2, building1a.getCurrentRating().getElement(bp2), "e2 by buildingPart");

		this.buildingRepository.store(building1a);
		building1a = null;

		ObjBuilding building1b = buildingRepository.get(sessionInfo, building1Id);
		Integer building1bIdHash = System.identityHashCode(building1b);
		assertNotEquals(building1aIdHash, building1bIdHash);
		assertNotNull(building1b.getMeta().getModifiedByUser(), "modifiedByUser not null");
		assertNotNull(building1b.getMeta().getModifiedAt(), "modifiedAt not null");

		this.checkBuilding(building1b);
		assertEquals(bp1, building1b.getCurrentRating().getElementById(e1id).getBuildingPart(), "e1 by id");
		assertEquals(bp2, building1b.getCurrentRating().getElementById(e2id).getBuildingPart(), "e2 by id");
		assertEquals(bp1, building1b.getCurrentRating().getElement(bp1).getBuildingPart(), "e1 by buildingPart");
		assertEquals(bp2, building1b.getCurrentRating().getElement(bp2).getBuildingPart(), "e2 by buildingPart");

		CodeBuildingPart bp3 = CodeBuildingPartEnum.getBuildingPart("P4");
		ObjBuildingPartElementRating e3 = building1b.getCurrentRating().getElement(bp3);
		e3.setCondition(100);
		e3.setConditionYear(2000);
		e3.setValuePart(50);
		Integer e3id = e3.getId();

		building1b.getCurrentRating().removeElement(e2id);

		assertEquals(21, building1b.getCurrentRating().getElementCount(), "element count 22");
		assertEquals(21, building1b.getCurrentRating().getElementList().size(), "element count 22");
		// assertEquals(building1b.getCurrentRating().getElementContributions(), 100,
		// "element contributions 100");
		assertEquals(bp1, building1b.getCurrentRating().getElementById(e1id).getBuildingPart(), "e1 by id");
		assertEquals(bp3, building1b.getCurrentRating().getElementById(e3id).getBuildingPart(), "e3 by id");
		assertEquals(bp1, building1b.getCurrentRating().getElement(bp1).getBuildingPart(), "e1 by buildingPart");
		assertEquals(bp3, building1b.getCurrentRating().getElement(bp3).getBuildingPart(), "e3 by buildingPart");

		this.buildingRepository.store(building1b);
		building1b = null;

		ObjBuilding building1c = buildingRepository.get(sessionInfo, building1Id);

		assertEquals(21, building1c.getCurrentRating().getElementCount(), "element count 22");
		assertEquals(21, building1c.getCurrentRating().getElementList().size(), "element count 22");
		// assertEquals(building1c.getCurrentRating().getElementContributions(), 100,
		// "element contributions 100");
		assertEquals(bp1, building1c.getCurrentRating().getElementById(e1id).getBuildingPart(), "e1 by id");
		assertEquals(bp3, building1c.getCurrentRating().getElementById(e3id).getBuildingPart(), "e3 by id");
		assertEquals(bp1, building1c.getCurrentRating().getElement(bp1).getBuildingPart(), "e1 by buildingPart");
		assertEquals(bp3, building1c.getCurrentRating().getElement(bp3).getBuildingPart(), "e3 by buildingPart");

	}

	private ObjAccount getOrCreateTestAccount(SessionInfo sessionInfo) {
		Optional<ObjAccount> maybeAccount = this.accountRepository.getByKey(sessionInfo, ACCT_KEY);
		if (maybeAccount.isPresent()) {
			return maybeAccount.get();
		}
		ObjAccount account = this.accountRepository.create(sessionInfo);
		account.setName("Building Test Account");
		account.setAccountType(CodeAccountTypeEnum.getAccountType("client"));
		account.setReferenceCurrency(CodeCurrencyEnum.getCurrency("chf"));
		this.accountRepository.store(account);
		return this.accountRepository.get(sessionInfo, account.getId());
	}

	private void initBuilding(ObjBuilding building) {

		building.setBuildingNr("B1");
		building.setInsuranceNr("BI1");
		building.setPlotNr("P1");
		building.setNationalBuildingId("NB1");

		building.setStreet("Teststrasse 10");
		building.setZip("1111");
		building.setCity("Testingen");
		building.setCountry(CodeCountryEnum.getCountry("ch"));
		building.setCurrency(CodeCurrencyEnum.getCurrency("chf"));

		building.setVolume(BigDecimal.valueOf(1000.0));
		building.setAreaGross(BigDecimal.valueOf(100.0));
		building.setAreaNet(BigDecimal.valueOf(90.0));
		building.setNrOfFloorsAboveGround(3);
		building.setNrOfFloorsBelowGround(1);

		building.setBuildingType(CodeBuildingTypeEnum.getBuildingType("T01"));
		building.setBuildingSubType(CodeBuildingSubTypeEnum.getBuildingSubType("ST05-26"));
		building.setBuildingYear(1985);

		building.setInsuredValue(BigDecimal.valueOf(1000000.0));
		building.setInsuredValueYear(2000);
		building.setNotInsuredValue(BigDecimal.valueOf(0.0));
		building.setNotInsuredValueYear(2000);
		building.setThirdPartyValue(BigDecimal.valueOf(0.0));
		building.setThirdPartyValueYear(2000);

		building.addRating();
		building.getCurrentRating().setPartCatalog(CodeBuildingPartCatalogEnum.getPartCatalog("C6"));
		building.getCurrentRating().setMaintenanceStrategy(CodeBuildingMaintenanceStrategyEnum.getMaintenanceStrategy("N"));

	}

	private void checkBuilding(ObjBuilding building) {
		assertEquals("B1", building.getBuildingNr());
		assertEquals("BI1", building.getInsuranceNr());
		assertEquals("P1", building.getPlotNr());
		assertEquals("NB1", building.getNationalBuildingId());

		assertEquals("Teststrasse 10", building.getStreet());
		assertEquals("1111", building.getZip());
		assertEquals("Testingen", building.getCity());
		assertEquals(CodeCountryEnum.getCountry("ch"), building.getCountry());
		assertEquals(CodeCurrencyEnum.getCurrency("chf"), building.getCurrency());

		assertEquals(BigDecimal.valueOf(1000.0), building.getVolume());
		assertEquals(BigDecimal.valueOf(100.0), building.getAreaGross());
		assertEquals(BigDecimal.valueOf(90.0), building.getAreaNet());
		assertEquals(3, building.getNrOfFloorsAboveGround());
		assertEquals(1, building.getNrOfFloorsBelowGround());

		assertEquals(CodeBuildingTypeEnum.getBuildingType("T01"), building.getBuildingType());
		assertEquals(CodeBuildingSubTypeEnum.getBuildingSubType("ST05-26"), building.getBuildingSubType());
		assertEquals(1985, building.getBuildingYear());

		assertEquals(BigDecimal.valueOf(1000000.0), building.getInsuredValue());
		assertEquals(2000, building.getInsuredValueYear());
		assertEquals(BigDecimal.valueOf(0.0), building.getNotInsuredValue());
		assertEquals(2000, building.getNotInsuredValueYear());
		assertEquals(BigDecimal.valueOf(0.0), building.getThirdPartyValue());
		assertEquals(2000, building.getThirdPartyValueYear());

		assertEquals(CodeBuildingMaintenanceStrategyEnum.getMaintenanceStrategy("N"),
				building.getCurrentRating().getMaintenanceStrategy());
		assertEquals(CodeBuildingPartCatalogEnum.getPartCatalog("C6"), building.getCurrentRating().getPartCatalog());

		assertEquals(22, building.getCurrentRating().getElementCount(), "element count 22");
		assertEquals(22, building.getCurrentRating().getElementList().size(), "element count 22");
		// assertEquals(100, building.getCurrentRating().getElementContributions(),
		// "element contributions 100");
	}

}
