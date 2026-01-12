package io.zeitwert.persist

import dddrive.ddd.model.AggregatePersistenceProvider
import dddrive.query.QuerySpec
import io.zeitwert.fm.collaboration.model.ObjNote

interface ObjNotePersistenceProvider : AggregatePersistenceProvider<ObjNote> {

}
