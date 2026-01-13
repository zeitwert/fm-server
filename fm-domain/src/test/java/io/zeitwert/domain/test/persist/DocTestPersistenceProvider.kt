package io.zeitwert.domain.test.persist

import dddrive.ddd.model.AggregatePersistenceProvider
import dddrive.query.QuerySpec
import io.zeitwert.domain.test.model.DocTest
import io.zeitwert.domain.test.model.ObjTest
import io.zeitwert.fm.collaboration.model.ObjNote

interface DocTestPersistenceProvider : AggregatePersistenceProvider<DocTest> {

}
