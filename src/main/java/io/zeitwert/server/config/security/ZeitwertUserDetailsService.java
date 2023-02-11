
package io.zeitwert.server.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import io.zeitwert.ddd.oe.service.api.ObjUserCache;
import io.zeitwert.fm.oe.model.ObjUserFM;

@Service
public class ZeitwertUserDetailsService implements UserDetailsService {

	@Autowired
	ObjUserCache userCache;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		ObjUserFM user = (ObjUserFM) this.userCache.getByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
		return ZeitwertUserDetails.build(user);
	}

}
