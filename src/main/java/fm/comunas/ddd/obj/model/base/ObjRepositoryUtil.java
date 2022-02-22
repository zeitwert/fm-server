
package fm.comunas.ddd.obj.model.base;

import org.jooq.Field;
import org.jooq.Table;
import org.jooq.impl.DSL;

import fm.comunas.ddd.aggregate.model.base.AggregateRepositoryDbUtil;
import fm.comunas.ddd.app.service.api.AppContext;

public class ObjRepositoryUtil extends AggregateRepositoryDbUtil {

	private static final Table<?> PART_LIST_TABLE = AppContext.getInstance().getSchema().getTable("obj_part_list");

	private static final Field<Integer> AGGREGATE_ID = DSL.field("obj_id", Integer.class);

	private static ObjRepositoryUtil INSTANCE;

	public static ObjRepositoryUtil getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ObjRepositoryUtil();
		}
		return INSTANCE;
	}

	protected Table<?> getPartListTable() {
		return PART_LIST_TABLE;
	}

	protected Field<Integer> getPartListAggregateId() {
		return AGGREGATE_ID;
	}

}
