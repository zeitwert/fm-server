
package io.zeitwert.fm.building.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;
import io.zeitwert.fm.building.model.db.Tables;
import io.zeitwert.fm.building.model.db.tables.records.CodeBuildingRatingStatusRecord;

@Component("codeBuildingRatingStatusEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public final class CodeBuildingRatingStatusEnum extends EnumerationBase<CodeBuildingRatingStatus> {

	private static CodeBuildingRatingStatusEnum INSTANCE;

	private CodeBuildingRatingStatusEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
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
