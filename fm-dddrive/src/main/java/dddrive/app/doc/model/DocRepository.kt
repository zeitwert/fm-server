package dddrive.app.doc.model

import dddrive.ddd.model.AggregateRepository
import dddrive.hex.IncomingPort

interface DocRepository<D : Doc> : AggregateRepository<D>, IncomingPort
