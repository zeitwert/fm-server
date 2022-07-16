
package io.zeitwert.fm.building.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;
import io.zeitwert.fm.building.model.db.Tables;
import io.zeitwert.fm.building.model.db.tables.records.CodeBuildingElementDescriptionRecord;

@Component("codeBuildingElementDescriptionEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public final class CodeBuildingElementDescriptionEnum extends EnumerationBase<CodeBuildingElementDescription> {

	private static CodeBuildingElementDescriptionEnum INSTANCE;

	private CodeBuildingElementDescriptionEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeBuildingElementDescriptionRecord item : this.getDslContext()
				.selectFrom(Tables.CODE_BUILDING_ELEMENT_DESCRIPTION).fetch()) {
			this.addItem(new CodeBuildingElementDescription(this, item.getId(), item.getName(), item.getCategory()));
		}
	}

	public static CodeBuildingElementDescription getBuildingElementDescription(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
