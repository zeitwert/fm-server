
package io.zeitwert.fm.building.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.util.List;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.ObjPartItemRepository;
import io.zeitwert.ddd.obj.model.ObjPartTransitionRepository;
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
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.dms.model.enums.CodeContentKindEnum;
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategoryEnum;
import io.zeitwert.fm.dms.model.enums.CodeDocumentKindEnum;
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase;

@Component("objBuildingRepository")
public class ObjBuildingRepositoryImpl extends FMObjRepositoryBase<ObjBuilding, ObjBuildingVRecord>
		implements ObjBuildingRepository {

	private static final String AGGREGATE_TYPE = "obj_building";

	private final ObjBuildingPartRatingRepository ratingRepository;
	private final CodePartListType ratingListType;
	private final ObjBuildingPartElementRatingRepository elementRepository;
	private final CodePartListType materialDescriptionSetType;
	private final CodePartListType conditionDescriptionSetType;
	private final CodePartListType measureDescriptionSetType;

	//@formatter:off
	protected ObjBuildingRepositoryImpl(
		final AppContext appContext,
		final DSLContext dslContext,
		final ObjPartTransitionRepository transitionRepository,
		final ObjPartItemRepository itemRepository,
		final ObjNoteRepository noteRepository,
		final ObjBuildingPartRatingRepository ratingRepository,
		final ObjBuildingPartElementRatingRepository elementRepository
	) {
		super(
			ObjBuildingRepository.class,
			ObjBuilding.class,
			ObjBuildingBase.class,
			AGGREGATE_TYPE,
			appContext,
			dslContext,
			transitionRepository,
			itemRepository,
			noteRepository
		);
		this.ratingRepository = ratingRepository;
		this.ratingListType = this.getAppContext().getPartListType(ObjBuildingFields.RATING_LIST);
		this.elementRepository = elementRepository;
		this.materialDescriptionSetType = this.getAppContext().getPartListType(ObjBuildingFields.MATERIAL_DESCRIPTION_SET);
		this.conditionDescriptionSetType = this.getAppContext().getPartListType(ObjBuildingFields.CONDITION_DESCRIPTION_SET);
		this.measureDescriptionSetType = this.getAppContext().getPartListType(ObjBuildingFields.MEASURE_DESCRIPTION_SET);
	}
	//@formatter:on

	@Override
	@PostConstruct
	public void registerPartRepositories() {
		super.registerPartRepositories();
		this.addPartRepository(this.getItemRepository());
		this.addPartRepository(this.getRatingRepository());
		this.addPartRepository(this.getElementRepository());
	}

	@Override
	public ObjBuildingPartRatingRepository getRatingRepository() {
		return this.ratingRepository;
	}

	@Override
	public CodePartListType getRatingListType() {
		return this.ratingListType;
	}

	@Override
	public ObjBuildingPartElementRatingRepository getElementRepository() {
		return this.elementRepository;
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
	protected boolean hasAccountId() {
		return true;
	}

	@Override
	public ObjBuilding doCreate() {
		return this.doCreate(this.getDSLContext().newRecord(Tables.OBJ_BUILDING));
	}

	@Override
	public void doAfterCreate(ObjBuilding building) {
		super.doAfterCreate(building);
		this.addCoverFoto(building);
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
	public void doBeforeStore(ObjBuilding building) {
		super.doBeforeStore(building);
		if (building.getCoverFotoId() == null) {
			this.addCoverFoto(building);
		}
	}

	@Override
	public List<ObjBuildingVRecord> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.OBJ_BUILDING_V, Tables.OBJ_BUILDING_V.ID, querySpec);
	}

	private void addCoverFoto(ObjBuilding building) {
		ObjDocumentRepository documentRepo = (ObjDocumentRepository) this.getAppContext().getRepository(ObjDocument.class);
		Integer tenantId = building.getTenantId();
		ObjDocument coverFoto = documentRepo.create(tenantId);
		coverFoto.setName("CoverFoto");
		coverFoto.setContentKind(CodeContentKindEnum.getContentKind("foto"));
		coverFoto.setDocumentKind(CodeDocumentKindEnum.getDocumentKind("standalone"));
		coverFoto.setDocumentCategory(CodeDocumentCategoryEnum.getDocumentCategory("foto"));
		documentRepo.store(coverFoto);
		building.setCoverFotoId(coverFoto.getId());
	}

}
