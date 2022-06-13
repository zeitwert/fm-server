
package io.zeitwert.fm.building.model.impl;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static io.zeitwert.ddd.util.Check.requireThis;

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
import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.collaboration.model.enums.CodeNoteTypeEnum;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.dms.model.enums.CodeContentKindEnum;
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategoryEnum;
import io.zeitwert.fm.dms.model.enums.CodeDocumentKindEnum;
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase;

import javax.annotation.PostConstruct;

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
		final ObjBuildingPartElementRepository elementRepository,
		final ObjNoteRepository noteRepository
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
	@PostConstruct
	public void registerPartRepositories() {
		super.registerPartRepositories();
		this.addPartRepository(this.getItemRepository());
		this.addPartRepository(this.getElementRepository());
	}

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
	protected String getAccountIdField() {
		return ObjBuildingFields.ACCOUNT_ID.getName();
	}

	@Override
	public ObjBuilding doCreate(SessionInfo sessionInfo) {
		return this.doCreate(sessionInfo, this.getDSLContext().newRecord(Tables.OBJ_BUILDING));
	}

	@Override
	public void doAfterCreate(ObjBuilding building) {
		super.doAfterCreate(building);
		this.addCoverFoto(building);
	}

	@Override
	public ObjBuilding doLoad(SessionInfo sessionInfo, Integer buildingId) {
		requireThis(buildingId != null, "objId not null");
		ObjBuildingRecord buildingRecord = this.getDSLContext().fetchOne(Tables.OBJ_BUILDING,
				Tables.OBJ_BUILDING.OBJ_ID.eq(buildingId));
		if (buildingRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + buildingId + "]");
		}
		return this.doLoad(sessionInfo, buildingId, buildingRecord);
	}

	@Override
	public void doAfterLoad(ObjBuilding building) {
		super.doAfterLoad(building);
		if (building.getNoteList().size() == 0) {
			for (int i = 0; i < 8.3 * Math.random(); i++) {
				ObjNote note = building.addNote(CodeNoteTypeEnum.getNoteType("note"));
				note.setSubject("Subject of Note " + (i + 1));
				note.setContent("Content of Note " + (i + 1));
				note.setIsPrivate(Math.random() > 0.8);
			}
		}
	}

	@Override
	public void doBeforeStore(ObjBuilding building) {
		super.doBeforeStore(building);
		if (building.getCoverFotoId() == null) {
			this.addCoverFoto(building);
		}
	}

	@Override
	public List<ObjBuildingVRecord> doFind(SessionInfo sessionInfo, QuerySpec querySpec) {
		return this.doFind(sessionInfo, Tables.OBJ_BUILDING_V, Tables.OBJ_BUILDING_V.ID, querySpec);
	}

	private void addCoverFoto(ObjBuilding building) {
		ObjDocumentRepository documentRepo = (ObjDocumentRepository) this.getAppContext().getRepository(ObjDocument.class);
		ObjDocument coverFoto = documentRepo.create(building.getMeta().getSessionInfo());
		coverFoto.setName("CoverFoto");
		coverFoto.setContentKind(CodeContentKindEnum.getContentKind("foto"));
		coverFoto.setDocumentKind(CodeDocumentKindEnum.getDocumentKind("standalone"));
		coverFoto.setDocumentCategory(CodeDocumentCategoryEnum.getDocumentCategory("foto"));
		documentRepo.store(coverFoto);
		building.setCoverFotoId(coverFoto.getId());
	}

}
