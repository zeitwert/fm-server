
package io.zeitwert.fm.building.model.enums;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;
import io.zeitwert.fm.building.model.db.Tables;
import io.zeitwert.fm.building.model.db.tables.records.CodeBuildingRatingStatusRecord;

@Component("codeBuildingRatingStatusEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public final class CodeBuildingRatingStatusEnum extends EnumerationBase<CodeBuildingRatingStatus> {

	private static CodeBuildingRatingStatusEnum INSTANCE;

	private CodeBuildingRatingStatusEnum(AppContext appContext) {
		super(appContext, CodeBuildingRatingStatus.class);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeBuildingRatingStatusRecord item : this.getDslContext().selectFrom(Tables.CODE_BUILDING_RATING_STATUS)
				.fetch()) {
			CodeBuildingRatingStatus ratingStatus = CodeBuildingRatingStatus.builder()
					.enumeration(this)
					.id(item.getId())
					.name(item.getName())
					.build();
			this.addItem(ratingStatus);
		}
	}

	public static CodeBuildingRatingStatus getRatingStatus(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
