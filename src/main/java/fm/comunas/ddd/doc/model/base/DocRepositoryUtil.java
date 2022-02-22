
package fm.comunas.ddd.doc.model.base;

import org.jooq.Field;
import org.jooq.Table;
import org.jooq.impl.DSL;

import fm.comunas.ddd.aggregate.model.base.AggregateRepositoryDbUtil;
import fm.comunas.ddd.app.service.api.AppContext;

public class DocRepositoryUtil extends AggregateRepositoryDbUtil {

	private static final Table<?> PART_LIST_TABLE = AppContext.getInstance().getSchema().getTable("doc_part_item");

	private static final Field<Integer> AGGREGATE_ID = DSL.field("doc_id", Integer.class);

	private static DocRepositoryUtil INSTANCE;

	public static DocRepositoryUtil getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new DocRepositoryUtil();
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
