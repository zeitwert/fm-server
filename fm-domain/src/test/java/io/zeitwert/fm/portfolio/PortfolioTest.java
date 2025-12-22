package io.zeitwert.fm.portfolio;

import io.zeitwert.dddrive.app.model.RequestContext;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.portfolio.model.ObjPortfolio;
import io.zeitwert.fm.portfolio.model.ObjPortfolioRepository;
import io.zeitwert.test.TestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test")
public class PortfolioTest {

	@Autowired
	private RequestContext requestCtx;

	@Autowired
	private ObjAccountRepository accountRepository;

	@Autowired
	private ObjPortfolioRepository portfolioRepository;

	@Autowired
	private ObjBuildingRepository buildingRepository;

	@Test
	public void testPortfolio() throws Exception {

		assertNotNull(portfolioRepository, "portfolioRepository not null");
		assertEquals("obj_portfolio", portfolioRepository.getAggregateType().getId());

		assertNotNull(accountRepository, "accountRepository not null");
		assertEquals("obj_account", accountRepository.getAggregateType().getId());

		assertNotNull(buildingRepository, "buildingRepository not null");
		assertEquals("obj_building", buildingRepository.getAggregateType().getId());

		ObjAccount account = getTestAccount(requestCtx);
		ObjPortfolio pf1a = portfolioRepository.create(requestCtx.getTenantId(), requestCtx.getUserId(), requestCtx.getCurrentTime());
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

		// portfolioRepository.store(pf1a);
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
	}

	private ObjAccount getTestAccount(RequestContext requestCtx) {
		return accountRepository.get(accountRepository.getAll(requestCtx.getTenantId()).getFirst());
	}

}
