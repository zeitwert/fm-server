
package io.zeitwert.fm.portfolio.model.base;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateType;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateTypeEnum;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.base.ObjExtnBase;
import io.zeitwert.ddd.property.model.ReferenceSetProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.fm.account.model.ItemWithAccount;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingVRecord;
import io.zeitwert.fm.collaboration.model.impl.AggregateWithNotesMixin;
import io.zeitwert.fm.obj.model.ObjVRepository;
import io.zeitwert.fm.oe.model.ObjTenantFM;
import io.zeitwert.fm.portfolio.model.ObjPortfolio;
import io.zeitwert.fm.portfolio.model.ObjPortfolioRepository;
import io.zeitwert.fm.task.model.impl.AggregateWithTasksMixin;

public abstract class ObjPortfolioBase extends ObjExtnBase
		implements ObjPortfolio, AggregateWithNotesMixin, AggregateWithTasksMixin {

	//@formatter:off
	protected final SimpleProperty<String> name = this.addSimpleProperty("name", String.class);
	protected final SimpleProperty<String> description = this.addSimpleProperty("description", String.class);
	protected final SimpleProperty<String> portfolioNr = this.addSimpleProperty("portfolioNr", String.class);
	protected final ReferenceSetProperty<Obj> includeSet = this.addReferenceSetProperty("includeSet", Obj.class);
	protected final ReferenceSetProperty<Obj> excludeSet = this.addReferenceSetProperty("excludeSet", Obj.class);
	protected final ReferenceSetProperty<ObjBuilding> buildingSet = this.addReferenceSetProperty("buildingSet", ObjBuilding.class);
	//@formatter:on

	protected ObjPortfolioBase(ObjPortfolioRepository repository, Object state) {
		super(repository, state);
	}

	@Override
	public ObjPortfolioRepository getRepository() {
		return (ObjPortfolioRepository) super.getRepository();
	}

	@Override
	public ObjPortfolio aggregate() {
		return this;
	}

	@Override
	public final ObjAccount getAccount() {
		return ItemWithAccount.getAccountCache().get(this.getAccountId());
	}

	@Override
	public void doCalcSearch() {
		this.addSearchToken(this.getPortfolioNr());
		this.addSearchText(this.getName());
		this.addSearchText(this.getDescription());
	}

	@Override
	public double getInflationRate() {
		BigDecimal inflationRate = this.getAccount().getInflationRate();
		inflationRate = inflationRate != null ? inflationRate : ((ObjTenantFM) this.getTenant()).getInflationRate();
		return inflationRate != null ? inflationRate.doubleValue() : 0;
	}

	@Override
	public double getPortfolioValue(int year) {
		// if (this.getInsuredValueYear() != null && this.getInsuredValue() != null) {
		// return ObjBuildingBase.DefaultPriceIndex.priceAt(this.getInsuredValueYear(),
		// 1000.0 * this.getInsuredValue().doubleValue(), year,
		// this.getInflationRate());
		// }
		return 0;
	}

	@Override
	public Integer getCondition(int year) {
		// ObjBuildingPartRating rating = this.getCurrentRating();
		// if (rating != null) {
		// return rating.getCondition(year);
		// }
		return null;
	}

	@Override
	protected void doCalcAll() {
		super.doCalcAll();
		this.calcCaption();
		this.calcBuildingSet();
	}

	private void calcCaption() {
		this.setCaption(this.getName());
	}

	private void calcBuildingSet() {
		this.buildingSet.clearItems();
		for (Integer objId : this.includeSet.getItems()) {
			this.getBuildingIds(objId).forEach(id -> this.buildingSet.addItem(id));
		}
		for (Integer objId : this.excludeSet.getItems()) {
			this.getBuildingIds(objId).forEach(id -> this.buildingSet.removeItem(id));
		}
	}

	private Set<Integer> getBuildingIds(Integer id) {
		ObjVRepository objRepo = ObjPortfolioRepository.getObjRepository();
		Obj obj = objRepo.get(id);
		CodeAggregateType objType = obj.getMeta().getAggregateType();
		if (objType == CodeAggregateTypeEnum.getAggregateType("obj_building")) {
			return Set.of(obj.getId());
		} else if (objType == CodeAggregateTypeEnum.getAggregateType("obj_portfolio")) {
			ObjPortfolio pf = this.getRepository().get(id);
			return pf.getBuildingSet();
		} else if (objType == CodeAggregateTypeEnum.getAggregateType("obj_account")) {
			List<ObjBuildingVRecord> buildings = ObjPortfolioRepository.getBuildingRepository().getByForeignKey("account_id",
					id);
			return buildings.stream().map(bldg -> bldg.getId()).collect(Collectors.toSet());
		}
		throw new InvalidParameterException("unsupported objType " + objType.getId());
	}

}
