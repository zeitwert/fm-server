package io.zeitwert.config.dsl

import io.zeitwert.fm.oe.model.ObjTenant
import io.zeitwert.fm.oe.model.ObjTenantRepository
import io.zeitwert.fm.oe.model.ObjUserRepository
import io.zeitwert.fm.oe.model.enums.CodeTenantType
import io.zeitwert.fm.oe.model.enums.CodeUserRole

/**
 * DSL for creating tenants with nested users using Spring repositories.
 *
 * This DSL uses the repository layer to create tenants and users, which ensures proper domain logic
 * is executed (e.g., creating logo images, avatar images, etc.).
 *
 * Usage:
 * ```
 * Tenant.init(tenantRepository, userRepository)
 * Tenant("demo", "Demo", "advisor") {
 *     user("admin@zeitwert.io", "Admin", "admin", "demo")
 *     user("user@zeitwert.io", "User", "user", "demo")
 * }
 * ```
 */
object Tenant {

	lateinit var tenantRepository: ObjTenantRepository
	lateinit var userRepository: ObjUserRepository

	fun init(
		tenantRepo: ObjTenantRepository,
		userRepo: ObjUserRepository,
	) {
		tenantRepository = tenantRepo
		userRepository = userRepo
	}

	operator fun invoke(
		key: String,
		name: String,
		type: String,
		init: TenantContext.() -> Unit,
	): Int {
		val context = TenantContext(key, name, type).apply(init)
		return createOrGetTenant(context)
	}

	private fun createOrGetTenant(ctx: TenantContext): Int {
		// Check if tenant already exists
		val existingTenant = tenantRepository.getByKey(ctx.key)

		if (existingTenant.isPresent) {
			val tenant = existingTenant.get()
			val tenantId = tenant.id as Int
			println("    Tenant ${ctx.key} already exists (id=$tenantId)")
			// Still create users that don't exist
			ctx.users.forEach { userCtx -> createOrGetUser(tenant, userCtx) }
			return tenantId
		}

		// Create new tenant via repository
		val tenant = tenantRepository.create()
		tenant.key = ctx.key
		tenant.name = ctx.name
		tenant.tenantType = CodeTenantType.getTenantType(ctx.type)
		tenantRepository.store(tenant)

		val tenantId = tenant.id as Int
		println("    Created tenant ${ctx.key} - ${ctx.name} (id=$tenantId)")

		// Create users for this tenant
		ctx.users.forEach { userCtx -> createOrGetUser(tenant, userCtx) }

		return tenantId
	}

	private fun createOrGetUser(
		tenant: ObjTenant,
		ctx: UserContext,
	): Int {
		// Check if user already exists
		val existingUser = userRepository.getByEmail(ctx.email)

		if (existingUser.isPresent) {
			val userId = existingUser.get().id as Int
			println("      User ${ctx.email} already exists (id=$userId)")
			return userId
		}

		// Create new user via repository
		val user = userRepository.create()
		user.email = ctx.email
		user.name = ctx.name
		user.role = CodeUserRole.getUserRole(ctx.role)
		user.password = userRepository.passwordEncoder.encode(ctx.password)

		// Associate user with tenant
		user.tenantSet.add(tenant.id)

		userRepository.store(user)

		val userId = user.id as Int
		println("      Created user ${ctx.email} - ${ctx.name} (id=$userId)")

		return userId
	}
}

@DslMarker
annotation class TenantDslMarker

@TenantDslMarker
class TenantContext(
	val key: String,
	val name: String,
	val type: String,
) {

	internal val users = mutableListOf<UserContext>()

	fun user(
		email: String,
		name: String,
		role: String,
		password: String,
	) {
		users += UserContext(email, name, role, password)
	}
}

@TenantDslMarker
class UserContext(
	val email: String,
	val name: String,
	val role: String,
	val password: String,
)
