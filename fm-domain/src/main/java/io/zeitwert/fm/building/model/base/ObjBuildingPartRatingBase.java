package io.zeitwert.fm.building.model.base;

import static io.dddrive.util.Invariant.requireThis;

import java.time.LocalDate;

import io.dddrive.core.ddd.model.Part;
import io.dddrive.core.ddd.model.PartRepository;
import io.dddrive.core.obj.model.base.ObjPartBase;
import io.dddrive.core.oe.model.ObjUser;
import io.dddrive.core.property.model.BaseProperty;
import io.dddrive.core.property.model.EnumProperty;
import io.dddrive.core.property.model.PartListProperty;
import io.dddrive.core.property.model.Property;
import io.dddrive.core.property.model.ReferenceProperty;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartElementRating;
import io.zeitwert.fm.building.model.ObjBuildingPartRating;
import io.zeitwert.fm.building.model.enums.CodeBuildingMaintenanceStrategy;
import io.zeitwert.fm.building.model.enums.CodeBuildingPart;
import io.zeitwert.fm.building.model.enums.CodeBuildingPartCatalog;
import io.zeitwert.fm.building.model.enums.CodeBuildingRatingStatus;
import kotlin.Pair;

public abstract class ObjBuildingPartRatingBase extends ObjPartBase<ObjBuilding>
		implements ObjBuildingPartRating {

	//@formatter:off
	protected final EnumProperty<CodeBuildingPartCatalog> partCatalog= this.addEnumProperty("partCatalog", CodeBuildingPartCatalog.class);
	protected final EnumProperty<CodeBuildingMaintenanceStrategy> maintenanceStrategy=this.addEnumProperty("maintenanceStrategy", CodeBuildingMaintenanceStrategy.class);
	protected final EnumProperty<CodeBuildingRatingStatus> ratingStatus= this.addEnumProperty("ratingStatus", CodeBuildingRatingStatus.class);
	protected final BaseProperty<LocalDate> ratingDate= this.addBaseProperty("ratingDate", LocalDate.class);
	protected final ReferenceProperty<ObjUser> ratingUser= this.addReferenceProperty("ratingUser", ObjUser.class);
	protected final PartListProperty<ObjBuildingPartElementRating> elementList= this.addPartListProperty("elementList", ObjBuildingPartElementRating.class);
	//@formatter:on

	protected Integer elementWeights = null;

	protected ObjBuildingPartRatingBase(ObjBuilding obj, PartRepository<ObjBuilding, ? extends Part<ObjBuilding>> repository, Property<?> property, Integer id) {
		super(obj, repository, property, id);
	}

	@Override
	public void doAfterCreate() {
		super.doAfterCreate();
		this.setRatingStatus(CodeBuildingRatingStatus.OPEN);
	}

	@Override
	public Part<?> doAddPart(Property<?> property, Integer partId) {
		if (property == this.elementList) {
			PartRepository<ObjBuilding, ?> partRepo = getDirectory().getPartRepository(ObjBuildingPartElementRating.class);
			return partRepo.create(getAggregate(), property, partId);
		}
		return super.doAddPart(property, partId);
	}

	@Override
	public void doAfterAdd(Property<?> property, Part<?> part) {
		if (property == this.elementList) {
			if (this.getRatingDate() != null) {
				Integer year = this.getRatingDate().getYear();
				this.getElement(this.getElementCount() - 1).setRatingYear(year);
			}
		}
	}

	@Override
	public void doAfterSet(Property<?> property) {
		if (property == this.partCatalog) {
			this.elementList.clearParts();
			if (this.getPartCatalog() != null) {
				CodeBuildingPartCatalog catalog = this.getPartCatalog();
				for (Pair<CodeBuildingPart, Integer> part : catalog.getParts()) {
					this.addElement(part.getFirst()).setWeight(part.getSecond());
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
		ObjBuildingPartElementRating e = this.elementList.addPart(null);
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
