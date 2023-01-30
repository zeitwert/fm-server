package io.zeitwert.fm.building.model.impl;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.base.ObjPartRepositoryBase;
import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.ddd.property.model.enums.CodePartListTypeEnum;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartRating;
import io.zeitwert.fm.building.model.ObjBuildingPartRatingRepository;
import io.zeitwert.fm.building.model.base.ObjBuildingPartRatingBase;
import io.zeitwert.fm.building.model.base.ObjBuildingPartRatingFields;
import io.zeitwert.fm.building.model.db.Tables;
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingPartRatingRecord;

@Component("buildingPartRatingRepository")
public class ObjBuildingPartRatingRepositoryImpl
		extends ObjPartRepositoryBase<ObjBuilding, ObjBuildingPartRating>
		implements ObjBuildingPartRatingRepository {

	private static final String PART_TYPE = "obj_building_part_element_rating";

	private final CodePartListType elementListType;

	//@formatter:off
	protected ObjBuildingPartRatingRepositoryImpl(
		final AppContext appContext,
		final DSLContext dslContext
	) {
		super(
			ObjBuilding.class,
			ObjBuildingPartRating.class,
			ObjBuildingPartRatingBase.class,
			PART_TYPE,
			appContext,
			dslContext
		);
		this.elementListType = CodePartListTypeEnum.getPartListType(ObjBuildingPartRatingFields.ELEMENT_RATING_LIST);
	}
	//@formatter:on

	@Override
	public CodePartListType getElementListType() {
		return this.elementListType;
	}

	@Override
	public ObjBuildingPartRating doCreate(ObjBuilding obj) {
		ObjBuildingPartRatingRecord dbRecord = this.getDSLContext()
				.newRecord(Tables.OBJ_BUILDING_PART_RATING);
		return this.newPart(obj, dbRecord);
	}

	@Override
	public List<ObjBuildingPartRating> doLoad(ObjBuilding obj) {
		//@formatter:off
		io.zeitwert.fm.building.model.db.tables.ObjBuildingPartRating table = Tables.OBJ_BUILDING_PART_RATING;
		Result<ObjBuildingPartRatingRecord> dbRecords = this.getDSLContext()
			.selectFrom(table)
			.where(table.OBJ_ID.eq(obj.getId()))
			.orderBy(table.SEQ_NR)
			.fetchInto(table);
		//@formatter:on
		return dbRecords.map(dbRecord -> this.newPart(obj, dbRecord));
	}

}
