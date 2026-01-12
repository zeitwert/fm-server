package io.zeitwert.persist

import dddrive.app.doc.model.Doc
import dddrive.ddd.model.AggregatePersistenceProvider
import dddrive.query.QuerySpec

interface DocPersistenceProvider : AggregatePersistenceProvider<Doc> {

	fun isDoc(id: Any): Boolean

}
