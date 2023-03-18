package io.zeitwert.fm.building.model.base;

import static io.dddrive.util.Invariant.requireThis;

import java.time.LocalDate;

import org.flywaydb.core.internal.util.Pair;

import io.dddrive.ddd.model.Part;
import io.dddrive.ddd.model.PartRepository;
import io.dddrive.obj.model.base.ObjPartBase;
import io.dddrive.oe.model.ObjUser;
import io.dddrive.property.model.EnumProperty;
import io.dddrive.property.model.PartListProperty;
import io.dddrive.property.model.Property;
import io.dddrive.property.model.ReferenceProperty;
import io.dddrive.property.model.SimpleProperty;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartElementRating;
import io.zeitwert.fm.building.model.ObjBuildingPartRating;
import io.zeitwert.fm.building.model.ObjBuildingPartRatingRepository;
import io.zeitwert.fm.building.model.enums.CodeBuildingMaintenanceStrategy;
import io.zeitwert.fm.building.model.enums.CodeBuildingPart;
import io.zeitwert.fm.building.model.enums.CodeBuildingPartCatalog;
import io.zeitwert.fm.building.model.enums.CodeBuildingRatingStatus;
import io.zeitwert.fm.building.model.enums.CodeBuildingRatingStatusEnum;

public abstract class ObjBuildingPartRatingBase extends ObjPartBase<ObjBuilding>
		implements ObjBuildingPartRating {

	//@formatter:off
	protected final EnumProperty<CodeBuildingPartCatalog> partCatalog= this.addEnumProperty("partCatalog", CodeBuildingPartCatalog.class);
	protected final EnumProperty<CodeBuildingMaintenanceStrategy> maintenanceStrategy=this.addEnumProperty("maintenanceStrategy", CodeBuildingMaintenanceStrategy.class);
	protected final EnumProperty<CodeBuildingRatingStatus> ratingStatus= this.addEnumProperty("ratingStatus", CodeBuildingRatingStatus.class);
	protected final SimpleProperty<LocalDate> ratingDate= this.addSimpleProperty("ratingDate", LocalDate.class);
	protected final ReferenceProperty<ObjUser> ratingUser= this.addReferenceProperty("ratingUser", ObjUser.class);
	protected final PartListProperty<ObjBuildingPartElementRating> elementList= this.addPartListProperty("elementList", ObjBuildingPartElementRating.class);
	//@formatter:on

	protected Integer elementWeights = null;

	public ObjBuildingPartRatingBase(PartRepository<ObjBuilding, ?> repository, ObjBuilding obj, Object state) {
		super(repository, obj, state);
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
