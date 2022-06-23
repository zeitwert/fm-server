
package io.zeitwert.fm.building.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;
import io.zeitwert.fm.building.model.db.Tables;
import io.zeitwert.fm.building.model.db.tables.records.CodeBuildingPartRecord;

@Component("codeBuildingPartEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public final class CodeBuildingPartEnum extends EnumerationBase<CodeBuildingPart> {

	private static CodeBuildingPartEnum INSTANCE;

	private CodeBuildingPartEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	//@formatter:off
	private void init() {
		for (final CodeBuildingPartRecord item : this.getDslContext().selectFrom(Tables.CODE_BUILDING_PART).fetch()) {
			this.addItem(
				new CodeBuildingPart(
					this,
					item.getId(),
					item.getName(),
					item.getOptRestoreDuration().doubleValue(),
					item.getOptRestoreTimeValue().doubleValue(),
					item.getMaxRestoreDuration().doubleValue(),
					item.getMaxRestoreTimeValue().doubleValue(),
					item.getAfterRestoreTimeValue().doubleValue(),
					item.getLinearDuration().doubleValue(),
					item.getLinearTimeValue().doubleValue(),
					item.getRestoreCostPerc().doubleValue(),
					item.getNewBuildCostPerc().doubleValue(),
					item.getC10().doubleValue(),
					item.getC9().doubleValue(),
					item.getC8().doubleValue(),
					item.getC7().doubleValue(),
					item.getC6().doubleValue(),
					item.getC5().doubleValue(),
					item.getC4().doubleValue(),
					item.getC3().doubleValue(),
					item.getC2().doubleValue(),
					item.getC1().doubleValue(),
					item.getC0().doubleValue()
				)
			);
		}
	}
	//@formatter:on

	public static CodeBuildingPart getBuildingPart(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
