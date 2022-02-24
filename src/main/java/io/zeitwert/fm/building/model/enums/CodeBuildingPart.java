package io.zeitwert.fm.building.model.enums;

import io.zeitwert.ddd.enums.model.base.EnumeratedBase;

public final class CodeBuildingPart extends EnumeratedBase {

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

	//@formatter:off
	public CodeBuildingPart(
		CodeBuildingPartEnum enumeration,
		String id,
		String name,
		Double optimalRestoreDuration,
		Double optimalRestoreTimeValue,
		Double maximalRestoreDuration,
		Double maximalRestoreTimeValue,
		Double afterRestoreTimeValue,
		Double linearDuration,
		Double linearTimeValue,
		Double restoreCostPerc,
		Double newBuildCostPerc,
		Double c10,
		Double c9,
		Double c8,
		Double c7,
		Double c6,
		Double c5,
		Double c4,
		Double c3,
		Double c2,
		Double c1,
		Double c0
	) {
		super(enumeration, id, name);
		this.optimalRestoreDuration = optimalRestoreDuration;
		this.optimalRestoreTimeValue = optimalRestoreTimeValue;
		this.maximalRestoreDuration = maximalRestoreDuration;
		this.maximalRestoreTimeValue = maximalRestoreTimeValue;
		this.afterRestoreTimeValue = afterRestoreTimeValue;
		this.restoreCostPerc = restoreCostPerc;
		this.newBuildCostPerc = newBuildCostPerc;
		this.linearDuration = linearDuration;
		this.linearTimeValue = linearTimeValue;
		this.c10 = c10;
		this.c9 = c9;
		this.c8 = c8;
		this.c7 = c7;
		this.c6 = c6;
		this.c5 = c5;
		this.c4 = c4;
		this.c3 = c3;
		this.c2 = c2;
		this.c1 = c1;
		this.c0 = c0;
	}
	//@formatter:on

	public Double getOptimalRestoreDuration() {
		return this.optimalRestoreDuration;
	}

	public Double getOptimalRestoreTimeValue() {
		return this.optimalRestoreTimeValue;
	}

	public Double getMaximalRestoreDuration() {
		return this.maximalRestoreDuration;
	}

	public Double getMaximalRestoreTimeValue() {
		return this.maximalRestoreTimeValue;
	}

	public Double getAfterRestoreTimeValue() {
		return this.afterRestoreTimeValue;
	}

	public Double getRestoreCostPerc() {
		return this.restoreCostPerc;
	}

	public Double getNewBuildCostPerc() {
		return this.newBuildCostPerc;
	}

	public Double getLinearDuration() {
		return this.linearDuration;
	}

	public Double getLinearTimeValue() {
		return this.linearTimeValue;
	}

	public Double getC10() {
		return this.c10;
	}

	public Double getC9() {
		return this.c9;
	}

	public Double getC8() {
		return this.c8;
	}

	public Double getC7() {
		return this.c7;
	}

	public Double getC6() {
		return this.c6;
	}

	public Double getC5() {
		return this.c5;
	}

	public Double getC4() {
		return this.c4;
	}

	public Double getC3() {
		return this.c3;
	}

	public Double getC2() {
		return this.c2;
	}

	public Double getC1() {
		return this.c1;
	}

	public Double getC0() {
		return this.c0;
	}

}
