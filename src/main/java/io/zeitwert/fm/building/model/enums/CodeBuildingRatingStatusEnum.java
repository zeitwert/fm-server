
package io.zeitwert.fm.building.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;
import io.zeitwert.fm.building.model.db.Tables;
import io.zeitwert.fm.building.model.db.tables.records.CodeBuildingRatingStatusRecord;

@Component("codeBuildingRatingStatusEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public final class CodeBuildingRatingStatusEnum extends EnumerationBase<CodeBuildingRatingStatus> {

	private static CodeBuildingRatingStatusEnum INSTANCE;

	@Autowired
	private CodeBuildingRatingStatusEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeBuildingRatingStatusRecord item : this.getDslContext().selectFrom(Tables.CODE_BUILDING_RATING_STATUS)
				.fetch()) {
			this.addItem(new CodeBuildingRatingStatus(this, item.getId(), item.getName()));
		}
	}

	public static CodeBuildingRatingStatus getRatingStatus(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
