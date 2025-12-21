package io.dddrive.domain.oe.model.base

import io.dddrive.core.oe.model.base.ObjUserBase
import io.dddrive.domain.oe.model.ObjUserRepository

abstract class ObjUserTestBase(
	override val repository: ObjUserRepository,
	isNew: Boolean,
) : ObjUserBase(repository, isNew)
