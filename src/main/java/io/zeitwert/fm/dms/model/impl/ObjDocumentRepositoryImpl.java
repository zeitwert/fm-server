
package io.zeitwert.fm.dms.model.impl;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.exception.NoDataFoundException;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static io.zeitwert.ddd.util.Check.requireThis;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.dms.model.base.ObjDocumentBase;
import io.zeitwert.fm.dms.model.db.Tables;
import io.zeitwert.fm.dms.model.db.tables.ObjDocumentPartContent;
import io.zeitwert.fm.dms.model.db.tables.records.ObjDocumentPartContentRecord;
import io.zeitwert.fm.dms.model.db.tables.records.ObjDocumentRecord;
import io.zeitwert.fm.dms.model.db.tables.records.ObjDocumentVRecord;
import io.zeitwert.fm.dms.model.enums.CodeContentType;
import io.zeitwert.fm.dms.model.enums.CodeContentTypeEnum;
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase;

import javax.annotation.PostConstruct;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.ObjPartItemRepository;
import io.zeitwert.ddd.obj.model.ObjPartTransitionRepository;
import io.zeitwert.ddd.session.model.SessionInfo;

@Component("objDocumentRepository")
public class ObjDocumentRepositoryImpl extends FMObjRepositoryBase<ObjDocument, ObjDocumentVRecord>
		implements ObjDocumentRepository {

	private static final String ITEM_TYPE = "obj_document";

	private static final ObjDocumentPartContent DOCUMENT_CONTENT = Tables.OBJ_DOCUMENT_PART_CONTENT;
	private static final TableField<ObjDocumentPartContentRecord, Integer> OBJ_ID = DOCUMENT_CONTENT.OBJ_ID;
	private static final TableField<ObjDocumentPartContentRecord, Integer> VERSION_NR = DOCUMENT_CONTENT.VERSION_NR;
	private static final TableField<ObjDocumentPartContentRecord, String> CONTENT_TYPE_ID = DOCUMENT_CONTENT.CONTENT_TYPE_ID;
	private static final TableField<ObjDocumentPartContentRecord, byte[]> CONTENT = DOCUMENT_CONTENT.CONTENT;
	private static final TableField<ObjDocumentPartContentRecord, Integer> CREATED_BY_USER_ID = DOCUMENT_CONTENT.CREATED_BY_USER_ID;

	@Autowired
	//@formatter:off
	protected ObjDocumentRepositoryImpl(
		final AppContext appContext,
		final DSLContext dslContext,
		final ObjPartTransitionRepository transitionRepository,
		final ObjPartItemRepository itemRepository,
		final ObjNoteRepository noteRepository
	) {
		super(
			ObjDocumentRepository.class,
			ObjDocument.class,
			ObjDocumentBase.class,
			ITEM_TYPE,
			appContext,
			dslContext,
			transitionRepository,
			itemRepository,
			noteRepository
		);
	}
	//@formatter:on

	@Override
	@PostConstruct
	public void registerPartRepositories() {
		super.registerPartRepositories();
	}

	@Override
	protected String getAccountIdField() {
		return "account_id";
	}

	@Override
	public ObjDocument doCreate(SessionInfo sessionInfo) {
		return this.doCreate(sessionInfo, this.getDSLContext().newRecord(Tables.OBJ_DOCUMENT));
	}

	@Override
	public ObjDocument doLoad(SessionInfo sessionInfo, Integer objId) {
		requireThis(objId != null, "objId not null");
		ObjDocumentRecord documentRecord = this.getDSLContext().fetchOne(Tables.OBJ_DOCUMENT,
				Tables.OBJ_DOCUMENT.OBJ_ID.eq(objId));
		if (documentRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return this.doLoad(sessionInfo, objId, documentRecord);
	}

	@Override
	public List<ObjDocumentVRecord> doFind(SessionInfo sessionInfo, QuerySpec querySpec) {
		return this.doFind(sessionInfo, Tables.OBJ_DOCUMENT_V, Tables.OBJ_DOCUMENT_V.ID, querySpec);
	}

	@Override
	public CodeContentType getContentType(ObjDocument document) {
		Table<ObjDocumentPartContentRecord> query = this.getMaxContentVersionQuery(document);
		Integer maxVersionNr = this.getDSLContext().fetchValue(this.getMaxVersionQuery(document));
		if (maxVersionNr == null) {
			return null;
		}
		String contentTypeId = this.getDSLContext().fetchOne(query).getContentTypeId();
		return CodeContentTypeEnum.getContentType(contentTypeId);
	}

	@Override
	public byte[] getContent(ObjDocument document) {
		Table<ObjDocumentPartContentRecord> query = this.getMaxContentVersionQuery(document);
		return this.getDSLContext().fetchOne(query).getContent();
	}

	@Override
	public void storeContent(SessionInfo sessionInfo, ObjDocument document, CodeContentType contentType, byte[] content) {
		Integer maxVersionNr = this.getDSLContext().fetchValue(this.getMaxVersionQuery(document));
		maxVersionNr = maxVersionNr == null ? 1 : maxVersionNr + 1;
		//@formatter:off
		this.getDSLContext()
			.insertInto(DOCUMENT_CONTENT)
			.columns(OBJ_ID, VERSION_NR, CONTENT_TYPE_ID, CONTENT, CREATED_BY_USER_ID)
			.values(document.getId(), maxVersionNr, contentType.getId(), content, sessionInfo.getUser().getId())
			.execute();
		//@formatter:on
	}

	private Table<ObjDocumentPartContentRecord> getMaxContentVersionQuery(ObjDocument document) {
		SelectConditionStep<Record1<Integer>> maxVersionQuery = this.getMaxVersionQuery(document);
		return DOCUMENT_CONTENT.where(OBJ_ID.eq(document.getId()).and(VERSION_NR.eq(maxVersionQuery)));
	}

	private SelectConditionStep<Record1<Integer>> getMaxVersionQuery(ObjDocument document) {
		return this.getDSLContext().select(DSL.max(VERSION_NR)).from(DOCUMENT_CONTENT).where(OBJ_ID.eq(document.getId()));
	}

}
