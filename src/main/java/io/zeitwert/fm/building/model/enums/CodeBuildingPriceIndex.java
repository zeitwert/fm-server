
package io.zeitwert.fm.building.model.enums;

import io.zeitwert.ddd.enums.model.base.EnumeratedBase;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Data
@SuperBuilder
public class CodeBuildingPriceIndex extends EnumeratedBase {

	private final Integer minIndexYear;
	private final Integer maxIndexYear;
	private final Map<Integer, Double> indexPerYear;

	@Override
	public boolean equals(Object other) {
		return super.equals(other);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
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
		if (targetYear > this.maxIndexYear) {
			targetYear = this.maxIndexYear;
		}
		if (origYear < this.minIndexYear) {
			origYear = this.minIndexYear;
		}
		if (this.indexPerYear.get(origYear) == null || this.indexPerYear.get(targetYear) == null) {
			return 1.0;
		}
		return this.indexPerYear.get(targetYear) / this.indexPerYear.get(origYear);
	}

	public double priceAt(int origYear, double origPrice, int targetYear, double inflationRate) {
		double targetPrice = this.indexAt(origYear, targetYear) * origPrice;
		if (targetYear > this.maxIndexYear && inflationRate > 0) {
			targetPrice = targetPrice * Math.pow(1.0 + inflationRate / 100.0, targetYear - this.maxIndexYear);
		}
		return targetPrice;
	}

}
