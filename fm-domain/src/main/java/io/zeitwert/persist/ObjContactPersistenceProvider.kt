package io.zeitwert.persist

import dddrive.ddd.model.AggregatePersistenceProvider
import dddrive.query.QuerySpec
import io.zeitwert.fm.contact.model.ObjContact

interface ObjContactPersistenceProvider : AggregatePersistenceProvider<ObjContact> {

}
