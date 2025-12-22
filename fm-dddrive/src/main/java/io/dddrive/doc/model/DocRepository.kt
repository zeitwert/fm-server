package io.dddrive.doc.model

import io.dddrive.ddd.model.AggregateRepository

interface DocRepository<D : Doc> : AggregateRepository<D>
