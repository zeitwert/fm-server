package io.zeitwert.fm.obj.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import org.jooq.DSLContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.jooq.persistence.ObjPersistenceProviderBase;

@Configuration("objPersistenceProvider")
@DependsOn("codePartListTypeEnum")
public class ObjVPersistenceProvider extends ObjPersistenceProviderBase<Obj> {

	public ObjVPersistenceProvider(DSLContext dslContext) {
		super(Obj.class, dslContext);
	}

	@Override
	public Obj doCreate() {
		return this.doCreate(null);
	}

	@Override
	public Obj doLoad(Integer objId) {
		requireThis(objId != null, "objId not null");
		return this.doLoad(objId, null);
	}

}
