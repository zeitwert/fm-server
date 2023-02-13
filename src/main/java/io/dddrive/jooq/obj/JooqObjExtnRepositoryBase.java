package io.dddrive.jooq.obj;

import java.util.HashMap;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.TableRecord;
import org.springframework.beans.factory.annotation.Autowired;

import io.dddrive.ddd.model.Aggregate;
import io.dddrive.ddd.model.AggregateRepository;
import io.dddrive.obj.model.Obj;
import io.dddrive.obj.model.base.ObjRepositoryBase;

public abstract class JooqObjExtnRepositoryBase<O extends Obj, V extends TableRecord<?>>
		extends ObjRepositoryBase<O, V>
		implements ObjExtnPropertyProviderMixin {

	private DSLContext dslContext;
	private final Map<String, Object> dbConfigMap = new HashMap<>();

	public JooqObjExtnRepositoryBase(
			Class<? extends AggregateRepository<O, V>> repoIntfClass,
			Class<? extends Obj> intfClass,
			Class<? extends Obj> baseClass,
			String aggregateTypeId) {
		super(repoIntfClass, intfClass, baseClass, aggregateTypeId);
		this.mapProperties();
	}

	@Autowired
	protected void setDSLContext(DSLContext dslContext) {
		this.dslContext = dslContext;
	}

	@Override
	public final Class<? extends Aggregate> getEntityClass() {
		return this.getAggregateClass();
	}

	@Override
	public final Map<String, Object> dbConfigMap() {
		return this.dbConfigMap;
	}

	public final DSLContext dslContext() {
		return this.dslContext;
	}

}
