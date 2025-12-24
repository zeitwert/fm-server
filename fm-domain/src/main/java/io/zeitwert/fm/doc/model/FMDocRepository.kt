package io.zeitwert.fm.doc.model

import io.dddrive.doc.model.Doc
import io.dddrive.doc.model.DocRepository
import io.zeitwert.dddrive.model.FMAggregateRepository

interface FMDocRepository<D : Doc> :
	DocRepository<D>,
	FMAggregateRepository
