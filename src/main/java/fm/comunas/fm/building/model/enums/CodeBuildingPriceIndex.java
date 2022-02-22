
package fm.comunas.fm.building.model.enums;

import fm.comunas.ddd.enums.model.base.EnumeratedBase;

import java.util.Map;

public class CodeBuildingPriceIndex extends EnumeratedBase {

	private final Integer minIndexYear;
	private final Integer maxIndexYear;
	private final Map<Integer, Double> indexPerYear;

	public CodeBuildingPriceIndex(CodeBuildingPriceIndexEnum enumeration, String id, String name, Integer minIndexYear,
			Integer maxIndexYear, Map<Integer, Double> indexPerYear) {
		super(enumeration, id, name);
		this.minIndexYear = minIndexYear;
		this.maxIndexYear = maxIndexYear;
		this.indexPerYear = indexPerYear;
	}

	public double priceAt(int origYear, double origPrice, int targetYear) {
		if (targetYear > maxIndexYear) {
			targetYear = maxIndexYear;
		}
		if (origYear < minIndexYear) {
			origYear = minIndexYear;
		}
		if (indexPerYear.get(origYear) == null || indexPerYear.get(targetYear) == null) {
			return origPrice;
		}
		return indexPerYear.get(targetYear) / indexPerYear.get(origYear) * origPrice;
	}

}
