package io.zeitwert.fm.oe.model

import dddrive.app.obj.model.Obj
import io.zeitwert.fm.dms.model.ObjDocument
import io.zeitwert.fm.oe.model.enums.CodeTenantType
import java.math.BigDecimal

interface ObjTenant : Obj {

	var key: String?

	var name: String?

	var description: String?

	var tenantType: CodeTenantType?

	var inflationRate: BigDecimal?

	var discountRate: BigDecimal?

	val users: List<ObjUser>

	var logoImageId: Any?

	val logoImage: ObjDocument?

}
