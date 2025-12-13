
package io.zeitwert.fm.server.config.security;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import io.zeitwert.fm.oe.model.ObjUserFM;

public class ZeitwertUserDetails implements UserDetails {

	private static final long serialVersionUID = 1L;

	private ObjUserFM user;
	private Integer tenantId;
	private Integer accountId;

	private Collection<? extends GrantedAuthority> authorities;

	public static ZeitwertUserDetails build(ObjUserFM user) {
		List<SimpleGrantedAuthority> authorities = List
				.of(user.getRole() == null ? null : new SimpleGrantedAuthority(user.getRole().getId()));
		return new ZeitwertUserDetails(user, authorities);
	}

	public ZeitwertUserDetails(ObjUserFM user, Collection<? extends GrantedAuthority> authorities) {
		this.user = user;
		this.authorities = authorities;
	}

	public Integer getUserId() {
		return (Integer) this.user.getId();
	}

	@Override
	public String getUsername() {
		return this.user.getEmail();
	}

	@Override
	public String getPassword() {
		return this.user.getPassword();
	}

	public Integer getTenantId() {
		return this.tenantId;
	}

	public void setTenantId(Integer tenantId) {
		this.tenantId = tenantId;
	}

	public Integer getAccountId() {
		return this.accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	public boolean isAppAdmin() {
		return this.user.isAppAdmin();
	}

	public boolean isAdmin() {
		return this.user.isAdmin();
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
		if (o == null || this.getClass() != o.getClass())
			return false;
		ZeitwertUserDetails user = (ZeitwertUserDetails) o;
		return Objects.equals(this.user.getId(), user.getUserId());
	}

}
