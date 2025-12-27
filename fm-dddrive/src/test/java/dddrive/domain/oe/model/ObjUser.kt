package dddrive.domain.oe.model

import dddrive.app.obj.model.Obj

interface ObjUser : Obj {

	var email: String?

	var name: String?

}
