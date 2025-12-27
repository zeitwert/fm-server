package io.zeitwert.fm.server.config.security;

import io.zeitwert.fm.oe.model.ObjUser;
import io.zeitwert.fm.oe.model.ObjUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ZeitwertUserDetailsService implements UserDetailsService {

	@Autowired
	ObjUserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		ObjUser user = this.userRepository.getByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
		return ZeitwertUserDetails.build(user);
	}

}
