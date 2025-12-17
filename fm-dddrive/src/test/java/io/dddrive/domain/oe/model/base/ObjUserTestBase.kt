package io.dddrive.domain.oe.model.base

import io.dddrive.core.oe.model.base.ObjUserBase
import io.dddrive.domain.oe.model.ObjUserRepository

abstract class ObjUserTestBase(
	repository: ObjUserRepository,
	isNew: Boolean,
) : ObjUserBase(repository, isNew)
