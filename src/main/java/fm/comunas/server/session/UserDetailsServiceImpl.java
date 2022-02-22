package fm.comunas.server.session;

import fm.comunas.ddd.oe.model.ObjUser;
import fm.comunas.ddd.oe.model.ObjUserRepository;
import fm.comunas.ddd.session.model.impl.UserDetailsImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	ObjUserRepository userRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		ObjUser user = userRepository.getByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
		return UserDetailsImpl.build(user);
	}

}
