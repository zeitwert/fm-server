
package io.zeitwert.fm.oe.model.impl;

import java.util.List;
import java.util.Optional;

import org.jooq.DSLContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.FilterOperator;
import io.crnk.core.queryspec.PathSpec;
import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.base.ObjFields;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.base.ObjUserRepositoryBase;
import io.zeitwert.fm.oe.model.db.Tables;
import io.zeitwert.fm.oe.model.db.tables.records.ObjUserVRecord;
import io.zeitwert.jooq.repository.JooqAggregateFinderMixin;
import io.zeitwert.jooq.util.SqlUtils;

@Component("objUserRepository")
public class ObjUserRepositoryImpl extends ObjUserRepositoryBase implements JooqAggregateFinderMixin<Object> {

	private final DSLContext dslContext;

	// passwordEncoder: break cycle from WebSecurityConfig TODO find better solution
	// (own class)
	protected ObjUserRepositoryImpl(AppContext appContext, DSLContext dslContext, @Lazy PasswordEncoder passwordEncoder) {
		super(appContext, passwordEncoder);
		this.dslContext = dslContext;
	}

	@Override
	public boolean hasAccountId() {
		return false;
	}

	@Override
	public DSLContext dslContext() {
		return this.dslContext;
	}

	@Override
	public final List<Object> find(QuerySpec querySpec) {
		querySpec = this.queryWithFilter(querySpec);
		if (!SqlUtils.hasFilterFor(querySpec, "isClosed")) {
			querySpec.addFilter(PathSpec.of(ObjFields.CLOSED_AT.getName()).filter(FilterOperator.EQ, null));
		}
		return this.doFind(querySpec);
	}

	@Override
	public List<Object> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.OBJ_USER_V, Tables.OBJ_USER_V.ID, querySpec);
	}

	@Override
	public Optional<ObjUser> getByEmail(String email) {
		ObjUserVRecord userRecord = AppContext.getInstance().getDslContext().fetchOne(Tables.OBJ_USER_V,
				Tables.OBJ_USER_V.EMAIL.eq(email));
		if (userRecord == null) {
			return Optional.empty();
		}
		return Optional.of(this.get(userRecord.getId()));
	}

}
