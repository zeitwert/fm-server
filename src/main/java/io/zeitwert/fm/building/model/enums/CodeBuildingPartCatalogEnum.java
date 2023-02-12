
package io.zeitwert.fm.building.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.dddrive.app.service.api.impl.Enumerations;
import io.dddrive.jooq.enums.JooqEnumerationBase;
import io.zeitwert.fm.building.model.db.Tables;
import io.zeitwert.fm.building.model.db.tables.records.CodeBuildingPartCatalogRecord;

@Component("codeBuildingPartCatalogEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public final class CodeBuildingPartCatalogEnum extends JooqEnumerationBase<CodeBuildingPartCatalog> {

	private static CodeBuildingPartCatalogEnum INSTANCE;

	private CodeBuildingPartCatalogEnum(Enumerations enums, DSLContext dslContext) {
		super(CodeBuildingPartCatalog.class, enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeBuildingPartCatalogRecord item : this.getDslContext().selectFrom(Tables.CODE_BUILDING_PART_CATALOG)
				.fetch()) {
			CodeBuildingPartCatalog part = CodeBuildingPartCatalog.builder()
					.enumeration(this)
					.id(item.getId())
					.name(item.getName())
					.parts(item.getParts())
					.build();
			this.addItem(part);
		}
	}

	public static CodeBuildingPartCatalog getPartCatalog(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
