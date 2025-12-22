package io.dddrive.domain.oe.model.base

import io.dddrive.oe.model.base.ObjTenantBase
import io.dddrive.domain.oe.model.ObjTenantRepository

abstract class ObjTenantTestBase(
	override val repository: ObjTenantRepository,
	isNew: Boolean,
) : ObjTenantBase(repository, isNew)
