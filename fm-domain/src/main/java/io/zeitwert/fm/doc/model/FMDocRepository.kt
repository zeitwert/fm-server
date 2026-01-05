package io.zeitwert.fm.doc.model

import dddrive.app.doc.model.Doc
import dddrive.app.doc.model.DocRepository

interface FMDocRepository<D : Doc> : DocRepository<D>
