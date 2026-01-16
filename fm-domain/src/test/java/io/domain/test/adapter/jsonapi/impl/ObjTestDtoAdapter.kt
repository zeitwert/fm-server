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
		config.field("firstNode", { test ->
			fromProperty(test.getProperty("firstNode", Any::class), doInline = true)
		}, { _, _ ->
			// no-op
		})
	}

}
