
package io.zeitwert.fm.portfolio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.portfolio.model.ObjPortfolio;
import io.zeitwert.fm.portfolio.model.ObjPortfolioRepository;
import io.zeitwert.server.Application;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class PortfolioTest {

	@Autowired
	private RequestContext requestCtx;

	@Autowired
	private ObjAccountRepository accountRepo;

	@Autowired
	private ObjAccountRepository accountCache;

	@Autowired
	private ObjPortfolioRepository portfolioRepository;

	@Autowired
	private ObjBuildingRepository buildingRepository;

	@Test
	public void testPortfolio() throws Exception {

		assertTrue(this.portfolioRepository != null, "portfolioRepository not null");
		assertEquals("obj_portfolio", this.portfolioRepository.getAggregateType().getId());

		assertTrue(this.accountCache != null, "accountRepository not null");
		assertEquals("obj_account", this.accountCache.getAggregateType().getId());

		assertTrue(this.buildingRepository != null, "buildingRepository not null");
		assertEquals("obj_building", this.buildingRepository.getAggregateType().getId());

		ObjAccount account = this.getTestAccount(this.requestCtx);
		ObjPortfolio pf1a = this.portfolioRepository.create(this.requestCtx.getTenantId());
		// Integer pf1Id = pf1a.getId();
		// Integer pf1aIdHash = System.identityHashCode(pf1a);

		pf1a.setAccountId(account.getId());
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

		// ObjPortfolio pf1b = portfolioRepository.get(requestCtx, pf1Id).get();
		// Integer pf1bIdHash = System.identityHashCode(pf1b);

		// assertNotEquals(pf1aIdHash, pf1bIdHash);
		// assertNotNull(pf1b.getMeta().getModifiedByUser(), "modifiedByUser not null");
		// assertNotNull(pf1b.getMeta().getModifiedAt(), "modifiedAt not null");

		// assertEquals(5, pf1b.getIncludeSet().size(), "include set count 5");
		// assertEquals(2, pf1b.getExcludeSet().size(), "exclude set count 5");
		// assertEquals(3, pf1b.getBuildingSet().size(), "building set count 5");

		assertEquals(account.getId(), pf1a.getAccountId(), "account id");
		assertEquals(account.getId(), pf1a.getAccount().getId(), "account id");
	}

	private ObjAccount getTestAccount(RequestContext requestCtx) {
		return this.accountCache.get(this.accountRepo.find(null).get(0).getId());
	}

}
