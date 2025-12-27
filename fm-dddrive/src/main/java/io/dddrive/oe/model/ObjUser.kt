package io.dddrive.oe.model

import dddrive.app.obj.model.Obj

interface ObjUser : Obj {

	var email: String?

	var password: String?

	var name: String?

	var description: String?

}
