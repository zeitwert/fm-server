package io.zeitwert.fm

import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.data.config.TestDataSetup
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.building.model.ObjBuildingRepository
import io.zeitwert.fm.portfolio.model.ObjPortfolioRepository
import io.zeitwert.test.TestApplication
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest(classes = [TestApplication::class])
@ActiveProfiles("test")
class PortfolioTest {

	@Autowired
	lateinit var sessionContext: SessionContext

	@Autowired
	lateinit var accountRepository: ObjAccountRepository

	@Autowired
	lateinit var portfolioRepository: ObjPortfolioRepository

	@Autowired
	lateinit var buildingRepository: ObjBuildingRepository

	@Test
	@Throws(Exception::class)
	fun testPortfolio() {
		assertNotNull(portfolioRepository, "portfolioRepository not null")
		assertEquals("obj_portfolio", portfolioRepository.aggregateType.id)

		assertNotNull(accountRepository, "accountRepository not null")
		assertEquals("obj_account", accountRepository.aggregateType.id)

		assertNotNull(buildingRepository, "buildingRepository not null")
		assertEquals("obj_building", buildingRepository.aggregateType.id)

		val account = accountRepository.getByKey(TestDataSetup.TEST_ACCOUNT_KEY).get()
		val pf1a = portfolioRepository.create()

		// Integer pf1Id = pf1a.getId();
		// Integer pf1aIdHash = System.identityHashCode(pf1a);
		pf1a.accountId = account.id
		pf1a.name = "Portfolio 1"
		pf1a.description = "A test portfolio"

		// TODO

		// pf1a.addInclude(1);
		// pf1a.addInclude(2);
		// pf1a.addInclude(3);
		// pf1a.addInclude(4);
		// pf1a.addInclude(5);

		// pf1a.addExclude(2);
		// pf1a.addExclude(4);

		// assertEquals(5, pf1a.getIncludeSet().size(), "include set count 5");
		// assertEquals(2, pf1a.getExcludeSet().size(), "exclude set count 5");
		// assertEquals(3, pf1a.getBuildingSet().size(), "building set count 5");

		// portfolioRepository.store(pf1a);
		// pf1a = null;

		// ObjPortfolio pf1b = portfolioRepository.get(sessionContext, pf1Id).get();
		// Integer pf1bIdHash = System.identityHashCode(pf1b);

		// assertNotEquals(pf1aIdHash, pf1bIdHash);
		// assertNotNull(pf1b.getMeta().getModifiedByUser(), "modifiedByUser not null");
		// assertNotNull(pf1b.getMeta().getModifiedAt(), "modifiedAt not null");

		// assertEquals(5, pf1b.getIncludeSet().size(), "include set count 5");
		// assertEquals(2, pf1b.getExcludeSet().size(), "exclude set count 5");
		// assertEquals(3, pf1b.getBuildingSet().size(), "building set count 5");
		assertEquals(account.id, pf1a.accountId, "account id")
	}

}
