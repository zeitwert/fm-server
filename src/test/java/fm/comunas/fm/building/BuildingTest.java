
package fm.comunas.fm.building;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import fm.comunas.fm.account.model.ObjAccount;
import fm.comunas.fm.account.model.ObjAccountRepository;
import fm.comunas.fm.account.model.enums.CodeAccountTypeEnum;
import fm.comunas.fm.building.model.ObjBuilding;
import fm.comunas.fm.building.model.ObjBuildingPartElement;
import fm.comunas.fm.building.model.ObjBuildingRepository;
import fm.comunas.fm.building.model.enums.CodeBuildingMaintenanceStrategyEnum;
import fm.comunas.fm.building.model.enums.CodeBuildingPart;
import fm.comunas.fm.building.model.enums.CodeBuildingPartCatalogEnum;
import fm.comunas.fm.building.model.enums.CodeBuildingPartEnum;
import fm.comunas.fm.building.model.enums.CodeBuildingSubTypeEnum;
import fm.comunas.fm.building.model.enums.CodeBuildingTypeEnum;
import fm.comunas.ddd.common.model.enums.CodeCountryEnum;
import fm.comunas.ddd.common.model.enums.CodeCurrencyEnum;
import fm.comunas.ddd.session.model.SessionInfo;
import fm.comunas.server.Application;

import java.math.BigDecimal;
import java.util.Optional;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class BuildingTest {

	private static final String HH_KEY = "##test##building";

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

		ObjAccount hh = this.getOrCreateTestHoushold(sessionInfo);
		ObjBuilding building1a = buildingRepository.create(sessionInfo);

		assertNotNull(building1a, "test not null");
		assertNotNull(building1a.getId(), "id not null");
		assertNotNull(building1a.getTenant(), "tenant not null");

		Integer building1Id = building1a.getId();
		Integer building1aIdHash = System.identityHashCode(building1a);

		assertNotNull(building1a.getMeta().getCreatedByUser(), "createdByUser not null");
		assertNotNull(building1a.getMeta().getCreatedAt(), "createdAt not null");

		building1a.setAccountId(hh.getId());
		this.initBuilding(building1a);

		assertEquals(building1a.getElementCount(), 0, "element count 0");
		assertEquals(building1a.getElementList().size(), 0, "element count 0");
		assertEquals(building1a.getElementContributions(), 0, "element contributions 0");

		CodeBuildingPart bp1 = CodeBuildingPartEnum.getBuildingPart("P1");
		ObjBuildingPartElement e1 = building1a.addElement(bp1);
		e1.setCondition(100);
		e1.setConditionYear(2000);
		e1.setValuePart(50);
		Integer e1id = e1.getId();

		assertEquals(building1a.getElementCount(), 1, "element count 1");
		assertEquals(building1a.getElementList().size(), 1, "element count 1");
		assertEquals(building1a.getElement(0), e1, "e1 by seqNr");
		assertEquals(building1a.getElementById(e1id), e1, "e1 by id");
		assertEquals(building1a.getElement(bp1), e1, "e1 by buildingPart");
		assertEquals(building1a.getElementContributions(), 50, "element contributions 50");

		CodeBuildingPart bp2 = CodeBuildingPartEnum.getBuildingPart("P2");
		ObjBuildingPartElement e2 = building1a.addElement(bp2);
		e2.setCondition(100);
		e2.setConditionYear(2000);
		e2.setValuePart(50);
		Integer e2id = e2.getId();

		this.checkBuilding(building1a);
		assertEquals(building1a.getElement(0), e1, "e1 by seqNr");
		assertEquals(building1a.getElement(1), e2, "e2 by seqNr");
		assertEquals(building1a.getElementById(e1id), e1, "e1 by id");
		assertEquals(building1a.getElementById(e2id), e2, "e2 by id");
		assertEquals(building1a.getElement(bp1), e1, "e1 by buildingPart");
		assertEquals(building1a.getElement(bp2), e2, "e2 by buildingPart");

		this.buildingRepository.store(building1a);
		building1a = null;

		ObjBuilding building1b = buildingRepository.get(sessionInfo, building1Id).get();
		Integer building1bIdHash = System.identityHashCode(building1b);
		assertNotEquals(building1aIdHash, building1bIdHash);
		assertNotNull(building1b.getMeta().getModifiedByUser(), "modifiedByUser not null");
		assertNotNull(building1b.getMeta().getModifiedAt(), "modifiedAt not null");

		this.checkBuilding(building1b);
		assertEquals(building1b.getElement(0).getBuildingPart(), bp1, "e1 by seqNr");
		assertEquals(building1b.getElement(1).getBuildingPart(), bp2, "e2 by seqNr");
		assertEquals(building1b.getElementById(e1id).getBuildingPart(), bp1, "e1 by id");
		assertEquals(building1b.getElementById(e2id).getBuildingPart(), bp2, "e2 by id");
		assertEquals(building1b.getElement(bp1).getBuildingPart(), bp1, "e1 by buildingPart");
		assertEquals(building1b.getElement(bp2).getBuildingPart(), bp2, "e2 by buildingPart");

		CodeBuildingPart bp3 = CodeBuildingPartEnum.getBuildingPart("P4");
		ObjBuildingPartElement e3 = building1b.addElement(bp3);
		e3.setCondition(100);
		e3.setConditionYear(2000);
		e3.setValuePart(50);
		Integer e3id = e3.getId();

		building1b.removeElement(e2id);

		assertEquals(building1b.getElementCount(), 2, "element count 2");
		assertEquals(building1b.getElementList().size(), 2, "element count 2");
		assertEquals(building1b.getElementContributions(), 100, "element contributions 100");
		assertEquals(building1b.getElement(0).getBuildingPart(), bp1, "e1 by seqNr");
		assertEquals(building1b.getElement(1).getBuildingPart(), bp3, "e3 by seqNr");
		assertEquals(building1b.getElementById(e1id).getBuildingPart(), bp1, "e1 by id");
		assertEquals(building1b.getElementById(e3id).getBuildingPart(), bp3, "e3 by id");
		assertEquals(building1b.getElement(bp1).getBuildingPart(), bp1, "e1 by buildingPart");
		assertEquals(building1b.getElement(bp3).getBuildingPart(), bp3, "e3 by buildingPart");

		this.buildingRepository.store(building1b);
		building1b = null;

		ObjBuilding building1c = buildingRepository.get(sessionInfo, building1Id).get();

		assertEquals(building1c.getElementCount(), 2, "element count 2");
		assertEquals(building1c.getElementList().size(), 2, "element count 2");
		assertEquals(building1c.getElementContributions(), 100, "element contributions 100");
		assertEquals(building1c.getElement(0).getBuildingPart(), bp1, "e1 by seqNr");
		assertEquals(building1c.getElement(1).getBuildingPart(), bp3, "e3 by seqNr");
		assertEquals(building1c.getElementById(e1id).getBuildingPart(), bp1, "e1 by id");
		assertEquals(building1c.getElementById(e3id).getBuildingPart(), bp3, "e3 by id");
		assertEquals(building1c.getElement(bp1).getBuildingPart(), bp1, "e1 by buildingPart");
		assertEquals(building1c.getElement(bp3).getBuildingPart(), bp3, "e3 by buildingPart");

	}

	private ObjAccount getOrCreateTestHoushold(SessionInfo sessionInfo) {
		Optional<ObjAccount> maybeHH = this.accountRepository.getByKey(sessionInfo, HH_KEY);
		if (maybeHH.isPresent()) {
			return maybeHH.get();
		}
		ObjAccount hh = this.accountRepository.create(sessionInfo);
		hh.setName("Building Test Account");
		// hh.setIntlKey(HH_KEY;
		hh.setAccountType(CodeAccountTypeEnum.getAccountType("client"));
		hh.setReferenceCurrency(CodeCurrencyEnum.getCurrency("chf"));
		this.accountRepository.store(hh);
		return this.accountRepository.get(sessionInfo, hh.getId()).get();
	}

	private void initBuilding(ObjBuilding building) {
		building.setBuildingNr("B1");
		building.setBuildingInsuranceNr("BI1");
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

		building.setBuildingMaintenanceStrategy(CodeBuildingMaintenanceStrategyEnum.getBuildingMaintenanceStrategy("N"));
		building.setBuildingPartCatalog(CodeBuildingPartCatalogEnum.getBuildingPartCatalog("C6"));
	}

	private void checkBuilding(ObjBuilding building) {
		assertEquals(building.getBuildingNr(), "B1");
		assertEquals(building.getBuildingInsuranceNr(), "BI1");
		assertEquals(building.getPlotNr(), "P1");
		assertEquals(building.getNationalBuildingId(), "NB1");

		assertEquals(building.getStreet(), "Teststrasse 10");
		assertEquals(building.getZip(), "1111");
		assertEquals(building.getCity(), "Testingen");
		assertEquals(building.getCountry(), CodeCountryEnum.getCountry("ch"));
		assertEquals(building.getCurrency(), CodeCurrencyEnum.getCurrency("chf"));

		assertEquals(building.getVolume(), BigDecimal.valueOf(1000.0));
		assertEquals(building.getAreaGross(), BigDecimal.valueOf(100.0));
		assertEquals(building.getAreaNet(), BigDecimal.valueOf(90.0));
		assertEquals(building.getNrOfFloorsAboveGround(), 3);
		assertEquals(building.getNrOfFloorsBelowGround(), 1);

		assertEquals(building.getBuildingType(), CodeBuildingTypeEnum.getBuildingType("T01"));
		assertEquals(building.getBuildingSubType(), CodeBuildingSubTypeEnum.getBuildingSubType("ST05-26"));
		assertEquals(building.getBuildingYear(), 1985);

		assertEquals(building.getInsuredValue(), BigDecimal.valueOf(1000000.0));
		assertEquals(building.getInsuredValueYear(), 2000);
		assertEquals(building.getNotInsuredValue(), BigDecimal.valueOf(0.0));
		assertEquals(building.getNotInsuredValueYear(), 2000);
		assertEquals(building.getThirdPartyValue(), BigDecimal.valueOf(0.0));
		assertEquals(building.getThirdPartyValueYear(), 2000);

		assertEquals(building.getBuildingMaintenanceStrategy(),
				CodeBuildingMaintenanceStrategyEnum.getBuildingMaintenanceStrategy("N"));
		assertEquals(building.getBuildingPartCatalog(), CodeBuildingPartCatalogEnum.getBuildingPartCatalog("C6"));

		assertEquals(building.getElementCount(), 2, "element count 2");
		assertEquals(building.getElementList().size(), 2, "element count 2");
		assertEquals(building.getElementContributions(), 100, "element contributions 100");
	}

}
