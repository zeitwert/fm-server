package io.dddrive.domain.household.persist

import io.dddrive.ddd.model.AggregatePersistenceProvider
import io.dddrive.domain.household.model.ObjHousehold

interface ObjHouseholdPersistenceProvider : AggregatePersistenceProvider<ObjHousehold>
