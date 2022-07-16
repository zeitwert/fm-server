
package io.zeitwert.fm.building.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;
import io.zeitwert.fm.building.model.db.Tables;
import io.zeitwert.fm.building.model.db.tables.records.CodeBuildingPartCatalogRecord;

@Component("codeBuildingPartCatalogEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public final class CodeBuildingPartCatalogEnum extends EnumerationBase<CodeBuildingPartCatalog> {

	private static CodeBuildingPartCatalogEnum INSTANCE;

	private CodeBuildingPartCatalogEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeBuildingPartCatalogRecord item : this.getDslContext().selectFrom(Tables.CODE_BUILDING_PART_CATALOG)
				.fetch()) {
			this.addItem(new CodeBuildingPartCatalog(this, item.getId(), item.getName(), item.getParts()));
		}
	}

	public static CodeBuildingPartCatalog getPartCatalog(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
