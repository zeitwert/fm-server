package io.zeitwert.fm.server.config.security

import io.zeitwert.fm.oe.model.db.Tables
import jakarta.annotation.PostConstruct
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class PasswordMigration {

	@Autowired
	lateinit var dbContext: DSLContext

	val pwdEncoder: PasswordEncoder = BCryptPasswordEncoder()

	@PostConstruct
	fun init() {
		println("Password Migration")
		dbContext
			.selectFrom(Tables.OBJ_USER_V)
			.where(Tables.OBJ_USER_V.PASSWORD.like("{noop}%"))
			.fetch()
			.forEach { user ->
				val pwd = (user.get("password") as String).substring(6) // remove {noop}
				val newPwd: String? = pwdEncoder.encode(pwd)
				println(user.email + " (" + user.id + "): " + pwd + " => " + newPwd)
				dbContext
					.update(Tables.OBJ_USER)
					.set(Tables.OBJ_USER.PASSWORD, newPwd)
					.where(Tables.OBJ_USER.OBJ_ID.eq(user.id))
					.execute()
			}
	}

}
