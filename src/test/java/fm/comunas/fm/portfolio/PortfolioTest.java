
package fm.comunas.fm.portfolio;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import fm.comunas.fm.account.model.ObjAccountRepository;
import fm.comunas.fm.building.model.ObjBuildingRepository;
import fm.comunas.fm.portfolio.model.ObjPortfolio;
import fm.comunas.fm.portfolio.model.ObjPortfolioRepository;
import fm.comunas.ddd.session.model.SessionInfo;
import fm.comunas.server.Application;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class PortfolioTest {

	@Autowired
	private SessionInfo sessionInfo;

	@Autowired
	private ObjPortfolioRepository portfolioRepository;

	@Autowired
	private ObjAccountRepository accountRepository;

	@Autowired
	private ObjBuildingRepository buildingRepository;

	@Test
	public void testPortfolio() throws Exception {

		assertTrue(portfolioRepository != null, "portfolioRepository not null");
		assertEquals("obj_portfolio", portfolioRepository.getAggregateType().getId());

		assertTrue(accountRepository != null, "accountRepository not null");
		assertEquals("obj_account", accountRepository.getAggregateType().getId());

		assertTrue(buildingRepository != null, "buildingRepository not null");
		assertEquals("obj_building", buildingRepository.getAggregateType().getId());

		ObjPortfolio pf1a = portfolioRepository.create(sessionInfo);
		// Integer pf1Id = pf1a.getId();
		// Integer pf1aIdHash = System.identityHashCode(pf1a);

		pf1a.setName("Portfolio 1");
		pf1a.setDescription("A test portfolio");

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

		// this.portfolioRepository.store(pf1a);
		// pf1a = null;

		// ObjPortfolio pf1b = portfolioRepository.get(sessionInfo, pf1Id).get();
		// Integer pf1bIdHash = System.identityHashCode(pf1b);

		// assertNotEquals(pf1aIdHash, pf1bIdHash);
		// assertNotNull(pf1b.getMeta().getModifiedByUser(), "modifiedByUser not null");
		// assertNotNull(pf1b.getMeta().getModifiedAt(), "modifiedAt not null");

		// assertEquals(5, pf1b.getIncludeSet().size(), "include set count 5");
		// assertEquals(2, pf1b.getExcludeSet().size(), "exclude set count 5");
		// assertEquals(3, pf1b.getBuildingSet().size(), "building set count 5");

	}

}
