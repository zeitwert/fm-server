package io.dddrive.domain.oe.model.impl

import io.dddrive.domain.oe.model.ObjUserRepository
import io.dddrive.oe.model.base.ObjUserBase

class ObjUserTestImpl(
	override val repository: ObjUserRepository,
	isNew: Boolean,
) : ObjUserBase(repository, isNew)
