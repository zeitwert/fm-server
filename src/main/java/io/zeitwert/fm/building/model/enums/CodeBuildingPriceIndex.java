
package io.zeitwert.fm.building.model.enums;

import io.zeitwert.ddd.enums.model.base.EnumeratedBase;

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

	public Integer getMinIndexYear() {
		return this.minIndexYear;
	}

	public Integer getMaxIndexYear() {
		return this.maxIndexYear;
	}

	public double indexAt(int year) {
		return this.maxIndexYear;
	}

	public double indexAt(int origYear, int targetYear) {
		if (targetYear > maxIndexYear) {
			targetYear = maxIndexYear;
		}
		if (origYear < minIndexYear) {
			origYear = minIndexYear;
		}
		if (indexPerYear.get(origYear) == null || indexPerYear.get(targetYear) == null) {
			return 1.0;
		}
		return indexPerYear.get(targetYear) / indexPerYear.get(origYear);
	}

	public double priceAt(int origYear, double origPrice, int targetYear, double inflationRate) {
		double targetPrice = this.indexAt(origYear, targetYear) * origPrice;
		if (targetYear > maxIndexYear && inflationRate > 0) {
			targetPrice = targetPrice * Math.pow(1.0 + inflationRate / 100.0, targetYear - maxIndexYear);
		}
		return targetPrice;
	}

}
