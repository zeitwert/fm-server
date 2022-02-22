
package fm.comunas.ddd.session.model.impl;

import fm.comunas.ddd.oe.model.ObjUser;
import fm.comunas.ddd.oe.model.base.ObjUserBase;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserDetailsImpl implements UserDetails {

	private static final long serialVersionUID = 1L;

	private Integer id;

	private String email;

	@JsonIgnore
	private String password;

	private String tenant;

	private Collection<? extends GrantedAuthority> authorities;

	public UserDetailsImpl(Integer id, String email, String password, String tenant,
			Collection<? extends GrantedAuthority> authorities) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.tenant = tenant;
		this.authorities = authorities;
	}

	public static UserDetailsImpl build(ObjUser user) {
		List<SimpleGrantedAuthority> authorities = user.getRoleList().stream()
				.map(role -> new SimpleGrantedAuthority(role.getId())).toList();
		return new UserDetailsImpl(user.getId(), user.getEmail(), ((ObjUserBase) user).getPassword(),
				user.getTenant().getExtlKey(), authorities);
	}

	public Integer getId() {
		return this.id;
	}

	@Override
	public String getUsername() {
		return this.email;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public String getEmail() {
		return this.email;
	}

	public String getTenant() {
		return this.tenant;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserDetailsImpl user = (UserDetailsImpl) o;
		return Objects.equals(id, user.id);
	}

}