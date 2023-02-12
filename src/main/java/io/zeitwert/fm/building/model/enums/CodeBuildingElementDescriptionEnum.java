
package io.zeitwert.fm.building.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.dddrive.app.service.api.impl.Enumerations;
import io.dddrive.jooq.enums.JooqEnumerationBase;
import io.zeitwert.fm.building.model.db.Tables;
import io.zeitwert.fm.building.model.db.tables.records.CodeBuildingElementDescriptionRecord;

@Component("codeBuildingElementDescriptionEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public final class CodeBuildingElementDescriptionEnum extends JooqEnumerationBase<CodeBuildingElementDescription> {

	private static CodeBuildingElementDescriptionEnum INSTANCE;

	private CodeBuildingElementDescriptionEnum(Enumerations enums, DSLContext dslContext) {
		super(CodeBuildingElementDescription.class, enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeBuildingElementDescriptionRecord item : this.getDslContext()
				.selectFrom(Tables.CODE_BUILDING_ELEMENT_DESCRIPTION).fetch()) {
			CodeBuildingElementDescription description = CodeBuildingElementDescription.builder()
					.enumeration(this)
					.id(item.getId())
					.name(item.getName())
					.category(item.getCategory())
					.build();
			this.addItem(description);
		}
	}

	public static CodeBuildingElementDescription getBuildingElementDescription(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
