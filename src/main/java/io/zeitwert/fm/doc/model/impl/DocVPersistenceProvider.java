package io.zeitwert.fm.doc.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import org.jooq.DSLContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.jooq.persistence.DocPersistenceProviderBase;

@Configuration("docPersistenceProvider")
@DependsOn("codePartListTypeEnum")
public class DocVPersistenceProvider extends DocPersistenceProviderBase<Doc> {

	public DocVPersistenceProvider(DSLContext dslContext) {
		super(Doc.class, dslContext);
	}

	@Override
	public Doc doCreate() {
		return this.doCreate(null);
	}

	@Override
	public Doc doLoad(Integer docId) {
		requireThis(docId != null, "docId not null");
		return this.doLoad(docId, null);
	}

}
