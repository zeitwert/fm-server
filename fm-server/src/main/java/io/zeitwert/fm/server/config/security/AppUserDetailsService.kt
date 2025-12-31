package io.zeitwert.fm.server.config.security

import io.zeitwert.fm.oe.model.ObjUserRepository
import io.zeitwert.fm.server.config.security.AppUserDetails.Companion.build
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.function.Supplier

@Service
class AppUserDetailsService : UserDetailsService {

	@Autowired
	lateinit var userRepository: ObjUserRepository

	@Throws(UsernameNotFoundException::class)
	override fun loadUserByUsername(email: String): UserDetails {
		val user = userRepository
			.getByEmail(email)
			.orElseThrow(Supplier { UsernameNotFoundException("User not found: $email") })
		return build(user)
	}

}
