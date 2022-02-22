
package fm.comunas.fm.portfolio.model.base;

import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.jooq.UpdatableRecord;

import fm.comunas.fm.account.model.ObjAccount;
import fm.comunas.fm.building.model.ObjBuilding;
import fm.comunas.fm.building.model.db.tables.records.ObjBuildingVRecord;
import fm.comunas.fm.obj.model.ObjVRepository;
import fm.comunas.fm.obj.model.base.FMObjBase;
import fm.comunas.fm.portfolio.model.ObjPortfolio;
import fm.comunas.fm.portfolio.model.ObjPortfolioRepository;
import fm.comunas.ddd.aggregate.model.enums.CodeAggregateType;
import fm.comunas.ddd.aggregate.model.enums.CodeAggregateTypeEnum;
import fm.comunas.ddd.obj.model.Obj;
import fm.comunas.ddd.obj.model.ObjPartItem;
import fm.comunas.ddd.part.model.Part;
import fm.comunas.ddd.property.model.Property;
import fm.comunas.ddd.property.model.ReferenceProperty;
import fm.comunas.ddd.property.model.ReferenceSetProperty;
import fm.comunas.ddd.property.model.SimpleProperty;
import fm.comunas.ddd.property.model.enums.CodePartListType;
import fm.comunas.ddd.session.model.SessionInfo;

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

	public abstract void loadIncludeSet(Collection<ObjPartItem> includedSet);

	public abstract void loadExcludeSet(Collection<ObjPartItem> excludedSet);

	public abstract void loadBuildingSet(Collection<ObjPartItem> buildingSet);

	@Override
	public void doInit(Integer objId, Integer tenantId, Integer userId) {
		super.doInit(objId, tenantId, userId);
		this.dbRecord.setValue(ObjPortfolioFields.OBJ_ID, objId);
	}

	@Override
	public <P extends Part<?>> P addPart(Property<P> property, CodePartListType partListType) {
		return super.addPart(property, partListType);
	}

	@Override
	public void doStore(Integer userId) {
		super.doStore(userId);
		this.dbRecord.store();
	}

	@Override
	protected void doCalcAll() {
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
		Optional<Obj> maybeObj = objRepo.get(sessionInfo, id);
		require(maybeObj.isPresent(), "valid obj [" + id + "]");
		Obj obj = maybeObj.get();
		CodeAggregateType objType = obj.getMeta().getAggregateType();
		if (objType == CodeAggregateTypeEnum.getAggregateType("obj_building")) {
			return Set.of(obj);
		} else if (objType == CodeAggregateTypeEnum.getAggregateType("obj_portfolio")) {
			ObjPortfolio pf = this.getRepository().get(sessionInfo, id).get();
			return pf.getBuildingSet().stream().map(buildingId -> objRepo.get(sessionInfo, buildingId).get())
					.collect(Collectors.toSet());
		} else if (objType == CodeAggregateTypeEnum.getAggregateType("obj_account")) {
			List<ObjBuildingVRecord> buildings = this.getRepository().getBuildingRepository()
					.getByForeignKey(sessionInfo, "account_id", id);
			return buildings.stream().map(bldg -> objRepo.get(sessionInfo, bldg.getId()).get())
					.collect(Collectors.toSet());
		}
		throw new InvalidParameterException("unsupported objType " + objType.getId());
	}

	@Override
	public void beforeStore() {
		super.beforeStore();
		this.beginCalc();
		try {
			this.includeSet.beforeStore();
			this.excludeSet.beforeStore();
			this.buildingSet.beforeStore();
		} finally {
			this.endCalc();
		}
	}

}
