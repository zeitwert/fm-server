package io.dddrive.core.property.model

import io.dddrive.core.ddd.model.Part

interface PartReferenceProperty<P : Part<*>> : ReferenceProperty<P, Int>
