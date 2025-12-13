package io.zeitwert.fm.ddd.model.impl;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.dddrive.ddd.IdProvider;
import io.zeitwert.fm.doc.model.db.Sequences;

@Component("idProvider")
public class IdProviderImpl implements IdProvider {

	private static Integer MIN_DOC_ID = 100000000; // doc_id_seq minvalue

	@Autowired
	DSLContext dslContext;

	@Override
	public Integer nextDocId() {
		return this.dslContext.nextval(Sequences.DOC_ID_SEQ).intValue();
	}

	@Override
	public boolean isDocId(Integer id) {
		return id != null && id >= MIN_DOC_ID;
	}

	@Override
	public Integer getOrderNr(Integer id) {
		return id;
	}

	@Override
	public Integer nextDocPartId() {
		return this.dslContext.nextval(Sequences.DOC_PART_ID_SEQ).intValue();
	}

	@Override
	public Integer nextObjId() {
		return this.dslContext.nextval(io.zeitwert.fm.obj.model.db.Sequences.OBJ_ID_SEQ).intValue();
	}

	@Override
	public boolean isObjId(Integer id) {
		return id != null && id < MIN_DOC_ID;
	}

	@Override
	public Integer nextObjPartId() {
		return this.dslContext.nextval(io.zeitwert.fm.obj.model.db.Sequences.OBJ_PART_ID_SEQ).intValue();
	}

}
