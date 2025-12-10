package io.dddrive.core.ddd.model;

import java.time.OffsetDateTime;
import java.util.List;

import io.dddrive.core.oe.model.ObjUser;
import io.dddrive.core.property.model.PropertyChangeListener;
import io.dddrive.core.validation.model.AggregatePartValidation;

/**
 * A DDD Aggregate Root Meta Information.
 */
public interface AggregateMeta {

	AggregateRepository<?> getRepository();

	boolean isFrozen();

	boolean isInCalc();

	boolean isCalcEnabled();

	void disableCalc();

	void enableCalc();

	boolean isInLoad();

	void beginLoad();

	void endLoad();

	void addPropertyChangeListener(PropertyChangeListener listener);

	void removePropertyChangeListener(PropertyChangeListener listener);

	Integer getVersion();

	List<AggregatePartValidation> getValidations();

	OffsetDateTime getCreatedAt();

	ObjUser getCreatedByUser();

	OffsetDateTime getModifiedAt();

	ObjUser getModifiedByUser();

}
