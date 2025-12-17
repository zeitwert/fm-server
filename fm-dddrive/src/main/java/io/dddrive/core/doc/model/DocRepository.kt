package io.dddrive.core.doc.model

import io.dddrive.core.ddd.model.AggregateRepository

interface DocRepository<D : Doc> : AggregateRepository<D>
