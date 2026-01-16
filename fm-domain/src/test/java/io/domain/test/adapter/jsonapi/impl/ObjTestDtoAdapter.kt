package io.domain.test.adapter.jsonapi.impl

import dddrive.ddd.model.RepositoryDirectory
import io.domain.test.adapter.jsonapi.dto.ObjTestDto
import io.domain.test.model.ObjTest
import io.zeitwert.app.obj.api.jsonapi.base.ObjDtoAdapterBase
import org.springframework.stereotype.Component

@Component("objTestDtoAdapter")
class ObjTestDtoAdapter(
	directory: RepositoryDirectory,
) : ObjDtoAdapterBase<ObjTest, ObjTestDto>(
	ObjTest::class.java,
	"objTest",
	ObjTestDto::class.java,
	directory,
	{ ObjTestDto() },
) {

	init {
		config.field("firstNode", doInline = true)
		// 	config.relationship("mainContact", "contact", "mainContact")
		// 	config.relationship("logo", "document", "logoImage")
		// 	config.relationshipMany("contacts", "contact") { entity, dto ->
		// 		(entity as ObjAccount).contactList.map { DtoUtils.idToString(it)!! }
		// 	}
	}

}
