
package io.zeitwert.fm.building.model.enums;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.dddrive.app.service.api.impl.Enumerations;
import io.dddrive.jooq.enums.JooqEnumerationBase;
import io.zeitwert.fm.building.model.db.Tables;
import io.zeitwert.fm.building.model.db.tables.records.CodeBuildingPriceIndexRecord;
import io.zeitwert.fm.building.model.db.tables.records.CodeBuildingPriceIndexValueRecord;

@Component("codeBuildingPriceIndexEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public final class CodeBuildingPriceIndexEnum extends JooqEnumerationBase<CodeBuildingPriceIndex> {

	private static CodeBuildingPriceIndexEnum INSTANCE;

	private CodeBuildingPriceIndexEnum(Enumerations enums, DSLContext dslContext) {
		super(CodeBuildingPriceIndex.class, enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeBuildingPriceIndexRecord item : this.getDslContext().selectFrom(Tables.CODE_BUILDING_PRICE_INDEX)
				.fetch()) {
			Integer minIndexYear = null;
			Integer maxIndexYear = null;
			Map<Integer, Double> indexPerYear = new HashMap<>();
			for (final CodeBuildingPriceIndexValueRecord value : this.getDslContext()
					.selectFrom(Tables.CODE_BUILDING_PRICE_INDEX_VALUE)
					.where(Tables.CODE_BUILDING_PRICE_INDEX_VALUE.BUILDING_PRICE_INDEX_ID.eq(item.getId())).fetch()) {
				if (minIndexYear == null || value.getYear() < minIndexYear) {
					minIndexYear = value.getYear();
				}
				if (maxIndexYear == null || value.getYear() > maxIndexYear) {
					maxIndexYear = value.getYear();
				}
				indexPerYear.put(value.getYear(), value.getValue().doubleValue());
			}
			CodeBuildingPriceIndex index = CodeBuildingPriceIndex.builder()
					.enumeration(this)
					.id(item.getId())
					.name(item.getName())
					.minIndexYear(minIndexYear)
					.maxIndexYear(maxIndexYear)
					.indexPerYear(indexPerYear)
					.build();
			this.addItem(index);
		}
	}

	public static CodeBuildingPriceIndex getBuildingPriceIndex(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
