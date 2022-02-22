
package fm.comunas.fm.building.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import fm.comunas.ddd.app.service.api.Enumerations;
import fm.comunas.ddd.enums.model.base.EnumerationBase;
import fm.comunas.fm.building.model.db.Tables;
import fm.comunas.fm.building.model.db.tables.records.CodeBuildingMaintenanceStrategyRecord;

@Component("codeBuildingMaintenaceStrategyEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public final class CodeBuildingMaintenanceStrategyEnum extends EnumerationBase<CodeBuildingMaintenanceStrategy> {

	private static CodeBuildingMaintenanceStrategyEnum INSTANCE;

	@Autowired
	private CodeBuildingMaintenanceStrategyEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeBuildingMaintenanceStrategyRecord item : this.getDslContext()
				.selectFrom(Tables.CODE_BUILDING_MAINTENANCE_STRATEGY).fetch()) {
			this.addItem(new CodeBuildingMaintenanceStrategy(this, item.getId(), item.getName()));
		}
	}

	public static CodeBuildingMaintenanceStrategy getBuildingMaintenanceStrategy(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
