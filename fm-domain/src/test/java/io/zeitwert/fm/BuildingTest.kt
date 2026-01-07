package io.zeitwert.fm

import io.zeitwert.config.data.TestDataSetup
import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.account.model.enums.CodeCurrency
import io.zeitwert.fm.building.model.ObjBuilding
import io.zeitwert.fm.building.model.ObjBuildingRepository
import io.zeitwert.fm.building.model.enums.CodeBuildingMaintenanceStrategy
import io.zeitwert.fm.building.model.enums.CodeBuildingPart
import io.zeitwert.fm.building.model.enums.CodeBuildingPartCatalog
import io.zeitwert.fm.building.model.enums.CodeBuildingSubType
import io.zeitwert.fm.building.model.enums.CodeBuildingType
import io.zeitwert.fm.oe.model.ObjUserRepository
import io.zeitwert.fm.oe.model.enums.CodeCountry.Enumeration.getCountry
import io.zeitwert.test.TestApplication
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

@SpringBootTest(classes = [TestApplication::class])
@ActiveProfiles("test")
class BuildingTest {

	@Autowired
	lateinit var sessionContext: SessionContext

	@Autowired
	lateinit var userRepo: ObjUserRepository

	@Autowired
	lateinit var accountRepo: ObjAccountRepository

	@Autowired
	lateinit var buildingRepository: ObjBuildingRepository

	@Test
	@Throws(Exception::class)
	fun testBuilding() {
		assertNotNull(buildingRepository, "buildingRepository not null")
		assertEquals("obj_building", buildingRepository.aggregateType.id)

		val account = accountRepo.getByKey(TestDataSetup.TEST_ACCOUNT_KEY).get()
		var buildingA1: ObjBuilding? = buildingRepository.create()

		assertNotNull(buildingA1, "test not null")
		assertNotNull(buildingA1.id, "id not null")
		assertNotNull(buildingA1.tenantId, "tenant not null")

		val buildingA_id = buildingA1.id as Int
		val buildingA_idHash = System.identityHashCode(buildingA1)

		assertNotNull(buildingA1.meta.createdByUserId, "createdByUser not null")
		assertNotNull(buildingA1.meta.createdAt, "createdAt not null")

		buildingA1.accountId = account.id
		initBuilding(buildingA1)
		assertEquals(account.id, buildingA1.accountId, "account id")
		assertEquals(account.id, buildingA1.account!!.id, "account id")

		assertEquals(22, buildingA1.currentRating!!.elementList.size, "element count 22")
		assertEquals(22, buildingA1.currentRating!!.elementList.size, "element list size 22")
		assertEquals(100, buildingA1.currentRating!!.elementWeights, "element contributions 100")

		val bp1 = CodeBuildingPart.P2
		val e1 = buildingA1.currentRating!!.getElement(bp1)
		e1.condition = 100
		e1.ratingYear = 2000
		e1.weight = 50
		val e1id = e1.id

		assertEquals(22, buildingA1.currentRating!!.elementList.size, "element count 22")
		assertEquals(22, buildingA1.currentRating!!.elementList.size, "element list size 22")
		assertEquals(e1, buildingA1.currentRating!!.elementList.getById(e1id), "e1 by id")
		assertEquals(e1, buildingA1.currentRating!!.getElement(bp1), "e1 by buildingPart")

		// assertEquals(50, building1a.getCurrentRating().getElementContributions(), 50,
		// "element contributions 50");
		val bp2 = CodeBuildingPart.P3
		val e2 = buildingA1.currentRating!!.getElement(bp2)
		e2.condition = 100
		e2.ratingYear = 2000
		e2.weight = 50
		val e2id = e2.id

		checkBuilding(buildingA1)
		assertEquals(e1, buildingA1.currentRating!!.elementList.getById(e1id), "e1 by id")
		assertEquals(e2, buildingA1.currentRating!!.elementList.getById(e2id), "e2 by id")
		assertEquals(e1, buildingA1.currentRating!!.getElement(bp1), "e1 by buildingPart")
		assertEquals(e2, buildingA1.currentRating!!.getElement(bp2), "e2 by buildingPart")

		buildingRepository.store(buildingA1)
		buildingA1 = null

		var buildingA2: ObjBuilding? = buildingRepository.load(buildingA_id)
		val buildingA2_idHash = System.identityHashCode(buildingA2)
		assertNotEquals(buildingA_idHash, buildingA2_idHash)
		assertNotNull(buildingA2!!.meta.modifiedByUserId, "modifiedByUser not null")
		assertNotNull(buildingA2.meta.modifiedAt, "modifiedAt not null")
		assertEquals(account.id, buildingA2.accountId, "account id")
		assertEquals(account.id, buildingA2.account!!.id, "account id")

		checkBuilding(buildingA2)
		assertEquals(
			bp1,
			buildingA2.currentRating!!
				.elementList
				.getById(e1id)
				.buildingPart,
			"e1 by id",
		)
		assertEquals(
			bp2,
			buildingA2.currentRating!!
				.elementList
				.getById(e2id)
				.buildingPart,
			"e2 by id",
		)
		assertEquals(bp1, buildingA2.currentRating!!.getElement(bp1).buildingPart, "e1 by buildingPart")
		assertEquals(bp2, buildingA2.currentRating!!.getElement(bp2).buildingPart, "e2 by buildingPart")

		val bp3 = CodeBuildingPart.P4
		val e3 = buildingA2.currentRating!!.getElement(bp3)
		e3.condition = 100
		e3.ratingYear = 2000
		e3.weight = 50
		val e3id = e3.id

		buildingA2.currentRating!!.elementList.remove(e2id)

		assertEquals(21, buildingA2.currentRating!!.elementList.size, "element count 21")
		assertEquals(21, buildingA2.currentRating!!.elementList.size, "element list size 21")
		// assertEquals(building1b.getCurrentRating().getElementContributions(), 100,
		// "element contributions 100");
		assertEquals(
			bp1,
			buildingA2.currentRating!!
				.elementList
				.getById(e1id)
				.buildingPart,
			"e1 by id",
		)
		assertEquals(
			bp3,
			buildingA2.currentRating!!
				.elementList
				.getById(e3id)
				.buildingPart,
			"e3 by id",
		)
		assertEquals(bp1, buildingA2.currentRating!!.getElement(bp1).buildingPart, "e1 by buildingPart")
		assertEquals(bp3, buildingA2.currentRating!!.getElement(bp3).buildingPart, "e3 by buildingPart")

		buildingRepository.store(buildingA2)
		buildingA2 = null

		val buildingA3 = buildingRepository.get(buildingA_id)

		assertEquals(21, buildingA3.currentRating!!.elementList.size, "element count 21")
		assertEquals(21, buildingA3.currentRating!!.elementList.size, "element list size 21")
		// assertEquals(building1c.getCurrentRating().getElementContributions(), 100,
		// "element contributions 100");
		assertEquals(
			bp1,
			buildingA3.currentRating!!
				.elementList
				.getById(e1id)
				.buildingPart,
			"e1 by id",
		)
		assertEquals(
			bp3,
			buildingA3.currentRating!!
				.elementList
				.getById(e3id)
				.buildingPart,
			"e3 by id",
		)
		assertEquals(bp1, buildingA3.currentRating!!.getElement(bp1).buildingPart, "e1 by buildingPart")
		assertEquals(bp3, buildingA3.currentRating!!.getElement(bp3).buildingPart, "e3 by buildingPart")
	}

	private fun initBuilding(building: ObjBuilding) {
		building.buildingNr = "B1"
		building.insuranceNr = "BI1"
		building.plotNr = "P1"
		building.nationalBuildingId = "NB1"

		building.street = "Teststrasse 10"
		building.zip = "1111"
		building.city = "Testingen"
		building.country = getCountry("ch")
		building.currency = CodeCurrency.CHF

		building.volume = BigDecimal.valueOf(1000.0)
		building.areaGross = BigDecimal.valueOf(100.0)
		building.areaNet = BigDecimal.valueOf(90.0)
		building.nrOfFloorsAboveGround = 3
		building.nrOfFloorsBelowGround = 1

		building.buildingType = CodeBuildingType.T01
		building.buildingSubType = CodeBuildingSubType.ST05_26
		building.buildingYear = 1985

		building.insuredValue = BigDecimal.valueOf(1000000.0)
		building.insuredValueYear = 2000
		building.notInsuredValue = BigDecimal.valueOf(0.0)
		building.notInsuredValueYear = 2000
		building.thirdPartyValue = BigDecimal.valueOf(0.0)
		building.thirdPartyValueYear = 2000

		building.addRating(userRepo.get(sessionContext.userId), sessionContext.currentTime)
		building.currentRating!!.partCatalog = CodeBuildingPartCatalog.C6
		building.currentRating!!.maintenanceStrategy = CodeBuildingMaintenanceStrategy.N
	}

	private fun checkBuilding(building: ObjBuilding) {
		assertEquals("B1", building.buildingNr)
		assertEquals("BI1", building.insuranceNr)
		assertEquals("P1", building.plotNr)
		assertEquals("NB1", building.nationalBuildingId)

		assertEquals("Teststrasse 10", building.street)
		assertEquals("1111", building.zip)
		assertEquals("Testingen", building.city)
		assertEquals(getCountry("ch"), building.country)
		assertEquals(CodeCurrency.CHF, building.currency)

		assertEquals(BigDecimal.valueOf(1000.0), building.volume)
		assertEquals(BigDecimal.valueOf(100.0), building.areaGross)
		assertEquals(BigDecimal.valueOf(90.0), building.areaNet)
		assertEquals(3, building.nrOfFloorsAboveGround)
		assertEquals(1, building.nrOfFloorsBelowGround)

		assertEquals(CodeBuildingType.T01, building.buildingType)
		assertEquals(CodeBuildingSubType.ST05_26, building.buildingSubType)
		assertEquals(1985, building.buildingYear)

		assertEquals(BigDecimal.valueOf(1000000.0), building.insuredValue)
		assertEquals(2000, building.insuredValueYear)
		assertEquals(BigDecimal.valueOf(0.0), building.notInsuredValue)
		assertEquals(2000, building.notInsuredValueYear)
		assertEquals(BigDecimal.valueOf(0.0), building.thirdPartyValue)
		assertEquals(2000, building.thirdPartyValueYear)

		assertEquals(CodeBuildingMaintenanceStrategy.N, building.currentRating!!.maintenanceStrategy)
		assertEquals(CodeBuildingPartCatalog.C6, building.currentRating!!.partCatalog)

		assertEquals(22, building.currentRating!!.elementList.size, "element count 22")
		assertEquals(22, building.currentRating!!.elementList.size, "element count 22")
		// assertEquals(100, building.getCurrentRating().getElementContributions(),
		// "element contributions 100");
	}

}
