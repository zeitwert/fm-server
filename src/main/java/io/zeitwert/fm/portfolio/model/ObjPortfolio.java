package io.zeitwert.fm.portfolio.model;

import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.obj.model.FMObj;

import java.util.Set;

public interface ObjPortfolio extends FMObj {

	String getName();

	void setName(String name);

	String getPortfolioNr();

	void setPortfolioNr(String portfolioNr);

	String getDescription();

	void setDescription(String description);

	Integer getAccountId();

	void setAccountId(Integer id);

	ObjAccount getAccount();

	Set<Integer> getIncludeSet();

	void clearIncludeSet();

	void addInclude(Integer include);

	void removeInclude(Integer include);

	Set<Integer> getExcludeSet();

	void clearExcludeSet();

	void addExclude(Integer exclude);

	void removeExclude(Integer exclude);

	Set<Integer> getBuildingSet();

}
