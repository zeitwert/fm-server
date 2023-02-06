
package io.zeitwert.fm.portfolio.model.base;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateType;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateTypeEnum;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjPartItemRepository;
import io.zeitwert.ddd.obj.model.ObjRepository;
import io.zeitwert.ddd.obj.model.base.ObjExtnBase;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.ReferenceSetProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingVRecord;
import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.db.tables.records.ObjNoteVRecord;
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType;
import io.zeitwert.fm.collaboration.model.impl.ItemWithNotesImpl;
import io.zeitwert.fm.obj.model.ObjVRepository;
import io.zeitwert.fm.portfolio.model.ObjPortfolio;
import io.zeitwert.fm.portfolio.model.ObjPortfolioRepository;
import io.zeitwert.fm.task.model.DocTask;
import io.zeitwert.fm.task.model.db.tables.records.DocTaskVRecord;
import io.zeitwert.fm.task.model.impl.ItemWithTasksImpl;

public abstract class ObjPortfolioBase extends ObjExtnBase implements ObjPortfolio {

	//@formatter:off
	protected final SimpleProperty<String> name = this.addSimpleProperty("name", String.class);
	protected final SimpleProperty<String> description = this.addSimpleProperty("description", String.class);
	protected final SimpleProperty<String> portfolioNr = this.addSimpleProperty("portfolioNr", String.class);
	protected final ReferenceSetProperty<Obj> includeSet = this.addReferenceSetProperty("includeSet", Obj.class);
	protected final ReferenceSetProperty<Obj> excludeSet = this.addReferenceSetProperty("excludeSet", Obj.class);
	protected final ReferenceSetProperty<ObjBuilding> buildingSet = this.addReferenceSetProperty("buildingSet", ObjBuilding.class);
	//@formatter:on

	private final ItemWithNotesImpl notes = new ItemWithNotesImpl(this);
	private final ItemWithTasksImpl tasks = new ItemWithTasksImpl(this);

	protected ObjPortfolioBase(ObjPortfolioRepository repository, Object state) {
		super(repository, state);
	}

	@Override
	public ObjPortfolioRepository getRepository() {
		return (ObjPortfolioRepository) super.getRepository();
	}

	@Override
	public void doAssignParts() {
		super.doAssignParts();
		ObjPartItemRepository itemRepo = ObjRepository.getItemRepository();
		this.includeSet.loadReferences(itemRepo.getParts(this, ObjPortfolioRepository.includeSetType()));
		this.excludeSet.loadReferences(itemRepo.getParts(this, ObjPortfolioRepository.excludeSetType()));
		this.buildingSet.loadReferences(itemRepo.getParts(this, ObjPortfolioRepository.buildingSetType()));
	}

	@Override
	public List<ObjNoteVRecord> getNotes() {
		return this.notes.getNotes();
	}

	@Override
	public ObjNote addNote(CodeNoteType noteType) {
		return this.notes.addNote(noteType);
	}

	@Override
	public void removeNote(Integer noteId) {
		this.notes.removeNote(noteId);
	}

	@Override
	public List<DocTaskVRecord> getTasks() {
		return this.tasks.getTasks();
	}

	@Override
	public DocTask addTask() {
		return this.tasks.addTask();
	}

	@Override
	public void doCalcSearch() {
		this.addSearchToken(this.getPortfolioNr());
		this.addSearchText(this.getName());
		this.addSearchText(this.getDescription());
	}

	@Override
	public Part<?> addPart(Property<?> property, CodePartListType partListType) {
		if (property.equals(this.includeSet)) {
			return ObjRepository.getItemRepository().create(this, partListType);
		} else if (property.equals(this.excludeSet)) {
			return ObjRepository.getItemRepository().create(this, partListType);
		} else if (property.equals(this.buildingSet)) {
			return ObjRepository.getItemRepository().create(this, partListType);
		}
		return super.addPart(property, partListType);
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
