package dddrive.app.doc.model

import dddrive.ddd.model.AggregateRepository

interface DocRepository<D : Doc> : AggregateRepository<D>
