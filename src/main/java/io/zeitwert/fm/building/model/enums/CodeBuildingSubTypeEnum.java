
package io.zeitwert.fm.building.model.enums;

import java.util.List;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;
import io.zeitwert.fm.building.model.db.Tables;
import io.zeitwert.fm.building.model.db.tables.records.CodeBuildingSubTypeRecord;

@Component("codeBuildingSubTypeEnum")
@DependsOn({ "flyway", "flywayInitializer", "codeBuildingTypeEnum" })
public final class CodeBuildingSubTypeEnum extends EnumerationBase<CodeBuildingSubType> {

	private static CodeBuildingSubTypeEnum INSTANCE;

	private CodeBuildingSubTypeEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeBuildingSubTypeRecord item : this.getDslContext().selectFrom(Tables.CODE_BUILDING_SUB_TYPE)
				.fetch()) {
			CodeBuildingSubType subType = CodeBuildingSubType.builder()
					.enumeration(this)
					.id(item.getId())
					.name(item.getName())
					.buildingType(CodeBuildingTypeEnum.getBuildingType(item.getBuildingTypeId()))
					.build();
			this.addItem(subType);
		}
	}

	public static CodeBuildingSubType getBuildingSubType(String itemId) {
		return INSTANCE.getItem(itemId);
	}

	public static List<CodeBuildingSubType> getBuildingSubTypeList(CodeBuildingType buildingType) {
		return INSTANCE.getItems().stream().filter(i -> i.getBuildingType() == buildingType).toList();
	}

}
