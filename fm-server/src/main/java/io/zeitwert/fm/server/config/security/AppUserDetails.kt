package io.zeitwert.fm.server.config.security

import io.zeitwert.fm.oe.model.ObjUser
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class AppUserDetails(
	private val user: ObjUser,
	private val authorities: List<GrantedAuthority>,
) : UserDetails {

	override fun getUsername(): String? = this.user.email

	override fun getPassword(): String? = this.user.password

	override fun getAuthorities(): MutableCollection<out GrantedAuthority> = this.authorities.toMutableList()

	override fun isAccountNonExpired(): Boolean = true

	override fun isAccountNonLocked(): Boolean = true

	override fun isCredentialsNonExpired(): Boolean = true

	override fun isEnabled(): Boolean = true

	val tenantId: Any get() = _tenantId!!

	var _tenantId: Any? = null

	var accountId: Any? = null

	val userId: Any
		get() = this.user.id as Int

	val isAppAdmin: Boolean
		get() = this.user.isAppAdmin

	val isAdmin: Boolean
		get() = this.user.isAdmin

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other == null || this.javaClass != other.javaClass) return false
		val user = other as AppUserDetails
		return this.user.id == user.userId
	}

	override fun hashCode(): Int {
		var result = tenantId as Int
		result = 31 * result + (accountId as Int? ?: 0)
		result = 31 * result + user.hashCode()
		result = 31 * result + authorities.hashCode()
		result = 31 * result + userId as Int
		result = 31 * result + isAppAdmin.hashCode()
		result = 31 * result + isAdmin.hashCode()
		return result
	}

	companion object {

		@JvmStatic
		fun build(user: ObjUser): AppUserDetails {
			val authorities = if (user.role == null) emptyList() else listOf(SimpleGrantedAuthority(user.role!!.id))
			return AppUserDetails(user, authorities)
		}
	}

}
