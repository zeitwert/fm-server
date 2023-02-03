package io.zeitwert.fm.obj.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import org.jooq.DSLContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.persistence.jooq.base.ObjPersistenceProviderBase;
import io.zeitwert.fm.obj.model.ObjVRepository;
import io.zeitwert.fm.obj.model.base.ObjVBase;

@Configuration("objPersistenceProvider")
@DependsOn("codePartListTypeEnum")
public class ObjVPersistenceProvider extends ObjPersistenceProviderBase<Obj> {

	public ObjVPersistenceProvider(DSLContext dslContext) {
		super(ObjVRepository.class, ObjVBase.class, dslContext);
	}

	@Override
	public Class<?> getEntityClass() {
		return Obj.class;
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
