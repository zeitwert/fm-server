package io.zeitwert.fm.server.config.jooq;

import org.jooq.ExecuteContext;
import org.jooq.impl.DefaultExecuteListener;

public class ExecuteListener extends DefaultExecuteListener {

	@Override
	public void start(ExecuteContext ctx) {
		String sql = (ctx.query() != null ? ctx.query().toString().replace("\n", " ") : "")
				+ (ctx.sql() != null ? ctx.sql().toString().replace("\n", " ") : "");
		int fromPos = sql.indexOf(" from ");
		int orderByPos = sql.indexOf(" order by ");
		String fromStmt = fromPos >= 0
				? orderByPos >= 0 ? sql.substring(fromPos + 6, orderByPos) : sql.substring(fromPos + 6)
				: "";
		System.out.println("DB " + ctx.type() + ": " + fromStmt.replace("public.", ""));
	}

}
