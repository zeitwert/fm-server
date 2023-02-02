package io.zeitwert.fm.test.model.impl;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.base.ObjPartRepositoryBase;
import io.zeitwert.fm.test.model.ObjTest;
import io.zeitwert.fm.test.model.ObjTestPartNode;
import io.zeitwert.fm.test.model.ObjTestPartNodeRepository;
import io.zeitwert.fm.test.model.base.ObjTestPartNodeBase;

@Component("testPartNodeRepository")
public class ObjTestPartNodeRepositoryImpl extends ObjPartRepositoryBase<ObjTest, ObjTestPartNode>
		implements ObjTestPartNodeRepository {

	private static final String PART_TYPE = "obj_test_part_node";

	protected ObjTestPartNodeRepositoryImpl(final AppContext appContext, final DSLContext dslContext) {
		super(ObjTest.class, ObjTestPartNode.class, ObjTestPartNodeBase.class, PART_TYPE, appContext, dslContext);
	}

	@Override
	public ObjTestPartNode doCreate(ObjTest obj) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<ObjTestPartNode> doLoad(ObjTest obj) {
		throw new UnsupportedOperationException();
	}

}
