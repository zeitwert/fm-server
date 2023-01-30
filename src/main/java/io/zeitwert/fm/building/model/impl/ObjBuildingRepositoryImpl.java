
package io.zeitwert.fm.building.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.util.List;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartElementRatingRepository;
import io.zeitwert.fm.building.model.ObjBuildingPartRatingRepository;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.building.model.base.ObjBuildingBase;
import io.zeitwert.fm.building.model.base.ObjBuildingFields;
import io.zeitwert.fm.building.model.db.Tables;
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingRecord;
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingVRecord;
import io.zeitwert.fm.contact.model.ObjContactRepository;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase;

@Component("objBuildingRepository")
public class ObjBuildingRepositoryImpl extends FMObjRepositoryBase<ObjBuilding, ObjBuildingVRecord>
		implements ObjBuildingRepository {

	private static final String AGGREGATE_TYPE = "obj_building";

	private ObjContactRepository contactRepository;
	private ObjDocumentRepository documentRepository;
	private ObjBuildingPartRatingRepository ratingRepository;
	private CodePartListType contactSetType;
	private CodePartListType ratingListType;
	private ObjBuildingPartElementRatingRepository elementRepository;
	private CodePartListType materialDescriptionSetType;
	private CodePartListType conditionDescriptionSetType;
	private CodePartListType measureDescriptionSetType;

	protected ObjBuildingRepositoryImpl(
			final AppContext appContext,
			final DSLContext dslContext) {
		super(
				ObjBuildingRepository.class,
				ObjBuilding.class,
				ObjBuildingBase.class,
				AGGREGATE_TYPE,
				appContext,
				dslContext);
	}

	@Override
	public ObjContactRepository getContactRepository() {
		if (this.contactRepository == null) {
			this.contactRepository = this.getAppContext().getBean(ObjContactRepository.class);
		}
		return this.contactRepository;
	}

	@Override
	public ObjDocumentRepository getDocumentRepository() {
		if (this.documentRepository == null) {
			this.documentRepository = this.getAppContext().getBean(ObjDocumentRepository.class);
		}
		return this.documentRepository;
	}

	@Override
	public ObjBuildingPartRatingRepository getRatingRepository() {
		if (this.ratingRepository == null) {
			this.ratingRepository = this.getAppContext().getBean(ObjBuildingPartRatingRepository.class);
		}
		return this.ratingRepository;
	}

	@Override
	public CodePartListType getContactSetType() {
		if (this.contactSetType == null) {
			this.contactSetType = this.getAppContext().getPartListType(ObjBuildingFields.CONTACT_SET);
		}
		return this.contactSetType;
	}

	@Override
	public CodePartListType getRatingListType() {
		if (this.ratingListType == null) {
			this.ratingListType = this.getAppContext().getPartListType(ObjBuildingFields.RATING_LIST);
		}
		return this.ratingListType;
	}

	@Override
	public ObjBuildingPartElementRatingRepository getElementRepository() {
		if (this.elementRepository == null) {
			this.elementRepository = this.getAppContext().getBean(ObjBuildingPartElementRatingRepository.class);
		}
		return this.elementRepository;
	}

	@Override
	public CodePartListType getMaterialDescriptionSetType() {
		if (this.materialDescriptionSetType == null) {
			this.materialDescriptionSetType = this.getAppContext()
					.getPartListType(ObjBuildingFields.MATERIAL_DESCRIPTION_SET);
		}
		return this.materialDescriptionSetType;
	}

	@Override
	public CodePartListType getConditionDescriptionSetType() {
		if (this.conditionDescriptionSetType == null) {
			this.conditionDescriptionSetType = this.getAppContext()
					.getPartListType(ObjBuildingFields.CONDITION_DESCRIPTION_SET);
		}
		return this.conditionDescriptionSetType;
	}

	@Override
	public CodePartListType getMeasureDescriptionSetType() {
		if (this.measureDescriptionSetType == null) {
			this.measureDescriptionSetType = this.getAppContext().getPartListType(ObjBuildingFields.MEASURE_DESCRIPTION_SET);
		}
		return this.measureDescriptionSetType;
	}

	@Override
	@PostConstruct
	public void registerPartRepositories() {
		super.registerPartRepositories();
		this.addPartRepository(this.getItemRepository());
		this.addPartRepository(this.getRatingRepository());
		this.addPartRepository(this.getElementRepository());
	}

	@Override
	protected boolean hasAccountId() {
		return true;
	}

	@Override
	public ObjBuilding doCreate() {
		return this.doCreate(this.getDSLContext().newRecord(Tables.OBJ_BUILDING));
	}

	@Override
	public ObjBuilding doLoad(Integer buildingId) {
		requireThis(buildingId != null, "objId not null");
		ObjBuildingRecord buildingRecord = this.getDSLContext().fetchOne(Tables.OBJ_BUILDING,
				Tables.OBJ_BUILDING.OBJ_ID.eq(buildingId));
		if (buildingRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + buildingId + "]");
		}
		return this.doLoad(buildingId, buildingRecord);
	}

	@Override
	public List<ObjBuildingVRecord> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.OBJ_BUILDING_V, Tables.OBJ_BUILDING_V.ID, querySpec);
	}

}
