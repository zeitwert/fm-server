package io.zeitwert.config.dsl

import io.zeitwert.config.DelegatingSessionContext
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
 *     adminUser("admin@zeitwert.io", "Admin", "admin", "demo") {
 *         user("user@zeitwert.io", "User", "user", "demo")
 *         account("ACC", "Account Name", "client") {
 *             contact(...)
 *         }
 *     }
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
		val context = TenantContext(key, name, type)

		// Create or get tenant first
		val tenantId = createOrGetTenant(context)

		// Set tenant ID in session context for nested operations
		DelegatingSessionContext.setSetupTenantId(tenantId)

		// Now execute the DSL block with tenant context set
		context.init()

		// Process any simple users (non-admin)
		context.users.forEach { userCtx ->
			val tenant = tenantRepository.get(tenantId)
			createOrGetUser(tenant, userCtx)
		}

		return tenantId
	}

	private fun createOrGetTenant(ctx: TenantContext): Int {
		// Check if tenant already exists
		val existingTenant = tenantRepository.getByKey(ctx.key)

		if (existingTenant.isPresent) {
			val tenant = existingTenant.get()
			val tenantId = tenant.id as Int
			println("    Tenant ${ctx.key} already exists (id=$tenantId)")
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

		return tenantId
	}

	internal fun createOrGetUser(
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

	/**
	 * Create a simple user without nested context.
	 * Use this for users that don't need to create accounts or other objects.
	 */
	fun user(
		email: String,
		name: String,
		role: String,
		password: String,
	) {
		users += UserContext(email, name, role, password)
	}

	/**
	 * Create an admin user with a nested context for creating additional users and accounts.
	 * The admin user's ID will be set in the session context for all nested operations.
	 */
	fun adminUser(
		email: String,
		name: String,
		role: String,
		password: String,
		init: AdminUserContext.() -> Unit,
	) {
		val tenantId = DelegatingSessionContext.getSetupTenantId()
			?: throw IllegalStateException("Tenant ID not set in session context")
		val tenant = Tenant.tenantRepository.get(tenantId)

		// Create the admin user
		val userId = Tenant.createOrGetUser(tenant, UserContext(email, name, role, password))

		// Set user ID in session context for nested operations
		DelegatingSessionContext.setSetupUserId(userId)

		// Execute nested block with user context set
		AdminUserContext(tenant).init()
	}
}

@TenantDslMarker
class UserContext(
	val email: String,
	val name: String,
	val role: String,
	val password: String,
)

/**
 * Context for admin user that allows creating additional users and accounts.
 */
@TenantDslMarker
class AdminUserContext(
	private val tenant: ObjTenant,
) {

	/**
	 * Create a simple user within this admin context.
	 */
	fun user(
		email: String,
		name: String,
		role: String,
		password: String,
	) {
		Tenant.createOrGetUser(tenant, UserContext(email, name, role, password))
	}

	/**
	 * Create an account with optional nested contacts.
	 */
	fun account(
		key: String,
		name: String,
		accountType: String,
		init: AccountContext.() -> Unit = {},
	): Int {
		return Account(key, name, accountType, init)
	}
}
