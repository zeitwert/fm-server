
package fm.comunas.fm.building.model.enums;

import javax.annotation.PostConstruct;

import java.util.HashMap;
import java.util.Map;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import fm.comunas.ddd.app.service.api.Enumerations;
import fm.comunas.ddd.enums.model.base.EnumerationBase;
import fm.comunas.fm.building.model.db.Tables;
import fm.comunas.fm.building.model.db.tables.records.CodeBuildingPriceIndexRecord;
import fm.comunas.fm.building.model.db.tables.records.CodeBuildingPriceIndexValueRecord;

@Component("codeBuildingPriceIndexEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public final class CodeBuildingPriceIndexEnum extends EnumerationBase<CodeBuildingPriceIndex> {

	private static CodeBuildingPriceIndexEnum INSTANCE;

	@Autowired
	private CodeBuildingPriceIndexEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
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
			this.addItem(
					new CodeBuildingPriceIndex(this, item.getId(), item.getName(), minIndexYear, maxIndexYear, indexPerYear));
		}
	}

	public static CodeBuildingPriceIndex getBuildingPriceIndex(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
