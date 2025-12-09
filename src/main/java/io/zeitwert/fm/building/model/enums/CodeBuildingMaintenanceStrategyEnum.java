
package io.zeitwert.fm.building.model.enums;

import jakarta.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.dddrive.app.service.api.AppContextSPI;
import io.dddrive.jooq.enums.JooqEnumerationBase;
import io.zeitwert.fm.building.model.db.Tables;
import io.zeitwert.fm.building.model.db.tables.records.CodeBuildingMaintenanceStrategyRecord;

@Component("codeBuildingMaintenaceStrategyEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public final class CodeBuildingMaintenanceStrategyEnum extends JooqEnumerationBase<CodeBuildingMaintenanceStrategy> {

	private static CodeBuildingMaintenanceStrategyEnum INSTANCE;

	private CodeBuildingMaintenanceStrategyEnum(AppContextSPI appContext, DSLContext dslContext) {
		super(CodeBuildingMaintenanceStrategy.class, appContext, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeBuildingMaintenanceStrategyRecord item : this.getDslContext()
				.selectFrom(Tables.CODE_BUILDING_MAINTENANCE_STRATEGY).fetch()) {
			CodeBuildingMaintenanceStrategy maintenanceStrategy = CodeBuildingMaintenanceStrategy.builder()
					.enumeration(this)
					.id(item.getId())
					.name(item.getName())
					.build();
			this.addItem(maintenanceStrategy);
		}
	}

	public static CodeBuildingMaintenanceStrategy getMaintenanceStrategy(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
