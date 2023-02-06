package io.zeitwert.fm.portfolio.model;

import java.util.Set;

import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.collaboration.model.ItemWithNotes;
import io.zeitwert.fm.task.model.ItemWithTasks;

public interface ObjPortfolio extends Obj, ItemWithNotes, ItemWithTasks {

	Integer getAccountId();

	void setAccountId(Integer id);

	ObjAccount getAccount();

	String getName();

	void setName(String name);

	String getDescription();

	void setDescription(String description);

	String getPortfolioNr();

	void setPortfolioNr(String portfolioNr);

	Set<Integer> getBuildingSet();

	Set<Integer> getIncludeSet();

	void clearIncludeSet();

	void addInclude(Integer include);

	void removeInclude(Integer include);

	Set<Integer> getExcludeSet();

	void clearExcludeSet();

	void addExclude(Integer exclude);

	void removeExclude(Integer exclude);

	double getInflationRate();

	Integer getCondition(int year);

	double getPortfolioValue(int year);

}
