
package fm.comunas.fm.building.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import fm.comunas.ddd.app.service.api.Enumerations;
import fm.comunas.ddd.enums.model.base.EnumerationBase;
import fm.comunas.fm.building.model.db.Tables;
import fm.comunas.fm.building.model.db.tables.records.CodeBuildingElementDescriptionRecord;

@Component("codeBuildingElementDescriptionEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public final class CodeBuildingElementDescriptionEnum extends EnumerationBase<CodeBuildingElementDescription> {

	private static CodeBuildingElementDescriptionEnum INSTANCE;

	@Autowired
	private CodeBuildingElementDescriptionEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeBuildingElementDescriptionRecord item : this.getDslContext()
				.selectFrom(Tables.CODE_BUILDING_ELEMENT_DESCRIPTION).fetch()) {
			this.addItem(new CodeBuildingElementDescription(this, item.getId(), item.getName(), item.getCategory()));
		}
	}

	public static CodeBuildingElementDescription getBuildingElementDescription(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
