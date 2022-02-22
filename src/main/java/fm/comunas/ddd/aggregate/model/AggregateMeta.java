
package fm.comunas.ddd.aggregate.model;

import fm.comunas.ddd.aggregate.model.enums.CodeAggregateType;
import fm.comunas.ddd.entity.model.EntityMeta;
import fm.comunas.ddd.validation.model.AggregatePartValidation;

import java.util.List;

/**
 * A DDD Aggregate Root Meta Information.
 */
public interface AggregateMeta extends EntityMeta {

	CodeAggregateType getAggregateType();

	List<AggregatePartValidation> getValidationList();

}
