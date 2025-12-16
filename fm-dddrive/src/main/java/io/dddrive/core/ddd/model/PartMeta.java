package io.dddrive.core.ddd.model;

public interface PartMeta<A extends Aggregate> {

	A getAggregate();

	PartRepository<A, ? extends Part<A>> getRepository();

	boolean isNew();

	boolean isCalcEnabled();

	void disableCalc();

	void enableCalc();

}
