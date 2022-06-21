
package io.zeitwert.fm.portfolio.model.base;

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
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.ReferenceSetProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingVRecord;
import io.zeitwert.fm.obj.model.ObjVRepository;
import io.zeitwert.fm.obj.model.base.FMObjBase;
import io.zeitwert.fm.portfolio.model.ObjPortfolio;
import io.zeitwert.fm.portfolio.model.ObjPortfolioRepository;

public abstract class ObjPortfolioBase extends FMObjBase implements ObjPortfolio {

	private final UpdatableRecord<?> dbRecord;

	protected final SimpleProperty<String> name;
	protected final SimpleProperty<String> description;
	protected final SimpleProperty<String> portfolioNr;
	protected final ReferenceProperty<ObjAccount> account;
	protected final ReferenceSetProperty<ObjBuilding> includeSet;
	protected final ReferenceSetProperty<ObjBuilding> excludeSet;
	protected final ReferenceSetProperty<ObjBuilding> buildingSet;

	protected ObjPortfolioBase(SessionInfo sessionInfo, ObjPortfolioRepository repository, UpdatableRecord<?> objRecord,
			UpdatableRecord<?> portfolioRecord) {
		super(sessionInfo, repository, objRecord);
		this.dbRecord = portfolioRecord;
		this.name = this.addSimpleProperty(dbRecord, ObjPortfolioFields.NAME);
		this.description = this.addSimpleProperty(dbRecord, ObjPortfolioFields.DESCRIPTION);
		this.portfolioNr = this.addSimpleProperty(dbRecord, ObjPortfolioFields.PORTFOLIO_NR);
		this.account = this.addReferenceProperty(dbRecord, ObjPortfolioFields.ACCOUNT_ID, ObjAccount.class);
		this.includeSet = this.addReferenceSetProperty(this.getRepository().getIncludeSetType(), ObjBuilding.class);
		this.excludeSet = this.addReferenceSetProperty(this.getRepository().getExcludeSetType(), ObjBuilding.class);
		this.buildingSet = this.addReferenceSetProperty(this.getRepository().getBuildingSetType(), ObjBuilding.class);
	}

	@Override
	public ObjPortfolioRepository getRepository() {
		return (ObjPortfolioRepository) super.getRepository();
	}

	@Override
	public void doInit(Integer objId, Integer tenantId) {
		super.doInit(objId, tenantId);
		this.dbRecord.setValue(ObjPortfolioFields.OBJ_ID, objId);
	}

	@Override
	public void doAssignParts() {
		super.doAssignParts();
		ObjPartItemRepository itemRepo = this.getRepository().getItemRepository();
		this.includeSet.loadReferenceSet(itemRepo.getPartList(this, this.getRepository().getIncludeSetType()));
		this.excludeSet.loadReferenceSet(itemRepo.getPartList(this, this.getRepository().getExcludeSetType()));
		this.buildingSet.loadReferenceSet(itemRepo.getPartList(this, this.getRepository().getBuildingSetType()));
	}

	@Override
	public void doBeforeStore() {
		System.out.println("portf.doBeforeStore");
		super.doBeforeStore();
	}

	@Override
	public void doStore() {
		super.doStore();
		this.dbRecord.store();
	}

	@Override
	public <P extends Part<?>> P addPart(Property<P> property, CodePartListType partListType) {
		return super.addPart(property, partListType);
	}

	@Override
	protected void doCalcAll() {
		super.doCalcAll();
		this.calcCaption();
		this.calcBuildingSet();
	}

	private void calcCaption() {
		this.caption.setValue(this.getName());
	}

	private void calcBuildingSet() {
		this.buildingSet.clearItems();
		for (Integer objId : this.includeSet.getItems()) {
			this.getBuildings(objId).forEach(b -> this.buildingSet.addItem(b.getId()));
		}
		for (Integer objId : this.excludeSet.getItems()) {
			this.getBuildings(objId).forEach(b -> this.buildingSet.removeItem(b.getId()));
		}
	}

	private Set<Obj> getBuildings(Integer id) {
		SessionInfo sessionInfo = this.getMeta().getSessionInfo();
		ObjVRepository objRepo = this.getRepository().getObjVRepository();
		Obj obj = objRepo.get(sessionInfo, id);
		CodeAggregateType objType = obj.getMeta().getAggregateType();
		if (objType == CodeAggregateTypeEnum.getAggregateType("obj_building")) {
			return Set.of(obj);
		} else if (objType == CodeAggregateTypeEnum.getAggregateType("obj_portfolio")) {
			ObjPortfolio pf = this.getRepository().get(sessionInfo, id);
			return pf.getBuildingSet().stream().map(buildingId -> objRepo.get(sessionInfo, buildingId))
					.collect(Collectors.toSet());
		} else if (objType == CodeAggregateTypeEnum.getAggregateType("obj_account")) {
			List<ObjBuildingVRecord> buildings = this.getRepository().getBuildingRepository()
					.getByForeignKey(sessionInfo, "account_id", id);
			return buildings.stream().map(bldg -> objRepo.get(sessionInfo, bldg.getId()))
					.collect(Collectors.toSet());
		}
		throw new InvalidParameterException("unsupported objType " + objType.getId());
	}

}
