package io.zeitwert.fm.contact.model.impl

import io.zeitwert.app.model.SessionContext
import io.zeitwert.dddrive.obj.model.base.FMObjRepositoryBase
import io.zeitwert.fm.contact.model.ObjContact
import io.zeitwert.fm.contact.model.ObjContactPartAddress
import io.zeitwert.fm.contact.model.ObjContactRepository
import org.springframework.stereotype.Component

/**
 * Repository implementation for ObjContact using the delegation-based framework.
 */
@Component("objContactRepository")
class ObjContactRepositoryImpl(
	override val sessionContext: SessionContext,
) : FMObjRepositoryBase<ObjContact>(
		ObjContact::class.java,
		AGGREGATE_TYPE_ID,
	),
	ObjContactRepository {

	override fun createAggregate(isNew: Boolean): ObjContact = ObjContactImpl(this, isNew)

	override fun registerParts() {
		super.registerParts()
		this.addPart(ObjContactPartAddress::class.java, ::ObjContactPartAddressImpl)
	}

	companion object {

		private const val AGGREGATE_TYPE_ID = "obj_contact"
	}
}
