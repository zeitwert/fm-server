
package io.zeitwert.fm.building.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.dddrive.app.service.api.AppContextSPI;
import io.dddrive.jooq.enums.JooqEnumerationBase;
import io.zeitwert.fm.building.model.db.Tables;
import io.zeitwert.fm.building.model.db.tables.records.CodeBuildingPartRecord;

@Component("codeBuildingPartEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public final class CodeBuildingPartEnum extends JooqEnumerationBase<CodeBuildingPart> {

	private static CodeBuildingPartEnum INSTANCE;

	private CodeBuildingPartEnum(AppContextSPI appContext, DSLContext dslContext) {
		super(CodeBuildingPart.class, appContext, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeBuildingPartRecord item : this.getDslContext().selectFrom(Tables.CODE_BUILDING_PART).fetch()) {
			CodeBuildingPart part = CodeBuildingPart.builder()
					.enumeration(this)
					.id(item.getId())
					.name(item.getName())
					.optimalRestoreDuration(item.getOptRestoreDuration().doubleValue())
					.optimalRestoreTimeValue(item.getOptRestoreTimeValue().doubleValue())
					.maximalRestoreDuration(item.getMaxRestoreDuration().doubleValue())
					.maximalRestoreTimeValue(item.getMaxRestoreTimeValue().doubleValue())
					.afterRestoreTimeValue(item.getAfterRestoreTimeValue().doubleValue())
					.linearDuration(item.getLinearDuration().doubleValue())
					.linearTimeValue(item.getLinearTimeValue().doubleValue())
					.restoreCostPerc(item.getRestoreCostPerc().doubleValue())
					.newBuildCostPerc(item.getNewBuildCostPerc().doubleValue())
					.c10(item.getC10().doubleValue())
					.c9(item.getC9().doubleValue())
					.c8(item.getC8().doubleValue())
					.c7(item.getC7().doubleValue())
					.c6(item.getC6().doubleValue())
					.c5(item.getC5().doubleValue())
					.c4(item.getC4().doubleValue())
					.c3(item.getC3().doubleValue())
					.c2(item.getC2().doubleValue())
					.c1(item.getC1().doubleValue())
					.c0(item.getC0().doubleValue())
					.build();
			this.addItem(part);
		}
	}

	public static CodeBuildingPart getBuildingPart(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
