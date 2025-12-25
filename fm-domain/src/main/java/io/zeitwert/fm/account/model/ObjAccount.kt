package io.zeitwert.fm.account.model

import io.dddrive.obj.model.Obj
import io.zeitwert.fm.account.model.enums.CodeAccountType
import io.zeitwert.fm.account.model.enums.CodeClientSegment
import io.zeitwert.fm.account.model.enums.CodeCurrency
import io.zeitwert.fm.collaboration.model.ItemWithNotes
import io.zeitwert.fm.contact.model.ObjContact
import io.zeitwert.fm.dms.model.ObjDocument
import java.math.BigDecimal

interface ObjAccount : Obj, ItemWithNotes {

  var name: String?

  var description: String?

  var accountType: CodeAccountType?

  var clientSegment: CodeClientSegment?

  var referenceCurrency: CodeCurrency?

  var inflationRate: BigDecimal?

  var discountRate: BigDecimal?

  var logoImageId: Any?

  val logoImage: ObjDocument?

  var mainContactId: Any?

  val mainContact: ObjContact?

  val contactList: List<Any>
}
