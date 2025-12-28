package dddrive.ddd.property.model

import dddrive.ddd.core.model.Aggregate
import dddrive.ddd.core.model.Part

interface PartReferenceProperty<A : Aggregate, P : Part<A>> : ReferenceProperty<P, Int>
