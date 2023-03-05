
package io.zeitwert.fm.building.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.dddrive.app.service.api.AppContextSPI;
import io.dddrive.jooq.enums.JooqEnumerationBase;
import io.zeitwert.fm.building.model.db.Tables;
import io.zeitwert.fm.building.model.db.tables.records.CodeBuildingTypeRecord;

@Component("codeBuildingTypeEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public final class CodeBuildingTypeEnum extends JooqEnumerationBase<CodeBuildingType> {

	private static CodeBuildingTypeEnum INSTANCE;

	private CodeBuildingTypeEnum(AppContextSPI appContext, DSLContext dslContext) {
		super(CodeBuildingType.class, appContext, dslContext);
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
