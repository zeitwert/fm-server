package io.zeitwert.fm.oe.model

import io.dddrive.core.oe.model.ObjTenant
import io.zeitwert.fm.dms.model.ObjDocument
import io.zeitwert.fm.oe.model.enums.CodeTenantType
import java.math.BigDecimal

interface ObjTenantFM : ObjTenant {

	var tenantType: CodeTenantType?

	var inflationRate: BigDecimal?

	var discountRate: BigDecimal?

	val users: List<ObjUserFM>

	val logoImageId: Any?

	val logoImage: ObjDocument?

}
