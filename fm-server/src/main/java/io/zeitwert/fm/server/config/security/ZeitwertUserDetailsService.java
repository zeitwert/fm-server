
package io.zeitwert.fm.server.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import io.zeitwert.fm.oe.model.ObjUserFM;
import io.zeitwert.fm.oe.model.ObjUserFMRepository;

@Service
public class ZeitwertUserDetailsService implements UserDetailsService {

	@Autowired
	ObjUserFMRepository userCache;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		ObjUserFM user = (ObjUserFM) this.userCache.getByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
		return ZeitwertUserDetails.build(user);
	}

}
