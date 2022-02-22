
package io.zeitwert.ddd.obj.service.api.impl;

import io.zeitwert.ddd.obj.service.api.ObjService;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("objService")
public class ObjServiceImpl implements ObjService {

	@Autowired
	ObjServiceImpl(final DSLContext dslContext) {
	}

}
