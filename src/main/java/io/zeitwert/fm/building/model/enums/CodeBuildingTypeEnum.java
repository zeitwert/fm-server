
package io.zeitwert.fm.building.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;
import io.zeitwert.fm.building.model.db.Tables;
import io.zeitwert.fm.building.model.db.tables.records.CodeBuildingTypeRecord;

@Component("codeBuildingTypeEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public final class CodeBuildingTypeEnum extends EnumerationBase<CodeBuildingType> {

	private static CodeBuildingTypeEnum INSTANCE;

	private CodeBuildingTypeEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeBuildingTypeRecord item : this.getDslContext().selectFrom(Tables.CODE_BUILDING_TYPE).fetch()) {
			this.addItem(new CodeBuildingType(this, item.getId(), item.getName()));
		}
	}

	public static CodeBuildingType getBuildingType(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
