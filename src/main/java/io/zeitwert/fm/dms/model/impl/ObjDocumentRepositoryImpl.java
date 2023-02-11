
package io.zeitwert.fm.dms.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.exception.NoDataFoundException;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.session.model.RequestContext;
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
import io.zeitwert.jooq.persistence.AggregateState;
import io.zeitwert.jooq.repository.JooqObjExtnRepositoryBase;

@Component("objDocumentRepository")
public class ObjDocumentRepositoryImpl extends JooqObjExtnRepositoryBase<ObjDocument, ObjDocumentVRecord>
		implements ObjDocumentRepository {

	private static final String AGGREGATE_TYPE = "obj_document";

	private static final ObjDocumentPartContent DOCUMENT_CONTENT = Tables.OBJ_DOCUMENT_PART_CONTENT;
	private static final TableField<ObjDocumentPartContentRecord, Integer> OBJ_ID = DOCUMENT_CONTENT.OBJ_ID;
	private static final TableField<ObjDocumentPartContentRecord, Integer> VERSION_NR = DOCUMENT_CONTENT.VERSION_NR;
	private static final TableField<ObjDocumentPartContentRecord, String> CONTENT_TYPE_ID = DOCUMENT_CONTENT.CONTENT_TYPE_ID;
	private static final TableField<ObjDocumentPartContentRecord, byte[]> CONTENT = DOCUMENT_CONTENT.CONTENT;
	private static final TableField<ObjDocumentPartContentRecord, Integer> CREATED_BY_USER_ID = DOCUMENT_CONTENT.CREATED_BY_USER_ID;

	protected ObjDocumentRepositoryImpl(AppContext appContext, DSLContext dslContext) {
		super(ObjDocumentRepository.class, ObjDocument.class, ObjDocumentBase.class, AGGREGATE_TYPE, appContext,
				dslContext);
	}

	@Override
	public void mapProperties() {
		super.mapProperties();
		this.mapField("name", AggregateState.EXTN, "name", String.class);
		this.mapField("documentKind", AggregateState.EXTN, "document_kind_id", String.class);
		this.mapField("documentCategory", AggregateState.EXTN, "document_category_id", String.class);
		this.mapField("templateDocument", AggregateState.EXTN, "template_document_id", Integer.class);
		this.mapField("contentKind", AggregateState.EXTN, "content_kind_id", String.class);
	}

	@Override
	public ObjDocument doCreate() {
		return this.doCreate(this.dslContext().newRecord(Tables.OBJ_DOCUMENT));
	}

	@Override
	public ObjDocument doLoad(Integer objId) {
		requireThis(objId != null, "objId not null");
		ObjDocumentRecord documentRecord = this.dslContext().fetchOne(Tables.OBJ_DOCUMENT,
				Tables.OBJ_DOCUMENT.OBJ_ID.eq(objId));
		if (documentRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return this.doLoad(objId, documentRecord);
	}

	@Override
	public List<ObjDocumentVRecord> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.OBJ_DOCUMENT_V, Tables.OBJ_DOCUMENT_V.ID, querySpec);
	}

	@Override
	public CodeContentType getContentType(ObjDocument document) {
		Integer maxVersionNr = this.dslContext().fetchValue(this.getContentMaxVersionQuery(document));
		if (maxVersionNr == null) {
			return null;
		}
		Table<ObjDocumentPartContentRecord> query = this.getContentWithMaxVersionQuery(document);
		String contentTypeId = this.dslContext().fetchOne(query).getContentTypeId();
		return CodeContentTypeEnum.getContentType(contentTypeId);
	}

	@Override
	public byte[] getContent(ObjDocument document) {
		Table<ObjDocumentPartContentRecord> query = this.getContentWithMaxVersionQuery(document);
		return this.dslContext().fetchOne(query).getContent();
	}

	@Override
	public void storeContent(RequestContext requestCtx, ObjDocument document, CodeContentType contentType,
			byte[] content) {
		Integer versionNr = this.dslContext().fetchValue(this.getContentMaxVersionQuery(document));
		versionNr = versionNr == null ? 1 : versionNr + 1;
		this.dslContext()
				.insertInto(DOCUMENT_CONTENT)
				.columns(OBJ_ID, VERSION_NR, CONTENT_TYPE_ID, CONTENT, CREATED_BY_USER_ID)
				.values(document.getId(), versionNr, contentType.getId(), content, requestCtx.getUser().getId())
				.execute();
		this.store(document); // modifiedBy, trigger event
	}

	private Table<ObjDocumentPartContentRecord> getContentWithMaxVersionQuery(ObjDocument document) {
		SelectConditionStep<Record1<Integer>> maxVersionQuery = this.getContentMaxVersionQuery(document);
		return DOCUMENT_CONTENT.where(OBJ_ID.eq(document.getId()).and(VERSION_NR.eq(maxVersionQuery)));
	}

	private SelectConditionStep<Record1<Integer>> getContentMaxVersionQuery(ObjDocument document) {
		return this.dslContext().select(DSL.max(VERSION_NR)).from(DOCUMENT_CONTENT)
				.where(OBJ_ID.eq(document.getId()));
	}

}
