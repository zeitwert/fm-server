package io.zeitwert.fm.contact.model.impl

import io.zeitwert.fm.contact.model.ObjContact
import io.zeitwert.fm.contact.model.ObjContactPartAddress
import io.zeitwert.fm.contact.model.ObjContactRepository
import io.zeitwert.fm.contact.model.base.ObjContactBase
import io.zeitwert.fm.contact.model.base.ObjContactPartAddressBase
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase
import org.springframework.stereotype.Component

/**
 * Repository implementation for ObjContact using the NEW dddrive framework.
 */
@Component("objContactRepository")
class ObjContactRepositoryImpl :
	FMObjRepositoryBase<ObjContact>(
		ObjContactRepository::class.java,
		ObjContact::class.java,
		ObjContactBase::class.java,
		AGGREGATE_TYPE_ID,
	),
	ObjContactRepository {

	override fun registerParts() {
		super.registerParts()
		this.addPart(ObjContact::class.java, ObjContactPartAddress::class.java, ObjContactPartAddressBase::class.java)
	}

	companion object {

		private const val AGGREGATE_TYPE_ID = "obj_contact"
	}
}
