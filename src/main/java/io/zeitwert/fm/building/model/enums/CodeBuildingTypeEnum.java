
package io.zeitwert.fm.building.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.fm.building.model.db.Tables;
import io.zeitwert.fm.building.model.db.tables.records.CodeBuildingTypeRecord;
import io.zeitwert.jooq.repository.JooqEnumerationBase;

@Component("codeBuildingTypeEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public final class CodeBuildingTypeEnum extends JooqEnumerationBase<CodeBuildingType> {

	private static CodeBuildingTypeEnum INSTANCE;

	private CodeBuildingTypeEnum(Enumerations enums, DSLContext dslContext) {
		super(CodeBuildingType.class, enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeBuildingTypeRecord item : this.getDslContext().selectFrom(Tables.CODE_BUILDING_TYPE).fetch()) {
			CodeBuildingType buildingType = CodeBuildingType.builder()
					.enumeration(this)
					.id(item.getId())
					.name(item.getName())
					.build();
			this.addItem(buildingType);
		}
	}

	public static CodeBuildingType getBuildingType(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
