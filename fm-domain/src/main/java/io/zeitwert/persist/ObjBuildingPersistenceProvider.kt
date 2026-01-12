package io.zeitwert.persist

import dddrive.ddd.model.AggregatePersistenceProvider
import dddrive.query.QuerySpec
import io.zeitwert.fm.building.model.ObjBuilding

interface ObjBuildingPersistenceProvider : AggregatePersistenceProvider<ObjBuilding> {

}
