package io.zeitwert.server.config.security;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.oe.model.db.Tables;

@Component
public class PasswordMigration {

	static final PasswordEncoder pwdEncoder = new BCryptPasswordEncoder();

	@Autowired
	DSLContext dbContext;

	@PostConstruct
	public void init() {
		System.out.println("Password Migration");
		for (Record user : this.dbContext.selectFrom("obj_user_v").where("password like '{noop}%'").fetch()) {
			Integer id = ((Integer) user.get("id"));
			String email = ((String) user.get("email"));
			String pwd = ((String) user.get("password")).substring(6); // remove {noop}
			String newPwd = pwdEncoder.encode(pwd);
			System.out.println(email + " (" + id + "): " + pwd + " => " + newPwd);
			this.dbContext.update(Tables.OBJ_USER)
					.set(Tables.OBJ_USER.PASSWORD, newPwd)
					.where(Tables.OBJ_USER.OBJ_ID.eq(id))
					.execute();
		}
	}

}
