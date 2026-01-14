package io.domain.test.persist

import dddrive.ddd.model.AggregatePersistenceProvider
import io.domain.test.model.ObjTest

interface ObjTestPersistenceProvider : AggregatePersistenceProvider<ObjTest>
