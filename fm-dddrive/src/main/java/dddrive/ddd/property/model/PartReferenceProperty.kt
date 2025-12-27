package dddrive.ddd.property.model

import dddrive.ddd.core.model.Part

interface PartReferenceProperty<P : Part<*>> : ReferenceProperty<P, Int>
