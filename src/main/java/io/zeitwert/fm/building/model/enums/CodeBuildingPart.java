package io.zeitwert.fm.building.model.enums;

import io.zeitwert.ddd.enums.model.base.EnumeratedBase;
import io.zeitwert.fm.building.service.api.dto.ProjectionPeriod;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

import static io.zeitwert.ddd.util.Check.assertThis;
import static io.zeitwert.ddd.util.Check.requireThis;

@Data
@SuperBuilder
public class CodeBuildingPart extends EnumeratedBase {

	static final List<String> FullTechRates = List.of("P6", "P7", "P8", "P54", "P55", "P9", "P50", "P51", "P10",
			"P63", "P64", "P65", "P66");
	static final List<String> HalfTechRates = List.of("P12", "P60", "P61", "P62");

	private final Double optimalRestoreDuration;
	private final Double optimalRestoreTimeValue;
	private final Double maximalRestoreDuration;
	private final Double maximalRestoreTimeValue;
	private final Double afterRestoreTimeValue;

	private final Double restoreCostPerc;
	private final Double newBuildCostPerc;

	private final Double linearDuration;
	private final Double linearTimeValue;

	private final Double c10;
	private final Double c9;
	private final Double c8;
	private final Double c7;
	private final Double c6;
	private final Double c5;
	private final Double c4;
	private final Double c3;
	private final Double c2;
	private final Double c1;
	private final Double c0;

	@Override
	public boolean equals(Object object) {
		return super.equals(object);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * Get the first renovation period for a given element and condition
	 * 
	 * @return first renovation period
	 */
	public ProjectionPeriod getNextRestoration(
			double elementValue,
			int conditionYear,
			double condition) {

		int startYear = 0;
		int restorationYear = 0;
		double restorationCosts = 0.0;
		if ((condition / 100) > this.getOptimalRestoreTimeValue()) {
			startYear = (int) Math.floor(this.getRelativeAge(condition / 100));
			restorationYear = (int) Math.floor(this.getRelativeAge(this.getOptimalRestoreTimeValue())) + 1;
			restorationCosts = this.getRestoreCostPerc() / 100 - this.getOptimalRestoreTimeValue();
		} else {
			restorationCosts = (this.getRestoreCostPerc() - condition) / 100;
		}
		int duration = restorationYear - startYear;

		return ProjectionPeriod.builder()
				.year(conditionYear + duration)
				.originalValue(elementValue)
				.timeValue(this.getOptimalRestoreTimeValue())
				.restorationCosts(Math.round(restorationCosts * elementValue))
				.build();
	}

	/**
	 * Get the timeValue for a given element and the relative age (in years)
	 * 
	 * @param relativeAge the (relative) age of the element in years
	 * @return time value ([0 .. 1])
	 */
	public double getTimeValue(double relativeAge) {
		if (this.getLinearDuration() > 0 && relativeAge <= this.getLinearDuration()) {
			return 1 - relativeAge / this.getLinearDuration() * (1 - this.getLinearTimeValue());
		}
		//@formatter:off
		return
			this.getC0() +
			this.getC1() * relativeAge +
			this.getC2() * Math.pow(relativeAge, 2) +
			this.getC3() * Math.pow(relativeAge, 3) +
			this.getC4() * Math.pow(relativeAge, 4) +
			this.getC5() * Math.pow(relativeAge, 5) +
			this.getC6() * Math.pow(relativeAge, 6) +
			this.getC7() * Math.pow(relativeAge, 7) +
			this.getC8() * Math.pow(relativeAge, 8) +
			this.getC9() * Math.pow(relativeAge, 9) +
			this.getC10() * Math.pow(relativeAge, 10);
		//@formatter:on
	}

	/**
	 * Get the relative age of a building element given its time value
	 * 
	 * @param timeValue the time value ([0 .. 1])
	 * @return relative age in years
	 */
	public double getRelativeAge(double timeValue) {
		if (timeValue > this.getLinearTimeValue()) {
			return (1 - timeValue) / (1 - this.getLinearTimeValue()) * this.getLinearDuration();
		}
		final double PRECISION = 0.0001;
		double prevT;
		double t;
		int i = 0;
		t = this.getOptimalRestoreDuration();
		prevT = t;
		while (Math.abs(this.getTimeValue(t) - timeValue) > PRECISION && i < 10) {
			t = prevT - (this.getTimeValue(prevT) - timeValue) / this.fDerivative(prevT);
			i += 1;
			prevT = t;
		}
		return t;
	}

	/**
	 * Get the expected lifetime of a building element given its time value
	 * 
	 * @param timeValue the time value ([0 .. 1])
	 * @return relative age in years
	 */
	public Integer getLifetime(double timeValue) {
		double optimalTimeValue = this.getOptimalRestoreTimeValue();
		if (timeValue <= optimalTimeValue) {
			return 0;
		}
		return (int) Math.floor(this.getRelativeAge(optimalTimeValue) - this.getRelativeAge(timeValue)) + 1;
	}

	private double fDerivative(double relativeAge) {
		return this.getC1() +
				2 * this.getC2() * relativeAge +
				3 * this.getC3() * Math.pow(relativeAge, 2) +
				4 * this.getC4() * Math.pow(relativeAge, 3) +
				5 * this.getC5() * Math.pow(relativeAge, 4) +
				6 * this.getC6() * Math.pow(relativeAge, 5) +
				7 * this.getC7() * Math.pow(relativeAge, 6) +
				8 * this.getC8() * Math.pow(relativeAge, 7) +
				9 * this.getC9() * Math.pow(relativeAge, 8) +
				10 * this.getC10() * Math.pow(relativeAge, 9);
	}

	/**
	 * Get the cost projection for a given element
	 * 
	 * @param buildingPart the building element
	 * @return cost projection
	 */
	public List<ProjectionPeriod> getProjection(
			double elementValue,
			int conditionYear,
			double condition,
			int startYear,
			int duration) {
		//@formatter:on

		requireThis(conditionYear <= startYear, "valid start year (" + conditionYear + "<=" + startYear + ")");
		requireThis(0 <= condition && condition <= 1.0, "valid condition (0 <=" + condition + " <= 1)");
		requireThis(duration <= 100, "duration <= 100");

		final int MaxProjectionYear = startYear + (int) Math.min(100.0, duration);
		final double RestorationTimeValue = this.getOptimalRestoreTimeValue();
		final double TotalRestorationCosts = this.getRestoreCostPerc() / 100;
		final double TimeValueAfterRestoration = this.getAfterRestoreTimeValue();
		final double RelativeAgeAfterRestoration = this.getRelativeAge(TimeValueAfterRestoration);

		final List<ProjectionPeriod> periodList = new ArrayList<>();

		double relativeAge = this.getRelativeAge(condition);
		double timeValue = condition;
		double techPart = this.getTechRate();
		double techRate = getTechRate(techPart);

		for (int simYear = conditionYear; simYear <= MaxProjectionYear; simYear++) {
			boolean needRestoration = timeValue <= RestorationTimeValue;
			double restorationCosts = 0.0;
			if (needRestoration) {
				restorationCosts = (TotalRestorationCosts - timeValue) * elementValue;
				relativeAge = RelativeAgeAfterRestoration;
			}
			double maintenanceRate = getMaintenanceRate(timeValue) / 100.0;
			if (simYear >= startYear) {
				//@formatter:off
				ProjectionPeriod period = ProjectionPeriod.builder()
					.year(simYear)
					.originalValue(elementValue)
					.timeValue(timeValue * elementValue)
					.restorationCosts(restorationCosts)
					.techPart(techPart)
					.techRate(techRate)
					.maintenanceRate(maintenanceRate)
					.maintenanceCosts(maintenanceRate * techRate * elementValue)
					.build();
				//@formatter:on
				periodList.add(period);
			}
			relativeAge += 1;
			timeValue = this.getTimeValue(relativeAge);
		}
		// assertThis(periodList.get(0).getYear() == startYear, "valid start year");
		assertThis(periodList.size() == duration + 1, "valid duration");
		return periodList;
	}

	public double getTechRate() {
		String id = this.getId();
		if (FullTechRates.contains(id)) {
			return 1.0;
		} else if (HalfTechRates.contains(id)) {
			return 0.5;
		}
		return 0.0;
	}

	public static double getTechRate(double techPart) {
		return getRatio(techPart, 0.09, 0.22, 0.5, 1.0);
	}

	public static double getMaintenanceRate(double timeValue) {
		if (timeValue >= 1.0) {
			return 0.5;
		} else if (timeValue >= 0.94) {
			return getRatio(timeValue, 0.94, 1.0, 0.64, 0.5);
		} else if (timeValue >= 0.85) {
			return getRatio(timeValue, 0.85, 0.94, 1.1, 0.64);
		} else if (timeValue >= 0.75) {
			return getRatio(timeValue, 0.75, 0.85, 2.0, 1.1);
		} else if (timeValue >= 0.67) {
			return getRatio(timeValue, 0.67, 0.75, 2.0, 2.0);
		} else if (timeValue >= 0.60) {
			return getRatio(timeValue, 0.60, 0.67, 0.5, 2.0);
		}
		return getRatio(timeValue, 0.00, 0.60, 0.75, 0.5);
	}

	private static double getRatio(double value, double lowBound, double highBound, double lowValue, double highValue) {
		if (value <= lowBound) {
			return lowValue;
		} else if (value >= highBound) {
			return highValue;
		}
		return lowValue + (value - lowBound) / (highBound - lowBound) * (highValue - lowValue);
	}

}
