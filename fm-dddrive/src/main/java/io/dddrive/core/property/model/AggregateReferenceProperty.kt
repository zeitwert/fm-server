package io.dddrive.core.property.model

import io.dddrive.core.ddd.model.Aggregate

interface AggregateReferenceProperty<A : Aggregate> : ReferenceProperty<A, Any>
