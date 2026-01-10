package dddrive.ddd.property.model

import dddrive.ddd.model.Aggregate
import dddrive.ddd.model.Part

interface PartReferenceProperty<A : Aggregate, P : Part<A>> : ReferenceProperty<P, Int>
