
package io.zeitwert.fm.building.model.impl;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.ObjPartItemRepository;
import io.zeitwert.ddd.obj.model.ObjPartTransitionRepository;
import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartElementRepository;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.building.model.base.ObjBuildingBase;
import io.zeitwert.fm.building.model.base.ObjBuildingFields;
import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.fm.building.model.db.Tables;
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingRecord;
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingVRecord;
import io.zeitwert.fm.obj.model.ObjPartNoteRepository;
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase;

@Component("objBuildingRepository")
public class ObjBuildingRepositoryImpl extends FMObjRepositoryBase<ObjBuilding, ObjBuildingVRecord>
		implements ObjBuildingRepository {

	private static final String ITEM_TYPE = "obj_building";

	private final ObjBuildingPartElementRepository elementRepository;
	private final CodePartListType elementListType;
	private final CodePartListType materialDescriptionSetType;
	private final CodePartListType conditionDescriptionSetType;
	private final CodePartListType measureDescriptionSetType;

	@Autowired
	//@formatter:off
	protected ObjBuildingRepositoryImpl(
		final AppContext appContext,
		final DSLContext dslContext,
		final ObjPartTransitionRepository transitionRepository,
		final ObjPartItemRepository itemRepository,
		final ObjPartNoteRepository noteRepository,
		final ObjBuildingPartElementRepository elementRepository
	) {
		super(
			ObjBuildingRepository.class,
			ObjBuilding.class,
			ObjBuildingBase.class,
			ITEM_TYPE,
			appContext,
			dslContext,
			transitionRepository,
			itemRepository,
			noteRepository
		);
		this.elementRepository = elementRepository;
		this.elementListType = this.getAppContext().getPartListType(ObjBuildingFields.ELEMENT_LIST);
		this.materialDescriptionSetType = this.getAppContext().getPartListType(ObjBuildingFields.MATERIAL_DESCRIPTION_SET);
		this.conditionDescriptionSetType = this.getAppContext().getPartListType(ObjBuildingFields.CONDITION_DESCRIPTION_SET);
		this.measureDescriptionSetType = this.getAppContext().getPartListType(ObjBuildingFields.MEASURE_DESCRIPTION_SET);
	}
	//@formatter:on

	@Override
	public ObjBuildingPartElementRepository getElementRepository() {
		return this.elementRepository;
	}

	@Override
	public CodePartListType getElementListType() {
		return this.elementListType;
	}

	@Override
	public CodePartListType getMaterialDescriptionSetType() {
		return this.materialDescriptionSetType;
	}

	@Override
	public CodePartListType getConditionDescriptionSetType() {
		return this.conditionDescriptionSetType;
	}

	@Override
	public CodePartListType getMeasureDescriptionSetType() {
		return this.measureDescriptionSetType;
	}

	@Override
	public ObjBuilding doCreate(SessionInfo sessionInfo) {
		return doCreate(sessionInfo, this.getDSLContext().newRecord(Tables.OBJ_BUILDING));
	}

	@Override
	public void doInitParts(ObjBuilding obj) {
		super.doInitParts(obj);
		this.getItemRepository().init(obj);
		this.getElementRepository().init(obj);
	}

	@Override
	public List<ObjBuildingVRecord> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.OBJ_BUILDING_V, Tables.OBJ_BUILDING_V.ID, querySpec);
	}

	@Override
	protected String getAccountIdField() {
		return "account_id";
	}

	@Override
	public ObjBuilding doLoad(SessionInfo sessionInfo, Integer objId) {
		require(objId != null, "objId not null");
		ObjBuildingRecord buildingRecord = this.getDSLContext().fetchOne(Tables.OBJ_BUILDING,
				Tables.OBJ_BUILDING.OBJ_ID.eq(objId));
		if (buildingRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return this.doLoad(sessionInfo, objId, buildingRecord);
	}

	@Override
	public void doLoadParts(ObjBuilding obj) {
		super.doLoadParts(obj);
		this.getItemRepository().load(obj);
		this.getElementRepository().load(obj);
		((ObjBuildingBase) obj).loadElementList(this.elementRepository.getPartList(obj, this.getElementListType()));
	}

	@Override
	public void doStoreParts(ObjBuilding obj) {
		super.doStoreParts(obj);
		this.getItemRepository().store(obj);
		this.getElementRepository().store(obj);
	}

}
