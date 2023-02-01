
package io.zeitwert.fm.oe.model.impl;

import static io.zeitwert.ddd.util.Check.assertThis;

import java.util.List;
import java.util.Optional;

import org.jooq.DSLContext;
import org.jooq.TableRecord;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.base.ObjUserRepositoryBase;
import io.zeitwert.fm.oe.model.db.Tables;
import io.zeitwert.fm.oe.model.db.tables.records.ObjUserVRecord;

@Component("objUserRepository")
public class ObjUserRepositoryImpl extends ObjUserRepositoryBase {

	// passwordEncoder: break cycle from WebSecurityConfig TODO find better solution
	// (own class)
	protected ObjUserRepositoryImpl(AppContext appContext, DSLContext dslContext, @Lazy PasswordEncoder passwordEncoder) {
		super(appContext, dslContext, passwordEncoder);
	}

	@Override
	public ObjUser doCreate() {
		assertThis(false, "nope");
		return null;
	}

	@Override
	public ObjUser doLoad(Integer id) {
		assertThis(false, "nope");
		return null;
	}

	@Override
	public List<TableRecord<?>> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.OBJ_USER_V, Tables.OBJ_USER_V.ID, querySpec);
	}

	@Override
	public Optional<ObjUser> getByEmail(String email) {
		ObjUserVRecord userRecord = this.getDSLContext().fetchOne(Tables.OBJ_USER_V, Tables.OBJ_USER_V.EMAIL.eq(email));
		if (userRecord == null) {
			return Optional.empty();
		}
		return Optional.of(this.get(userRecord.getId()));
	}

}
