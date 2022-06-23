
package io.zeitwert.ddd.obj.service.api.impl;

import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import io.zeitwert.ddd.obj.service.api.ObjService;

@Service("objService")
public class ObjServiceImpl implements ObjService {

	ObjServiceImpl(final DSLContext dslContext) {
	}

}
