
package io.zeitwert.server.config.security;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.base.ObjUserBase;
import io.zeitwert.ddd.oe.model.enums.CodeUserRoleEnum;

public class ZeitwertUserDetails implements UserDetails {

	private static final long serialVersionUID = 1L;

	private Integer userId;
	private String userEmail;
	private String userPassword;
	private Integer tenantId;
	private Integer accountId;

	private Collection<? extends GrantedAuthority> authorities;

	public static ZeitwertUserDetails build(ObjUser user) {
		List<SimpleGrantedAuthority> authorities = List
				.of(user.getRole() == null ? null : new SimpleGrantedAuthority(user.getRole().getId()));
		return new ZeitwertUserDetails(user, authorities);
	}

	public ZeitwertUserDetails(ObjUser user, Collection<? extends GrantedAuthority> authorities) {
		this.userId = user.getId();
		this.userEmail = user.getEmail();
		this.userPassword = ((ObjUserBase) user).getPassword();
		this.authorities = authorities;
	}

	public Integer getUserId() {
		return this.userId;
	}

	@Override
	public String getUsername() {
		return this.userEmail;
	}

	@Override
	public String getPassword() {
		return this.userPassword;
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
		return authorities;
	}

	public boolean isAdmin() {
		return authorities.stream().anyMatch((a) -> CodeUserRoleEnum.ADMIN.getId().equals(a.getAuthority()));
	}

	public boolean isAppAdmin() {
		return authorities.stream().anyMatch((a) -> CodeUserRoleEnum.APP_ADMIN.getId().equals(a.getAuthority()));
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
		ZeitwertUserDetails user = (ZeitwertUserDetails) o;
		return Objects.equals(this.userId, user.getUserId());
	}

}