package io.zeitwert.fm.portfolio.model;

import java.util.Set;

import io.dddrive.core.obj.model.Obj;
import io.zeitwert.fm.account.model.ItemWithAccount;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.collaboration.model.ItemWithNotes;
import io.zeitwert.fm.task.model.ItemWithTasks;

public interface ObjPortfolio extends Obj, ItemWithAccount, ItemWithNotes, ItemWithTasks {

	@Override
	Integer getAccountId();

	@Override
	void setAccountId(Integer id);

	@Override
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

	void addInclude(Integer id);

	void removeInclude(Integer id);

	Set<Integer> getExcludeSet();

	void clearExcludeSet();

	void addExclude(Integer id);

	void removeExclude(Integer id);

	double getInflationRate();

	double getDiscountRate();

	Integer getCondition(int year);

	double getPortfolioValue(int year);

}
