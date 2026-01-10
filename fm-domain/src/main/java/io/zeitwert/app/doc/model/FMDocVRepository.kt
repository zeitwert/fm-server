package io.zeitwert.app.doc.model

import dddrive.app.doc.model.Doc
import dddrive.app.doc.model.DocRepository

interface FMDocVRepository : DocRepository<Doc> {

	fun isDoc(id: Any): Boolean

}
