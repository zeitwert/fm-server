
package io.zeitwert.ddd.aggregate.model;

import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateType;
import io.zeitwert.ddd.entity.model.EntityMeta;
import io.zeitwert.ddd.validation.model.AggregatePartValidation;

import java.util.List;

/**
 * A DDD Aggregate Root Meta Information.
 */
public interface AggregateMeta extends EntityMeta {

	CodeAggregateType getAggregateType();

	List<AggregatePartValidation> getValidationList();

}
