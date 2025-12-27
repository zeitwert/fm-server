package dddrive.app.doc.model

import dddrive.ddd.core.model.AggregateRepository

interface DocRepository<D : Doc> : dddrive.ddd.core.model.AggregateRepository<D>
