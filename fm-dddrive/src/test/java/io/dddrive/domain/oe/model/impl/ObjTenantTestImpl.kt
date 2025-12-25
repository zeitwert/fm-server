package io.dddrive.domain.oe.model.impl

import io.dddrive.domain.oe.model.ObjTenantRepository
import io.dddrive.oe.model.base.ObjTenantBase

open class ObjTenantTestImpl(
	override val repository: ObjTenantRepository,
	isNew: Boolean,
) : ObjTenantBase(repository, isNew)
