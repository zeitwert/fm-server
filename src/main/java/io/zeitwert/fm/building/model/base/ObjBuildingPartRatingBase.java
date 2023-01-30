package io.zeitwert.fm.building.model.base;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.time.LocalDate;
import java.util.List;

import org.flywaydb.core.internal.util.Pair;
import org.jooq.UpdatableRecord;

import io.zeitwert.ddd.obj.model.base.ObjPartBase;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.PartListProperty;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartElementRating;
import io.zeitwert.fm.building.model.ObjBuildingPartElementRatingRepository;
import io.zeitwert.fm.building.model.ObjBuildingPartRating;
import io.zeitwert.fm.building.model.ObjBuildingPartRatingRepository;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.building.model.enums.CodeBuildingMaintenanceStrategy;
import io.zeitwert.fm.building.model.enums.CodeBuildingMaintenanceStrategyEnum;
import io.zeitwert.fm.building.model.enums.CodeBuildingPart;
import io.zeitwert.fm.building.model.enums.CodeBuildingPartCatalog;
import io.zeitwert.fm.building.model.enums.CodeBuildingPartCatalogEnum;
import io.zeitwert.fm.building.model.enums.CodeBuildingRatingStatus;
import io.zeitwert.fm.building.model.enums.CodeBuildingRatingStatusEnum;

public abstract class ObjBuildingPartRatingBase extends ObjPartBase<ObjBuilding>
		implements ObjBuildingPartRating {

	protected final EnumProperty<CodeBuildingPartCatalog> partCatalog;
	protected final EnumProperty<CodeBuildingMaintenanceStrategy> maintenanceStrategy;

	protected final EnumProperty<CodeBuildingRatingStatus> ratingStatus;
	protected final SimpleProperty<LocalDate> ratingDate;
	protected final ReferenceProperty<ObjUser> ratingUser;

	protected final PartListProperty<ObjBuildingPartElementRating> elementList;

	protected Integer elementWeights = null;

	public ObjBuildingPartRatingBase(PartRepository<ObjBuilding, ?> repository, ObjBuilding obj,
			UpdatableRecord<?> dbRecord) {
		super(repository, obj, dbRecord);

		this.partCatalog = this.addEnumProperty(dbRecord, ObjBuildingPartRatingFields.PART_CATALOG_ID,
				CodeBuildingPartCatalogEnum.class);
		this.maintenanceStrategy = this.addEnumProperty(dbRecord, ObjBuildingPartRatingFields.MAINTENANCE_STRATEGY_ID,
				CodeBuildingMaintenanceStrategyEnum.class);

		this.ratingStatus = this.addEnumProperty(dbRecord, ObjBuildingPartRatingFields.RATING_STATUS_ID,
				CodeBuildingRatingStatusEnum.class);
		this.ratingDate = this.addSimpleProperty(dbRecord, ObjBuildingPartRatingFields.RATING_DATE);
		this.ratingUser = this.addReferenceProperty(dbRecord, ObjBuildingPartRatingFields.RATING_USER_ID, ObjUser.class);
		this.elementList = this.addPartListProperty(this.getRepository().getElementListType());
	}

	@Override
	public ObjBuildingPartRatingRepository getRepository() {
		return (ObjBuildingPartRatingRepository) super.getRepository();
	}

	@Override
	public void doAfterCreate() {
		super.doAfterCreate();
		this.setRatingStatus(CodeBuildingRatingStatusEnum.getRatingStatus("open"));
	}

	@Override
	public void doAssignParts() {
		super.doAssignParts();
		ObjBuildingRepository repo = (ObjBuildingRepository) this.getAggregate().getMeta().getRepository();
		ObjBuildingPartElementRatingRepository elementRepo = repo.getElementRepository();
		List<ObjBuildingPartElementRating> elementList = elementRepo.getParts(this,
				this.getRepository().getElementListType());
		this.elementList.loadParts(elementList);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <P extends Part<?>> P addPart(Property<P> property, CodePartListType partListType) {
		if (property == this.elementList) {
			ObjBuildingRepository repo = (ObjBuildingRepository) this.getAggregate().getMeta().getRepository();
			return (P) repo.getElementRepository().create(this, partListType);
		}
		return super.addPart(property, partListType);
	}

	@Override
	public void afterAdd(Property<?> property) {
		if (property == this.elementList) {
			if (this.getRatingDate() != null) {
				Integer year = this.getRatingDate().getYear();
				this.getElement(this.getElementCount() - 1).setRatingYear(year);
			}
		}
	}

	@Override
	public void afterSet(Property<?> property) {
		if (property == this.partCatalog) {
			this.elementList.clearParts();
			if (this.getPartCatalog() != null) {
				for (Pair<CodeBuildingPart, Integer> part : this.getPartCatalog().getParts()) {
					this.addElement(part.getLeft()).setWeight(part.getRight());
				}
			}
		} else if (property == this.ratingDate) {
			if (this.getRatingDate() != null) {
				Integer year = this.getRatingDate().getYear();
				for (ObjBuildingPartElementRating element : this.getElementList()) {
					element.setRatingYear(year);
				}
			}
		}
	}

	@Override
	public Integer getRatingYear() {
		return this.getRatingDate() != null ? this.getRatingDate().getYear() : null;
	}

	@Override
	public Integer getElementWeights() {
		return this.elementWeights;
	}

	@Override
	public ObjBuildingPartElementRating getElement(CodeBuildingPart buildingPart) {
		return this.elementList.getParts().stream().filter(p -> p.getBuildingPart() == buildingPart).findFirst()
				.orElse(null);
	}

	@Override
	public ObjBuildingPartElementRating addElement(CodeBuildingPart buildingPart) {
		requireThis(this.getElement(buildingPart) == null,
				"unique element for buildingPart [" + buildingPart.getId() + "]");
		ObjBuildingPartElementRating e = this.elementList.addPart();
		e.setBuildingPart(buildingPart);
		return e;
	}

	@Override
	public Integer getCondition() {
		if (this.getRatingYear() == null || this.elementWeights == 0) {
			return null;
		}
		Integer condition = 0;
		for (ObjBuildingPartElementRating element : this.getElementList()) {
			if (element.getWeight() != null && element.getWeight() > 0) {
				condition += element.getWeight() * element.getCondition();
			}
		}
		return condition / this.elementWeights;
	}

	@Override
	public Integer getCondition(Integer year) {
		if (year == null || year < this.getRatingYear() || this.elementWeights == 0) {
			return null;
		}
		Integer condition = 0;
		for (ObjBuildingPartElementRating element : this.getElementList()) {
			if (element.getWeight() != null && element.getWeight() > 0) {
				condition += element.getWeight() * element.getCondition(year);
			}
		}
		return condition / this.elementWeights;
	}

	@Override
	protected void doCalcAll() {
		super.doCalcAll();
		this.doCalcVolatile();
	}

	@Override
	protected void doCalcVolatile() {
		super.doCalcVolatile();
		this.calcElementWeights();
	}

	private void calcElementWeights() {
		this.elementWeights = 0;
		for (ObjBuildingPartElementRating element : this.getElementList()) {
			if (element.getWeight() != null && element.getWeight() > 0) {
				this.elementWeights += element.getWeight();
			}
		}
	}

}
