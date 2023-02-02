package io.zeitwert.ddd.db.model;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.enums.model.Enumerated;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.EnumSetProperty;
import io.zeitwert.ddd.property.model.PartListProperty;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.ReferenceSetProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.ddd.property.model.base.EntityWithPropertiesSPI;

public interface PersistenceProvider<A extends Aggregate> {

	Class<?> getEntityClass();

	/**
	 * Create a new Aggregate instance (purely technical)
	 * 
	 * @return new Aggregate
	 */
	A doCreate();

	/**
	 * Initialise the database records of an Aggregate with basic fields (id,
	 * tenantId) after creation (internal, technical callback).
	 *
	 * @param id       aggregate id
	 * @param tenantId tenant id
	 */
	void doInit(A aggregate, Integer id, Integer tenantId);

	/**
	 * Load core aggregate data from database and instantiate a new Aggregate. This
	 * must not load Parts, they will be loaded by @see AggregateSPI.doGet
	 * 
	 * @param id aggregate id
	 * @return instantiated Aggregate
	 */
	A doLoad(Integer id);

	/**
	 * Store the database record(s) (of the Aggregate only). The Parts will be
	 * stored from the repository.
	 * 
	 * @param aggregate aggregate to store
	 */
	void doStore(A aggregate);

	<T> SimpleProperty<T> getSimpleProperty(EntityWithPropertiesSPI entity, String name, Class<T> type);

	<E extends Enumerated> EnumProperty<E> getEnumProperty(EntityWithPropertiesSPI entity, String name,
			Class<E> enumClass);

	<E extends Enumerated> EnumSetProperty<E> getEnumSetProperty(EntityWithPropertiesSPI entity, String name,
			Class<E> enumClass);

	<T extends Aggregate> ReferenceProperty<T> getReferenceProperty(EntityWithPropertiesSPI entity, String name,
			Class<T> aggregateClass);

	<T extends Aggregate> ReferenceSetProperty<T> getReferenceSetProperty(EntityWithPropertiesSPI entity, String name,
			Class<T> aggregateClass);

	<P extends Part<?>> PartListProperty<P> getPartListProperty(EntityWithPropertiesSPI entity, String name,
			Class<P> partType);

}
