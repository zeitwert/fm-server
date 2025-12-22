package io.dddrive.property.model

import io.dddrive.ddd.model.Part

interface PartReferenceProperty<P : Part<*>> : ReferenceProperty<P, Int>
