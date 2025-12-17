package io.dddrive.domain.oe.model.base

import io.dddrive.core.oe.model.base.ObjTenantBase
import io.dddrive.domain.oe.model.ObjTenantRepository

abstract class ObjTenantTestBase(
	repository: ObjTenantRepository,
	isNew: Boolean,
) : ObjTenantBase(repository, isNew)
