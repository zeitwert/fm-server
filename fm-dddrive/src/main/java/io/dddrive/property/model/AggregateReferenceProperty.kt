package io.dddrive.property.model

import io.dddrive.ddd.model.Aggregate

interface AggregateReferenceProperty<A : Aggregate> : ReferenceProperty<A, Any>
