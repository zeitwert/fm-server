
package io.zeitwert.fm.portfolio.model.base;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jooq.UpdatableRecord;

import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateType;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateTypeEnum;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjPartItemRepository;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.ReferenceSetProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingVRecord;
import io.zeitwert.fm.obj.model.ObjVRepository;
import io.zeitwert.fm.obj.model.base.FMObjBase;
import io.zeitwert.fm.portfolio.model.ObjPortfolio;
import io.zeitwert.fm.portfolio.model.ObjPortfolioRepository;

public abstract class ObjPortfolioBase extends FMObjBase implements ObjPortfolio {

	protected final SimpleProperty<String> name;
	protected final SimpleProperty<String> description;
	protected final SimpleProperty<String> portfolioNr;
	protected final ReferenceSetProperty<ObjBuilding> includeSet;
	protected final ReferenceSetProperty<ObjBuilding> excludeSet;
	protected final ReferenceSetProperty<ObjBuilding> buildingSet;

	protected ObjPortfolioBase(ObjPortfolioRepository repository, UpdatableRecord<?> objRecord,
			UpdatableRecord<?> portfolioRecord) {
		super(repository, objRecord, portfolioRecord);
		this.name = this.addSimpleProperty(this.extnDbRecord(), ObjPortfolioFields.NAME);
		this.description = this.addSimpleProperty(this.extnDbRecord(), ObjPortfolioFields.DESCRIPTION);
		this.portfolioNr = this.addSimpleProperty(this.extnDbRecord(), ObjPortfolioFields.PORTFOLIO_NR);
		this.includeSet = this.addReferenceSetProperty(this.getRepository().getIncludeSetType(), ObjBuilding.class);
		this.excludeSet = this.addReferenceSetProperty(this.getRepository().getExcludeSetType(), ObjBuilding.class);
		this.buildingSet = this.addReferenceSetProperty(this.getRepository().getBuildingSetType(), ObjBuilding.class);
	}

	@Override
	public ObjPortfolioRepository getRepository() {
		return (ObjPortfolioRepository) super.getRepository();
	}

	@Override
	public void doAssignParts() {
		super.doAssignParts();
		ObjPartItemRepository itemRepo = this.getRepository().getItemRepository();
		this.includeSet.loadReferences(itemRepo.getParts(this, this.getRepository().getIncludeSetType()));
		this.excludeSet.loadReferences(itemRepo.getParts(this, this.getRepository().getExcludeSetType()));
		this.buildingSet.loadReferences(itemRepo.getParts(this, this.getRepository().getBuildingSetType()));
	}

	@Override
	public void doCalcSearch() {
		this.addSearchToken(this.getPortfolioNr());
		this.addSearchText(this.getName());
		this.addSearchText(this.getDescription());
	}

	@Override
	public <P extends Part<?>> P addPart(Property<P> property, CodePartListType partListType) {
		return super.addPart(property, partListType);
	}

	@Override
	public void setAccountId(Integer id) {
		super.account.setId(id);
		this.extnDbRecord().setValue(ObjPortfolioFields.ACCOUNT_ID, id);
	}

	@Override
	public double getInflationRate() {
		BigDecimal inflationRate = this.getAccount().getInflationRate();
		inflationRate = inflationRate != null ? inflationRate : this.getTenant().getInflationRate();
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
		ObjVRepository objRepo = this.getRepository().getObjVRepository();
		Obj obj = objRepo.get(id);
		CodeAggregateType objType = obj.getMeta().getAggregateType();
		if (objType == CodeAggregateTypeEnum.getAggregateType("obj_building")) {
			return Set.of(obj.getId());
		} else if (objType == CodeAggregateTypeEnum.getAggregateType("obj_portfolio")) {
			ObjPortfolio pf = this.getRepository().get(id);
			return pf.getBuildingSet();
		} else if (objType == CodeAggregateTypeEnum.getAggregateType("obj_account")) {
			List<ObjBuildingVRecord> buildings = this.getRepository().getBuildingRepository().getByForeignKey("account_id",
					id);
			return buildings.stream().map(bldg -> bldg.getId()).collect(Collectors.toSet());
		}
		throw new InvalidParameterException("unsupported objType " + objType.getId());
	}

}
