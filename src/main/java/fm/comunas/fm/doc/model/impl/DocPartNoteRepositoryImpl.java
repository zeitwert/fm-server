package fm.comunas.fm.doc.model.impl;

import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import fm.comunas.ddd.app.service.api.AppContext;
import fm.comunas.ddd.doc.model.Doc;
import fm.comunas.fm.doc.model.DocPartNote;
import fm.comunas.fm.doc.model.DocPartNoteRepository;
import fm.comunas.fm.doc.model.base.DocPartNoteBase;
import fm.comunas.fm.item.model.base.ItemPartNoteRepositoryBase;

@Component("docPartNoteRepository")
public class DocPartNoteRepositoryImpl
		extends ItemPartNoteRepositoryBase<Doc>
		implements DocPartNoteRepository {

	private static final String PART_TYPE = "doc_part_note";
	private final String DOC_PART_ID_SEQ = "doc_part_id_seq";

	//@formatter:off
	protected DocPartNoteRepositoryImpl(
		AppContext appContext,
		DSLContext dslContext
	) {
		super(
			Doc.class,
			DocPartNote.class,
			DocPartNoteBase.class,
			PART_TYPE,
			appContext,
			dslContext
		);
	}
	//@formatter:on

	@Override
	public Integer nextPartId() {
		return this.dslContext.nextval(DOC_PART_ID_SEQ).intValue();
	}

}
