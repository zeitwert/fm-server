
package fm.comunas.fm.building.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import fm.comunas.ddd.app.service.api.Enumerations;
import fm.comunas.ddd.enums.model.base.EnumerationBase;
import fm.comunas.fm.building.model.db.Tables;
import fm.comunas.fm.building.model.db.tables.records.CodeBuildingTypeRecord;

@Component("codeBuildingTypeEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public final class CodeBuildingTypeEnum extends EnumerationBase<CodeBuildingType> {

	private static CodeBuildingTypeEnum INSTANCE;

	@Autowired
	private CodeBuildingTypeEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeBuildingTypeRecord item : this.getDslContext().selectFrom(Tables.CODE_BUILDING_TYPE).fetch()) {
			this.addItem(new CodeBuildingType(this, item.getId(), item.getName()));
		}
	}

	public static CodeBuildingType getBuildingType(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
