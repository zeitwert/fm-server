package io.zeitwert.fm.doc.model

import dddrive.app.doc.model.Doc
import dddrive.app.doc.model.DocRepository
import io.zeitwert.dddrive.model.FMAggregateRepository

interface FMDocRepository<D : Doc> :
	DocRepository<D>,
	FMAggregateRepository
